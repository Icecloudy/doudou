package com.doudou.passenger.ui.main.booking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.OrderDetailBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.main.DestinationActivity;
import com.doudou.passenger.ui.main.task.LocationTask;
import com.doudou.passenger.ui.main.task.RouteTask;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingCarActivity extends BaseActivity implements RouteTask.OnRouteCalculateListener,
        HttpListener, GeocodeSearch.OnGeocodeSearchListener {


    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.tab_record_title)
    TabLayout tabRecordTitle;
    @BindView(R.id.tvBookingTime)
    TextView tvBookingTime;
    @BindView(R.id.editSetOut)
    TextView editSetOut;
    @BindView(R.id.tvDestination)
    TextView tvDestination;
    @BindView(R.id.tvCarType)
    TextView tvCarType;
    @BindView(R.id.layoutCarType)
    RelativeLayout layoutCarType;
    @BindView(R.id.tvEstimatedCost)
    TextView tvEstimatedCost;
    @BindView(R.id.btnCallCarImmediately)
    AppCompatButton btnCallCarImmediately;

    @BindView(R.id.layoutTab)
    RelativeLayout layoutTab;


    private int type = 2;//预定车辆分类[2专车,3商务车]
    private int mCost;
    private String time;
    private RouteTask mRouteTask;
    UserDataPreference userDataPreference;

    private GeocodeSearch geocoderSearch;
    private int distance;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_booking_car);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            layoutTab.setVisibility(View.GONE);
            setTitle("送机");
        } else {
            getmToolbar().setVisibility(View.GONE);

        }
        initView();
        userDataPreference = new UserDataPreference(this);
        mRouteTask = RouteTask.getInstance(getApplicationContext());
        mRouteTask.addRouteCalculateListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        setBtnBg();
    }


    private void initView() {
        tabRecordTitle.addTab(tabRecordTitle.newTab().setText("专车"));
        tabRecordTitle.addTab(tabRecordTitle.newTab().setText("商务车"));
        tabRecordTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (TextUtils.equals(tab.getText().toString(), "专车")) {
                    type = 2;
                    layoutCarType.setVisibility(View.GONE);
                } else if (TextUtils.equals(tab.getText().toString(), "商务车")) {
                    type = 3;
                    layoutCarType.setVisibility(View.VISIBLE);
                }
                setBtnBg();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @OnClick({R.id.tvBookingTime, R.id.setOUtVoice, R.id.destinationVoice, R.id.btnCallCarImmediately, R.id.tvCarType, R.id.tvDestination, R.id.editSetOut, R.id.imgBack})
    public void onViewClicked(View view) {
        Intent intent = null;
        int requestCode = 0;
        switch (view.getId()) {
            case R.id.tvBookingTime://出发时间
                intent = new Intent(this, SelectTimeActivity.class);
                requestCode = REQUEST_CODE_TIME;
                break;
            case R.id.editSetOut://出发地
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "start");
                requestCode = REQUEST_CODE_START;
                break;
            case R.id.tvDestination://目的地
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                requestCode = REQUEST_CODE_END;
                break;

            case R.id.setOUtVoice:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "start");
                intent.putExtra("isVoice", true);
                requestCode = REQUEST_CODE_START;
                break;
            case R.id.destinationVoice:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                requestCode = REQUEST_CODE_END;
                intent.putExtra("isVoice", true);
                break;

            case R.id.tvCarType://车类型
                intent = new Intent(this, BusinessCarActivity.class);
                requestCode = REQUEST_CODE_CAR;
                break;

            case R.id.btnCallCarImmediately://立即叫车
                btnCallCarImmediately.setEnabled(false);
                if (distance < 500) {
                    ToastUtil.showToast(this, "起终点距离不能小于500米，请重新选择");
                    btnCallCarImmediately.setEnabled(true);
                } else {
                    sendOrder(editSetOut.getText().toString(), tvDestination.getText().toString(),
                            mRouteTask.getStartPoint().latitude, mRouteTask.getStartPoint().longitude,
                            mRouteTask.getEndPoint().latitude, mRouteTask.getEndPoint().longitude,
                            mCost, getOrderTime(), type, tvCarType.getText().toString());
                }
                break;

            case R.id.imgBack://出发语音
                finish();
                break;

        }
        if (intent != null) {
            if (requestCode != 0) {
                startActivityForResult(intent, requestCode);
            } else {
                startActivity(intent);
            }
        }
    }

    private String getOrderTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(Calendar.getInstance().getTime());
        time = tvBookingTime.getText().toString();
        time = time.replace("月", "-");
        time = time.replace("日", " ");
        time = time.substring(2, time.length());
        Logger.d("time:" + time);
        return year + "-" + time;
    }

    private static final int REQUEST_CODE_TIME = 1000;
    private static final int REQUEST_CODE_CAR = 1001;
    private static final int REQUEST_CODE_START = 1002;
    private static final int REQUEST_CODE_END = 1003;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TIME:
                    String time = data.getExtras().getString("time");
                    if (!TextUtils.isEmpty(time)) {
                        time = time.replaceAll("点", ":");
                        time = time.replaceAll("分", "");
                        addForeColorSpan(time);
                    }
                    setBtnBg();
                    break;
                case REQUEST_CODE_START:
                    editSetOut.setText(mRouteTask.getStartPoint().address);
                    setBtnBg();
                    getAddress(new LatLonPoint(mRouteTask.getStartPoint().latitude, mRouteTask.getStartPoint().longitude));
                    break;
                case REQUEST_CODE_CAR:
                    String car = data.getExtras().getString("carTime");
                    tvCarType.setText(car);
                    setBtnBg();
                    break;
            }
        }
    }

    private void addForeColorSpan(String content) {
        SpannableString spanString = new SpannableString(content);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spanString.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBookingTime.setText(spanString);
    }

    private void setBtnBg() {
        if (TextUtils.isEmpty(tvBookingTime.getText().toString()) ||
                TextUtils.isEmpty(editSetOut.getText().toString()) ||
                TextUtils.isEmpty(tvDestination.getText().toString()) ||
                TextUtils.isEmpty(tvEstimatedCost.getText().toString())) {
            btnCallCarImmediately.setBackgroundResource(R.drawable.bg_enable_clickable);
            btnCallCarImmediately.setEnabled(false);
        } else {
            if (type == 3) {
                if (TextUtils.isEmpty(tvCarType.getText().toString())) {
                    btnCallCarImmediately.setBackgroundResource(R.drawable.bg_enable_clickable);
                    btnCallCarImmediately.setEnabled(false);
                } else {
                    btnCallCarImmediately.setBackgroundResource(R.drawable.bg_primary_clickable_radius);
                    btnCallCarImmediately.setEnabled(true);
                }
            } else {
                btnCallCarImmediately.setBackgroundResource(R.drawable.bg_primary_clickable_radius);
                btnCallCarImmediately.setEnabled(true);
            }
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * @param addresses  上车地址
     * @param down       下车详细地址
     * @param latitude   上车纬度
     * @param longitude  上车经度
     * @param latitudel  目的地纬度
     * @param longitudel 目的地经度
     * @ string booktime 预约用车时间（2017-05-10 11:40 ）
     * @ int type 预定车辆分类[2专车,3商务车]
     */
    private void sendOrder(String addresses, String down, double latitude, double longitude
            , double latitudel, double longitudel, int mCost, String booktime, int type, String brand) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.SEND_BOOK_ORDER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("province", LocationTask.province)
                .add("city", LocationTask.city)
                .add("area", LocationTask.area)
                .add("addresses", addresses)
                .add("down", down)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("latitudel", latitudel)
                .add("longitudel", longitudel)
                .add("cost", mCost)
                .add("booktime", booktime)
                .add("type", type);
        if (type == 3) {
            baseRequest.add("brand", brand);
        }
        CallServer.getRequestInstance().add(this, 0, baseRequest, this, false, true);
    }

    @Override
    public void onRouteCalculate(float cost, float distance, int duration) {
        tvDestination.setText(mRouteTask.getEndPoint().address);
        tvEstimatedCost.setText((int) cost + "元");
        this.mCost = (int) cost;
        this.distance = (int) (distance * 1000);
        setBtnBg();
    }

    @Override
    public void onSucceed(int what, String response) {
        btnCallCarImmediately.setEnabled(true);
        try {
            OrderDetailBean detailBean = JSON.parseObject(response, OrderDetailBean.class);
            Intent intent = new Intent(this, OrderCallingActivity.class);
            intent.putExtra("orderId", detailBean.getId());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
        btnCallCarImmediately.setEnabled(true);
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        btnCallCarImmediately.setEnabled(true);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRouteTask.removeRouteCalculateListener(this);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                LocationTask.province = result.getRegeocodeAddress().getProvince();
                LocationTask.city = result.getRegeocodeAddress().getCity();
                LocationTask.area = result.getRegeocodeAddress().getDistrict();
                Log.e("789",LocationTask.province + LocationTask.city + LocationTask.area);
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {

    }

}
