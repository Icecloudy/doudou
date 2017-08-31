package com.doudou.passenger.ui.profile.record;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bumptech.glide.Glide;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.OrderDriverBean;
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.main.CancelOrderActivity;
import com.doudou.passenger.ui.main.task.DrivingRouteOverLay;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.views.RoundAngleImageView;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.doudou.passenger.R.id.rating_star;

public class TripDetailsActivity extends BaseActivity implements HttpListener, RouteSearch.OnRouteSearchListener ,View.OnClickListener{


    @BindView(R.id.profile_image)
    RoundAngleImageView profileImage;
    @BindView(R.id.diverName)
    TextView diverName;
    @BindView(R.id.tvDriverType)
    TextView diverType;
    @BindView(rating_star)
    TextView ratingStar;
    @BindView(R.id.tvBillCount)
    TextView tvBillCount;
    @BindView(R.id.carNum)
    TextView carNum;
    @BindView(R.id.carType)
    TextView carType;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.layoutCallCar)
    LinearLayout layoutCallCar;
    @BindView(R.id.orderDate)
    TextView orderDate;
    @BindView(R.id.setOutLocation)
    TextView setOutLocation;
    @BindView(R.id.EsLocation)
    TextView EsLocation;
    @BindView(R.id.tvCancelContent)
    TextView tvCancelContent;
    @BindView(R.id.layoutCancel)
    LinearLayout layoutCancel;
    @BindView(R.id.layoutCost)
    RelativeLayout layoutCost;

    UserDataPreference userDataPreference;
    UserBean userBean;
    OrderRecordBean recordBean;
    @BindView(R.id.map)
    MapView mapView;
    private AMap aMap;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;

    //地图对象
    private Marker mStartMarker;
    private Marker mEndMarker;
    private int orderId;
    int state;
    String driverPhone;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_trip_details);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        init(savedInstanceState);

        if (getIntent().getExtras() != null) {
            orderId = getIntent().getExtras().getInt("orderId");
            getOrderRecord(orderId);
        }
    }


    /**
     * 初始化AMap对象
     */
    private void init(Bundle savedInstanceState) {
        mContext = this.getApplicationContext();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);

        // 初始化Marker添加到地图
        mStartMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.ic_start_marker))));
        mEndMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.ic_end_marker))));
    }


    @OnClick({R.id.imgCallDiver, R.id.imgCallDiverVoice,R.id.layoutCallCar,R.id.layoutCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCallDiver:
                showMessageDialog();
                break;
            case R.id.imgCallDiverVoice:
                break;
            case R.id.layoutCallCar:
                break;
            case R.id.layoutCancel:
                break;
        }
    }
    /**
     * Show message dialog.
     */
    public void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.nav_service);
        builder.setMessage("打电话司机");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + driverPhone));
                if (ActivityCompat.checkSelfPermission(TripDetailsActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int i) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);

                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);

                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    if (state==7){
                        drivingRouteOverlay.addStartAndEndMarker();
                    }else{
                        drivingRouteOverlay.addToMap();
                    }
                    drivingRouteOverlay.zoomToSpan();
                }
            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private static final int GET_ORDER_DETAILS = 0x002;

    /**
     * * @param int orderid 订单id
     * userid 用户id / 司机id
     */
    private void getOrderRecord(int id) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_TRIP_RECORD_DETAILS)
                .add("orderid", id)
                .add("usertype", 1)
                .add("userid", userBean.getId());
        request(GET_ORDER_DETAILS, baseRequest, this, false, true);

    }


    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_ORDER_DETAILS) {
            try {
                recordBean = JSON.parseObject(response, OrderRecordBean.class);
                setData(recordBean, recordBean.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    /**
     * TextView money;
     *
     * @param data
     * @param statue
     * @BindView(R.id.ratingBar) RatingBar ratingBar;
     * @BindView(R.id.layoutCallCar) LinearLayout layoutCallCar;
     * @BindView(R.id.orderDate) TextView orderDate;
     * @BindView(R.id.setOutLocation) TextView setOutLocation;
     * @BindView(R.id.EsLocation) TextView EsLocation;
     * @BindView(R.id.tvCancelContent) TextView tvCancelContent;
     * @BindView(R.id.layoutCancel) LinearLayout layoutCancel;
     */
    private void setData(OrderRecordBean data, int statue) {
        try {
            if (statue == 7) {//取消
                setCancelInfo(data);
                setTitle("行程结束");
            } else if (statue > 3) {//接单
                setDriverInfo(data.getDriver());
                setTitle("行程结束");
                money.setText(data.getPrice()+"");
            }else if (statue ==2||statue ==3) {//接单* status(int) 订单状态[1未接,2已接,3载客中,4已到达,5已结单,6已评价,7取消],
                setDriverInfo(data.getDriver());
                setTitle("等待司机接驾");
                setRightText(R.string.cancel_order, this);
                layoutCost.setVisibility(View.GONE);
            }else{
                setTitle("等待司机接驾");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        state = data.getStatus();
        mStartPoint = new LatLonPoint(data.getLatitude(), data.getLongitude());
        mEndPoint = new LatLonPoint(data.getLatitudel(), data.getLongitudel());
        mStartMarker.setPosition(new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()));
        mEndMarker.setPosition(new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()));
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }

    /**
     * 司机信息
     *
     * @param data
     */
    private void setDriverInfo(OrderDriverBean data) {
        layoutCallCar.setVisibility(View.VISIBLE);
        driverPhone = data.getPhone();
        Glide.with(this)
                .load(data.getPhoto())
                .placeholder(R.drawable.ic_head)
                .error(R.drawable.ic_head)
                .crossFade()
                .centerCrop()
                .into(profileImage);
        diverName.setText(TextUtils.isEmpty(data.getUsername()) ? "司机" : data.getUsername());
        ratingStar.setText(data.getScore() + "");
        tvBillCount.setText(data.getCountorders() + "单");
        carNum.setText(data.getCarnum());
        carType.setText( data.getBrand());

        if (data.getType()==1){
            diverType.setText("社会车");//车辆分类[1社会车,2自营专车,3高端车]
        }else if (data.getType()==2){
            diverType.setText("自营专车");//车辆分类[1社会车,2自营专车,3高端车]
        }else{
            diverType.setText("高端车");
        }

        ratingBar.setRating((float) data.getScore());
        ratingBar.setEnabled(false);
    }
    //后面还会有接送机 [1实时,2预约,3接机,4送机,5代叫]
    private void setCancelInfo(OrderRecordBean data) {
        layoutCancel.setVisibility(View.VISIBLE);
        if (data.getType()==1){
            orderDate.setText(data.getPlacetime());
        }else if (data.getType()==2){
            orderDate.setText(data.getBooktime());
        }
        setOutLocation.setText(data.getAddresses());
        EsLocation.setText(data.getDown());
        tvCancelContent.setText(data.getReason());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, CancelOrderActivity.class);
        intent.putExtra("orderId",orderId);
        startActivity(intent);
    }
}
