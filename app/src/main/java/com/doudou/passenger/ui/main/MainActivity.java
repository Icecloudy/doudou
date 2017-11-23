package com.doudou.passenger.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.doudou.passenger.MyApplication;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.DriverLocationBean;
import com.doudou.passenger.data.models.OrderRule;
import com.doudou.passenger.data.models.SendOrderBean;
import com.doudou.passenger.data.models.UpdateBean;
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.login.LoginActivity;
import com.doudou.passenger.ui.main.airport.PickAirActivity;
import com.doudou.passenger.ui.main.booking.BookingCarActivity;
import com.doudou.passenger.ui.main.car.CarListActivity;
import com.doudou.passenger.ui.main.car.CarMallActivity;
import com.doudou.passenger.ui.main.replace.ReplaceActivity;
import com.doudou.passenger.ui.main.task.AMapUtil;
import com.doudou.passenger.ui.main.task.LocationTask;
import com.doudou.passenger.ui.main.task.OnLocationGetListener;
import com.doudou.passenger.ui.main.task.PositionEntity;
import com.doudou.passenger.ui.main.task.RegeocodeTask;
import com.doudou.passenger.ui.main.task.RouteTask;
import com.doudou.passenger.ui.main.task.Utils;
import com.doudou.passenger.ui.profile.coupon.CouponActivity;
import com.doudou.passenger.ui.profile.invoice.InvoiceActivity;
import com.doudou.passenger.ui.profile.message.NewsActivity;
import com.doudou.passenger.ui.profile.profile.ProfileActivity;
import com.doudou.passenger.ui.profile.record.TripRecordActivity;
import com.doudou.passenger.ui.profile.score.MyScoreActivity;
import com.doudou.passenger.ui.profile.settings.SettingsActivity;
import com.doudou.passenger.ui.profile.share.ShareActivity;
import com.doudou.passenger.ui.profile.wallet.PayActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.StringUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.doudou.passenger.views.RoundAngleImageView;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.doudou.passenger.utils.ConfigUtil.EXTRA_CODE;

public class MainActivity extends AppCompatActivity
        implements AMap.OnCameraChangeListener, RouteTask.OnRouteCalculateListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, HttpListener, OnGeocodeSearchListener {
    //toolbar
    @BindView(R.id.toolbar_title)
    TextView toolbar;
    @BindView(R.id.location)
    TextView tvLocation;


    //Drawer
    @BindView(R.id.imgHead)
    RoundAngleImageView imgHead;
    @BindView(R.id.nickname)
    TextView nickname;

    @BindView(R.id.checkboxMore)
    CheckBox checkboxMore;

    //layoutCallCar
    @BindView(R.id.layoutCallCar)
    LinearLayout layoutCallCar;
    @BindView(R.id.tvSetOut)
    TextView tvSetOut;
    @BindView(R.id.editDestination)
    TextView editDestination;
    @BindView(R.id.layoutCost)
    LinearLayout layoutCost;
    @BindView(R.id.tvEstimatedCost)
    TextView tvEstimatedCost;
    //layoutMore
    @BindView(R.id.layoutMore)
    LinearLayout layoutMore;


    @BindView(R.id.layoutHomeBottom)
    LinearLayout layoutHomeBottom;
    @BindView(R.id.layoutHomeTop)
    RelativeLayout layoutHomeTop;

    @BindView(R.id.layoutBottom)
    RelativeLayout layoutBottom;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.layoutBack)
    RelativeLayout layoutBack;

    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;

    @BindView(R.id.layoutTitleBottom)
    LinearLayout layoutTitleBottom;

    private AMap mAmap;
    /**
     * 地图对象
     */
    private Marker mPositionMark;

    private LatLng mStartPosition;
    private LocationTask mLocationTask;
    private RegeocodeTask mRegeocodeTask;

    private boolean mIsFirst = true;

    private boolean mIsRouteSuccess = false;
    UserDataPreference userDataPreference;
    private boolean isBack; // 是否连续点击返回键
    private static final int GET_NEAR_DRIVER = 0x001;
    private static final int SEND_ORDER = 0x002;
    private static final int GET_APP_UPDATE = 0x003;
    private static final int GET_USER = 0x004;
    private static final int GET_ORDER_CHARGE = 0x005;


    private int cost;
    private RouteTask mRouteTask;
    private String city;//上传位置
    double balance;
    int distance;
    String hotLine;

    private GeocodeSearch geocoderSearch;

    MarkerOptions markerOptions;

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        String userInfo = userDataPreference.getUserInfo();
        UserBean userBean = JSON.parseObject(userInfo, UserBean.class);
        initData(userBean);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        mLocationTask = LocationTask.getInstance(getApplicationContext());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(getApplicationContext());//为该对象赋值
        mRouteTask = RouteTask.getInstance(getApplicationContext());
        initView(savedInstanceState);
        mRouteTask.addRouteCalculateListener(this);
        checkPermission();

        registerMessageReceiver();

        getAppUpdata();
        getOrderChargeParams();
        getUserInfo();

        mLocationTask.startSingleLocate();
    }


    private void initView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        mAmap.getUiSettings().setZoomControlsEnabled(false);
        mAmap.getUiSettings().setRotateGesturesEnabled(false);
        mAmap.setOnMapLoadedListener(this);
        mAmap.setOnCameraChangeListener(this);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_zq));// 设置小蓝点的图标
        mAmap.setMyLocationStyle(myLocationStyle);//设置定位图标样式
        mAmap.setMyLocationEnabled(false);

        checkboxMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutMore.setVisibility(View.VISIBLE);
                } else {
                    layoutMore.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initData(UserBean userBean) {
        if (!TextUtils.isEmpty(userBean.getNickname())) {
            nickname.setText(userBean.getNickname());
        } else {
            nickname.setText(TextUtils.isEmpty(userBean.getTruename()) ? "用户" + userBean.getId() : userBean.getTruename());
        }
        Glide.with(this)
                .load(userBean.getPhoto())
                .placeholder(R.drawable.ic_head)
                .error(R.drawable.ic_head)
                .centerCrop()
                .crossFade()
                .into(imgHead);

        balance = userBean.getBalance();
        hotLine = userBean.getHotline();
    }

    private void hideView() {
        layoutBottom.setVisibility(View.GONE);
    }

    private void showView() {
        layoutBottom.setVisibility(View.VISIBLE);
        if (mIsRouteSuccess) {
            layoutCost.setVisibility(View.VISIBLE);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS},
                    1);//自定义的code
        }
    }


    /**
     * Show message dialog.
     */
    public void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.nav_service);
        builder.setMessage("确定拨打400客服电话");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hotLine));
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
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


    @OnClick({R.id.car_mall, R.id.car_finance, R.id.pay, R.id.imgLocation, R.id.imgPersonal, R.id.imgNews, R.id.order, R.id.call_car_immediately,
            R.id.setOUtVoice, R.id.destinationVoice, R.id.btnCallCarImmediately, R.id.editDestination, R.id.tvSetOut, R.id.location, R.id.imgBack})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.car_mall:
                intent = new Intent(this, CarListActivity.class);
                break;
            case R.id.car_finance:
                intent = new Intent(this, CarMallActivity.class);
                break;
            case R.id.pay:
                intent = new Intent(this, PayActivity.class);
                break;
            case R.id.imgLocation:
                if (mLocationTask == null) {
                    mLocationTask = LocationTask.getInstance(getApplicationContext());
                    mLocationTask.setOnLocationGetListener(this);
                }
                mLocationTask.startSingleLocate();
                break;
            case R.id.imgPersonal:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.imgNews:
                intent = new Intent(this, NewsActivity.class);
                break;
            case R.id.order:
                intent = new Intent(this, BookingCarActivity.class);
                break;
            case R.id.call_car_immediately:
                callCar();

                break;
            case R.id.setOUtVoice:
                Intent intent2 = new Intent(this, DestinationActivity.class);
                intent2.putExtra("flag", "start");
                intent2.putExtra("isVoice", true);
                startActivityForResult(intent2, 1001);
                break;
            case R.id.destinationVoice:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                intent.putExtra("isVoice", true);
                break;
            case R.id.btnCallCarImmediately://立即叫车【Button】
                if (TextUtils.equals(tvSetOut.getText().toString(), editDestination.getText().toString())) {
                    ToastUtil.showToast(this, "不可设置相同的上车地点和到达地点");
                } else if (distance < 500) {
                    ToastUtil.showToast(this, "起终点距离不能小于500米，请重新选择");
                } else {
                    sendOrder(city, mRouteTask.getStartPoint().address,
                            mRouteTask.getEndPoint().address, mRouteTask.getStartPoint().latitude, mRouteTask.getStartPoint().longitude,
                            mRouteTask.getEndPoint().latitude, mRouteTask.getEndPoint().longitude, cost);
                }
                break;
            case R.id.editDestination:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                break;
            case R.id.tvSetOut://出发地
                Intent intent1 = new Intent(this, DestinationActivity.class);
                intent1.putExtra("flag", "start");
                startActivityForResult(intent1, 1001);
                break;

            case R.id.location:
                //启动
                startActivityForResult(new Intent(MainActivity.this, CityPickerActivity.class),
                        REQUEST_CODE_PICK_CITY);
                break;
            case R.id.imgBack:
                layoutHomeTop.setVisibility(View.VISIBLE);
                layoutHomeBottom.setVisibility(View.VISIBLE);
                layoutTitle.setVisibility(View.VISIBLE);
                layoutTitleBottom.setVisibility(View.VISIBLE);
                layoutCallCar.setVisibility(View.GONE);
                layoutBack.setVisibility(View.GONE);
                break;

        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    //立即叫车Method
    private void callCar() {
        if (balance < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.nav_service);
            builder.setMessage("账户暂无余额，请先充值再叫车");
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    layoutHomeTop.setVisibility(View.GONE);
                    layoutHomeBottom.setVisibility(View.GONE);
                    layoutCallCar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            mLocationTask.startSingleLocate();
            layoutHomeTop.setVisibility(View.GONE);
            layoutHomeBottom.setVisibility(View.GONE);
            layoutCallCar.setVisibility(View.VISIBLE);
            layoutMore.setVisibility(View.GONE);

            if (mRouteTask.getEndPoint() != null) {
                layoutTitle.setVisibility(View.GONE);
                layoutTitleBottom.setVisibility(View.GONE);
                layoutCost.setVisibility(View.VISIBLE);
                layoutBack.setVisibility(View.VISIBLE);
            } else {
                layoutTitle.setVisibility(View.VISIBLE);
                layoutTitleBottom.setVisibility(View.VISIBLE);
                layoutCost.setVisibility(View.GONE);
                layoutBack.setVisibility(View.GONE);
            }
        }
    }

    private static final int REQUEST_CODE_PICK_CITY = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PositionEntity entity = mRouteTask.getStartPoint();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                tvSetOut.setText(entity.address);
                getLatlon(entity.address, entity.city);
            } else if (requestCode == REQUEST_CODE_PICK_CITY) {
                if (data != null) {
                    String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                    if (!city.endsWith("市")) {
                        city = city + "市";
                    }
                    tvLocation.setText(city);
                    getLatlon(city, city);
                }
            }
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name, String city) {

        GeocodeQuery query = new GeocodeQuery(name, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }


    //开始定位
    @Override
    public void onMapLoaded() {
        markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.icon_location_start)));
        mPositionMark = mAmap.addMarker(markerOptions);
        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2);


//        mLocationTask.startSingleLocate();
        Log.e("123", "onMapLoaded");
    }

    @Override
    public void onLocationGet(PositionEntity entity) {
        // todo 这里在网络定位时可以减少一个逆地理编码
        tvSetOut.setText(entity.address);
        mRouteTask.setStartPoint(entity);
        mStartPosition = new LatLng(entity.latitude, entity.longitude);

        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                mStartPosition, 16);
        mAmap.animateCamera(cameraUpate);
        Log.e("123", "onLocationGet");
        getNearDriver(entity.city, entity.latitude, entity.longitude);//获取周边三公里司机位置
        mMapView.setVisibility(View.VISIBLE);

    }

    //移动地图动态设置当前位置
    @Override
    public void onRegecodeGet(PositionEntity entity) {
        mMapView.setVisibility(View.VISIBLE);
        tvSetOut.setText(entity.address);


        entity.latitude = mStartPosition.latitude;
        entity.longitude = mStartPosition.longitude;

        mRouteTask.setStartPoint(entity);
        mRouteTask.search();
        city = entity.city;
        Log.e("123", "onRegecodeGet" + city);
        if (!city.endsWith("市")) {
            city = city + "市";
        }
        tvLocation.setText(city);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        hideView();
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        showView();
        mStartPosition = cameraPosition.target;
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask.search(mStartPosition.latitude, mStartPosition.longitude);
        mPositionMark.setPosition(mStartPosition);
        if (mIsFirst) {
            //添加小车
            if (mPositionMark != null) {
                mPositionMark.setToTop();
            }
            mIsFirst = false;
        }

        Logger.d("onCameraChangeFinish");
    }


    //驾车路径规划
    @Override
    public void onRouteCalculate(float mCost, float distance, int duration) {
        if (layoutCallCar.getVisibility() == View.VISIBLE) {
            layoutTitle.setVisibility(View.GONE);
            layoutTitleBottom.setVisibility(View.GONE);
            layoutCost.setVisibility(View.VISIBLE);
            layoutBack.setVisibility(View.VISIBLE);
        } else {
            layoutTitle.setVisibility(View.VISIBLE);
            layoutTitleBottom.setVisibility(View.VISIBLE);
            layoutCost.setVisibility(View.GONE);
            layoutBack.setVisibility(View.GONE);
        }
        Log.e("123", "cost:" + mCost);
        Log.e("123", "StartPoint:\n" + JSON.toJSONString(mRouteTask.getStartPoint()));
        Log.e("123", "EndPoint:\n" + JSON.toJSONString(mRouteTask.getEndPoint()));
        mIsRouteSuccess = true;
        editDestination.setText(mRouteTask.getEndPoint().address);
        this.distance = (int) (distance * 1000);
        String costTime = getString(R.string.estimated_cost, (int) mCost) + getString(R.string.estimate_time, duration);
        tvEstimatedCost.setText(costTime);
        cost = (int) mCost;
    }

    /**
     * 获取计费规则
     */
    private void getOrderChargeParams() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_ORDER_CHARGE_PARAMS)
                .add("token", userDataPreference.getToken());

        CallServer.getRequestInstance().add(this, GET_ORDER_CHARGE, baseRequest, this, false, false);
    }

    /**
     * @param city       当前城市
     * @param addresses  上车地址
     * @param down       下车详细地址
     * @param latitude   上车纬度
     * @param longitude  上车经度
     * @param latitudel  目的地纬度
     * @param longitudel 目的地经度
     */
    private void sendOrder(String city, String addresses, String down, double latitude, double longitude
            , double latitudel, double longitudel, int mCost) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.SEND_ORDER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("province", LocationTask.province)
                .add("city", city)
                .add("area", LocationTask.area)
                .add("addresses", addresses)
                .add("down", down)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("latitudel", latitudel)
                .add("longitudel", longitudel)
                .add("cost", mCost);
        CallServer.getRequestInstance().add(this, SEND_ORDER, baseRequest, this, false, true);
    }

    private void getUserInfo() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                .add("clientid", JPushInterface.getRegistrationID(this))
                .add("account", userDataPreference.getAccount());
        CallServer.getRequestInstance().add(this, GET_USER, baseRequest, this, false, false);
    }

    private void getAppUpdata() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.APP_UPDATE)
                .add("app_id", 1)
                .add("version_id", StringUtil.getPackageVersionName(this));
        CallServer.getRequestInstance().add(this, GET_APP_UPDATE, baseRequest, this, false, false);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
        Utils.removeMarkers();
        mMapView.onDestroy();
        mLocationTask.onDestroy();
        mRouteTask.removeRouteCalculateListener(this);


    }


    @OnClick({R.id.layoutHead, R.id.nav_record, R.id.nav_wallet, R.id.nav_score, R.id.nav_coupon,
            R.id.nav_share, R.id.nav_massage, R.id.nav_invoice, R.id.nav_service, R.id.nav_setting,
            R.id.PickUp, R.id.SendOut, R.id.replaceCall})
    public void onNavClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.layoutHead:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                break;
            case R.id.nav_record:
                intent = new Intent(MainActivity.this, TripRecordActivity.class);
                break;
            case R.id.nav_wallet:
                intent = new Intent(MainActivity.this, PayActivity.class);
                break;
            case R.id.nav_score:
                intent = new Intent(MainActivity.this, MyScoreActivity.class);
                break;
            case R.id.nav_coupon:
                intent = new Intent(MainActivity.this, CouponActivity.class);
                break;
            case R.id.nav_share:
                intent = new Intent(MainActivity.this, ShareActivity.class);
                break;
            case R.id.nav_massage:
                intent = new Intent(MainActivity.this, NewsActivity.class);
                break;
            case R.id.nav_invoice:
                intent = new Intent(MainActivity.this, InvoiceActivity.class);
                break;
            case R.id.nav_service:
                showMessageDialog();
                break;
            case R.id.nav_setting:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.PickUp:
                //intent = new Intent(MainActivity.this, PickAirActivity.class);
                intent = new Intent(MainActivity.this, BookingCarActivity.class);
                intent.putExtra("title", "接机");
                break;
            case R.id.SendOut:
                intent = new Intent(MainActivity.this, BookingCarActivity.class);
                intent.putExtra("title", "送机");
                break;
            case R.id.replaceCall:
                intent = new Intent(MainActivity.this, ReplaceActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_NEAR_DRIVER) {
            Log.e("123", "GET_NEAR_DRIVER" + response);
            try {
                List<DriverLocationBean> list = JSON.parseArray(response, DriverLocationBean.class);
                Utils.addEmulateData(mAmap, list);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (what == SEND_ORDER) {
            userDataPreference.setStartPosition(JSON.toJSONString(mRouteTask.getStartPoint()));
            SendOrderBean sendOrderBean = JSON.parseObject(response, SendOrderBean.class);
            userDataPreference.setOrderId(sendOrderBean.getId());
            Intent intent = new Intent(this, NavigationActivity.class);
            intent.putExtra("flag", "MainActivity");
            startActivity(intent);
            this.finish();

        } else if (what == GET_APP_UPDATE) {
            UpdateBean updateBean = JSON.parseObject(response, UpdateBean.class);
            if (updateBean.getStatus() == 0) {//	status(int) [0不用更新,1可更新,2强制更新]
            } else {
                showUpdateDialog(updateBean);
            }
        } else if (what == GET_USER) {
            userDataPreference.setUserInfo(response);
        } else if (what == GET_ORDER_CHARGE) {
            JSONArray array = JSON.parseArray(response);
            List<OrderRule> orderRules = JSON.parseArray(array.getJSONArray(0).toJSONString(), OrderRule.class);
            MyApplication.getInstance().setOrderRules(orderRules);
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == SEND_ORDER) {
            ToastUtil.showToast(this, msg);
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void getNearDriver(String city, double lat, double lng) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_NEAR_DRIVER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("city", city)
                .add("lat", lat)
                .add("lng", lng);
        CallServer.getRequestInstance().add(this, GET_NEAR_DRIVER, baseRequest, this, false, true);
        Log.e("123", "getNearDriver");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //若当前不在主页，则先返回主页
                // 双击返回桌面，默认返回true，调用finish()
                if (layoutHomeBottom.getVisibility() == View.GONE) {
                    layoutHomeTop.setVisibility(View.VISIBLE);
                    layoutHomeBottom.setVisibility(View.VISIBLE);
                    layoutTitle.setVisibility(View.VISIBLE);
                    layoutTitleBottom.setVisibility(View.VISIBLE);
                    layoutCallCar.setVisibility(View.GONE);
                    layoutBack.setVisibility(View.GONE);
                    return false;
                } else {
                    if (!isBack) {
                        isBack = true;
                        ToastUtil.showToast(this, "再按一次返回键回到桌面");
                        return false;
                    }
                }

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.lexing.passenger.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(ConfigUtil.FILTER_CODE);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                mPositionMark.setPosition(AMapUtil.convertToLatLng(address
                        .getLatLonPoint()));
            }
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConfigUtil.FILTER_CODE.equals(intent.getAction())) {
                String extra = intent.getStringExtra(EXTRA_CODE);
                if (extra.equals(ConfigUtil.UPDATE_USER)) {
                    String userInfo = userDataPreference.getUserInfo();
                    UserBean userBean = JSON.parseObject(userInfo, UserBean.class);
                    initData(userBean);
                }
            } else if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String extras = intent.getStringExtra(KEY_EXTRAS);
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extras);
                if (jsonObject.containsKey("orderid")) {
                    userDataPreference.setOrderId(jsonObject.getInteger("orderid"));
                }
                if (jsonObject.containsKey("payload")) {
                    String payload = jsonObject.getString("payload");
                    if (payload.equals(ConfigUtil.PAYLOAD_LOGOUT)) {
                        new UserDataPreference(context).reMoveKry("token");
                        context.startActivity(new Intent(context, LoginActivity.class));
                        SysApplication.getInstance().exit();
                    } else if (payload.equals(ConfigUtil.PAYLOAD_shipping) ||
                            payload.equals(ConfigUtil.PAYLOAD_driverReady) ||
                            payload.equals(ConfigUtil.PAYLOAD_startCharging) ||
                            payload.equals(ConfigUtil.PAYLOAD_payment)) {
                        Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                        i.putExtra("flag", "MainActivity");
                        startActivity(i);
                        finish();
                    }
                }
            }
        }
    }

    private void showUpdateDialog(final UpdateBean updateBean) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新提示");
        builder.setMessage(updateBean.getContent());
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(updateBean.getApk_url());
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
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
}
