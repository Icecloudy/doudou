package com.lexing.passenger.ui.main;

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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.bumptech.glide.Glide;
import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.DriverLocationBean;
import com.lexing.passenger.data.models.OrderDetailBean;
import com.lexing.passenger.data.models.OrderDriverBean;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.login.LoginActivity;
import com.lexing.passenger.ui.main.task.InfoWinAdapter;
import com.lexing.passenger.ui.main.task.LocationTask;
import com.lexing.passenger.ui.main.task.OnLocationGetListener;
import com.lexing.passenger.ui.main.task.PositionEntity;
import com.lexing.passenger.ui.main.task.Utils;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.lexing.passenger.views.RoundAngleImageView;
import com.lexing.passenger.views.WaveView;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


/**
 * 逻辑：
 * A.从主页Main跳转过来
 * 1.拿到起点 显示正在呼叫司机
 * 2.接收到司机接单通知 修改成等待司机接驾状态 司机车辆平滑
 * 3.接收到司机已就位 停止定位
 * 4.接受到司机开始收费  行程中....
 * 5.接受到司机结束收费  行程结束....去付款
 * B.有未完成的订单
 * orderid(int) 订单id
 * status(int) 订单状态[1未接,2已接,3载客中,4已到达]
 * 1.未接
 * 2.已接  展示上车位置
 * 3.载客中
 * 4.已到达
 */
public class NavigationActivity extends BaseActivity implements View.OnClickListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, HttpListener {


    @BindView(R.id.wait_for_driver_tips)
    TextView wait_for_driver_tips;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.profile_image)
    RoundAngleImageView profileImage;
    @BindView(R.id.diverName)
    TextView diverName;
    @BindView(R.id.tvDriverType)
    TextView diverType;
    @BindView(R.id.rating_star)
    TextView rating_star;
    @BindView(R.id.tvBillCount)
    TextView tvBillCount;
    @BindView(R.id.carNum)
    TextView carNum;
    @BindView(R.id.imgCallDiver)
    ImageView imgCallDiver;
    @BindView(R.id.imgCallDiverVoice)
    ImageView imgCallDiverVoice;
    @BindView(R.id.carType)
    TextView carType;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;
    @BindView(R.id.layoutCost)
    LinearLayout layoutCost;
    @BindView(R.id.waveview)
    WaveView waveview;
    @BindView(R.id.submit)
    AppCompatButton submit;
    @BindView(R.id.activity_navigation)
    RelativeLayout activityNavigation;

    private AMap mAmap;
    private Marker mPositionMark;
    private MarkerOptions markerOption;
    private PositionEntity startEntity;
    private LatLng startLatLng;

    private int mIsFirst = 0;
    //    Circle circle;
    public static boolean isForeground = false;


    //实现平滑
    private double[] coords;//路线坐标点数据,不断清空复用
    private List<LatLng> carsLatLng;//静态车辆的数据
    private List<LatLng> goLatLng;
    private List<Marker> showMarks;//静态车辆图标
    private List<SmoothMoveMarker> smoothMarkers;//平滑移动图标集合

    private double currLat;
    private double currLog;

    MyTimerTask mTimerTask;
    Timer timer;

    UserDataPreference userDataPreference;
    private static final int ORDER_STATE_CALLING = 1;
    private static final int ORDER_STATE_WAIT = 2;
    private static final int ORDER_STATE_GOING = 3;
    private static final int ORDER_STATE_PAY = 4;
    private static final int ORDER_STATE_COM = 5;
    private static final int ORDER_STATE_COM1 = 6;
    private static final int ORDER_STATE_CANCEL = 7;
    private static final int ORDER_STATE_READY = 8;

    private int did;//司机id
    private String driverPhone;//司机号码
    private String orderInfo;//订单详情
    private String driverInfo;//司机信息
    UserBean userBean = new UserBean();
    OrderStatueTimerTask orderTimerTask;
    Timer orderTimer;
    private MyCountDownTimer2 mc2;
    String flag;
    int status;
    List<DriverLocationBean> listDriverLocation = new ArrayList<>();

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        initView(savedInstanceState);

        userDataPreference = new UserDataPreference(this);
        userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        flag = getIntent().getExtras().getString("flag");
        if (TextUtils.equals(flag, "LoginActivity")) {
            getOrderDetails(userDataPreference.getOrderId(), userBean.getId());
        } else if (TextUtils.equals(flag, "MyReceiver")) {
            getNotifationMsg(userBean);
        } else if (TextUtils.equals(flag, "MainActivity")) {
            //乘客叫车
            mc2 = new MyCountDownTimer2(360 * 1000, 1000);
            mc2.start();
            setViewByType(ORDER_STATE_CALLING);
        } else if (TextUtils.equals(flag, "SplashActivity")) {
            if (userDataPreference.getOrderId() != 0) {
                getOrderDetails(userDataPreference.getOrderId(), userBean.getId());
            }
        }

        registerMessageReceiver();


    }

    //初始化地图
    private void initView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        mAmap.getUiSettings().setZoomControlsEnabled(false);
        mAmap.getUiSettings().setRotateGesturesEnabled(false);
        mAmap.setOnMapLoadedListener(this);
        addForeColorSpan();
        // 初始化Marker添加到地图
    }

    private void setViewByType(int state) {
        switch (state) {
            case ORDER_STATE_CALLING:
                setTitle("叫车中");
                setRightText(R.string.cancel_order, this);
                if (!TextUtils.isEmpty(userDataPreference.getStartPosition())) {
                    startEntity = JSON.parseObject(userDataPreference.getStartPosition(), PositionEntity.class);
                    startLatLng = new LatLng(startEntity.latitude, startEntity.longitude);
                    setStartMarker(state, startLatLng);
                    getNearDriver(LocationTask.city, startEntity.latitude, startEntity.longitude);//获取周边三公里司机位置
                }
                break;
        }
    }

    private void getNotifationMsg(UserBean userBean) {
        Bundle bundle = getIntent().getExtras();
        String extraInfo = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.d("====================\n" + extraInfo);
        if (!TextUtils.isEmpty(extraInfo)) {
            JSONObject jsonObject = JSON.parseObject(extraInfo);
            if (jsonObject.containsKey("orderid")) {
                int orderid = jsonObject.getInteger("orderid");
                userDataPreference.setOrderId(orderid);
                getOrderDetails(orderid, userBean.getId());
            }
        }
    }

    //根据orderid获取的数据
    private void unFinishedOrder(int state, OrderDetailBean orderDetailBean) {
        Log.e("state:", "state:" + state);
        switch (state) {
            case ORDER_STATE_CALLING:
                setTitle("叫车中");
                setRightText(R.string.cancel_order, this);
                setStartMarker(state, new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude()));
                Utils.addEmulateData(mAmap, listDriverLocation);
                break;
            case ORDER_STATE_WAIT:
                setTitle("等待接驾");
                setRightText(R.string.cancel_order, this);
                setStartMarker(state, new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude()));
                showTipsView();
                handler.sendEmptyMessage(0);//获取司机位置
                break;
            case ORDER_STATE_GOING:
                setBackToFinish();
                setTitle("行程中");
                setRightTextGone();
                setStartMarker(state, new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude()));
                hideTipsView();
                if (timer != null)
                    timer.cancel();

                MyLocationStyle myLocationStyle;
                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                mAmap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                mAmap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                break;
            case ORDER_STATE_PAY:
                setBackToFinish();
                setTitle("行程结束");
                setRightTextGone();
                showPayView();
                setStartMarker(state, new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude()));
                money.setText(orderDetailBean.getPrice() + "");
                break;
            case ORDER_STATE_READY:
                setTitle("等待接驾");
                setRightText(R.string.cancel_order, this);
                setStartMarker(state, new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude()));
                showTipsView();
                if (timer != null)
                    timer.cancel();
                break;
            case ORDER_STATE_CANCEL:
                ToastUtil.showToast(this, "订单已取消");
                startActivity(new Intent(this, MainActivity.class));
                SysApplication.getInstance().exit();
                break;
            case ORDER_STATE_COM:
                ToastUtil.showToast(this, "订单已完成");
                startActivity(new Intent(this, MainActivity.class));
                SysApplication.getInstance().exit();
                break;
            case ORDER_STATE_COM1:
                ToastUtil.showToast(this, "订单已完成");
                startActivity(new Intent(this, MainActivity.class));
                SysApplication.getInstance().exit();
                break;

        }
    }
    boolean isFirstLocation;
    private void setStartMarker(int state, LatLng latLng) {
        if (!isFirstLocation){
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                    latLng, 16);
            mAmap.animateCamera(cameraUpate);
            isFirstLocation = true;
            mPositionMark = mAmap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet(" 正在为您呼叫司机 ")
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_start_marker))));
        }
        InfoWinAdapter adapter = new InfoWinAdapter();
        mAmap.setInfoWindowAdapter(adapter);
        if (state == 1) {
            waveview.start();
            waveview.setColor(0xffa7d8b2);
            if (!waveview.getState()) {
                waveview.addCircle();
                mPositionMark.showInfoWindow();
            }
        } else {
            waveview.stop();
            mPositionMark.hideInfoWindow();
        }
    }

    private void showTipsView() {
        wait_for_driver_tips.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
    }

    private void showPayView() {
        layoutCost.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
    }

    private void hideTipsView() {
        wait_for_driver_tips.setVisibility(View.GONE);
        layoutBottom.setVisibility(View.GONE);
    }


    private void addForeColorSpan() {
        SpannableString spanString = new SpannableString(getString(R.string.wait_for_driving_tips));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spanString.setSpan(span, 23, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wait_for_driver_tips.setText(spanString);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, CancelOrderActivity.class));
    }


    @OnClick({R.id.imgCallDiver, R.id.imgCallDiverVoice, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCallDiver:
                showMessageDialog();
                break;
            case R.id.imgCallDiverVoice:
                break;
            case R.id.submit:
                Intent intent = new Intent(this, ConfirmOrderActivity.class);
                intent.putExtra("orderInfo", orderInfo);
                intent.putExtra("driverInfo", driverInfo);
                startActivity(intent);
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
                if (ActivityCompat.checkSelfPermission(NavigationActivity.this,
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


    @Override
    public void onMapLoaded() {
//        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2,
//                mMapView.getHeight() / 2);

    }

    @Override
    public void onLocationGet(PositionEntity entity) {

    }

    @Override
    public void onRegecodeGet(PositionEntity entity) {


    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        mMapView.onResume();
        handler.sendEmptyMessage(3);
        Log.d("MainAty", "onResume");
        setBackToMainAty();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
        mMapView.onPause();
        if (orderTimer != null)
            orderTimer.cancel();
        Log.d("MainAty", "onPause");
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
        if (timer != null)
            timer.cancel();
        if (orderTimer != null)
            orderTimer.cancel();
        if (mc2 != null) {
            mc2.cancel();
        }

    }

    private static final int GET_ORDER_DRIVER_POSITION = 0x001;
    private static final int GET_ORDER_DETAILS = 0x002;
    private static final int GET_ORDER_DRIVER = 0x003;
    private static final int CANCEL_ORDER = 0x004;
    private static final int GET_NEAR_DRIVER = 0x005;

    private void getNearDriver(String city, double lat, double lng) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_NEAR_DRIVER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("city", city)
                .add("lat", lat)
                .add("lng", lng);
        CallServer.getRequestInstance().add(this, GET_NEAR_DRIVER, baseRequest, this, false, true);
    }

    /**
     * usertype
     *
     * @param orderid
     */
    private void setCancelReason(int orderid) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.CANCEL_ORDER)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype", 1)
                .add("orderid", orderid)
                .add("reason", "没有司机接单");
        CallServer.getRequestInstance().add(this, CANCEL_ORDER, baseRequest, this, false, false);
    }

    //获取司机位置
    private void getDriverPosition(int did) {
        BaseRequest request = new BaseRequest(ConfigUtil.GET_ORDER_DRIVER_POSITION)
                .add("did", did);
        request(GET_ORDER_DRIVER_POSITION, request, this, false, false);
    }

    //用户端获取订单数据(订单详细)
    private void getOrderDetails(int orderid, int uid) {
        BaseRequest request = new BaseRequest(ConfigUtil.GET_ORDER_DETAILS)
                .add("orderid", orderid)
                .add("uid", uid);
        request(GET_ORDER_DETAILS, request, this, false, false);
    }

    private void getDriverInfo(int did) {
        BaseRequest request = new BaseRequest(ConfigUtil.GET_ORDER_DRIVER)
                .add("did", did);
        request(GET_ORDER_DRIVER, request, this, false, false);
    }

    private static final int TOANOTHER = 0x010;

    //用户端获取订单数据(订单详细)
    private void toAnother(int orderid, int uid) {
        BaseRequest request = new BaseRequest(ConfigUtil.TO_ANOTHER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("orderid", orderid)
                .add("uid", uid);
        request(TOANOTHER, request, this, false, false);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_NEAR_DRIVER) {
            Log.e("456", response);
            try {
                listDriverLocation = JSON.parseArray(response, DriverLocationBean.class);
                Utils.addEmulateData(mAmap, listDriverLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (what == GET_ORDER_DRIVER_POSITION) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("dlat") && jsonObject.containsKey("dlng")) {
                //目的地
                if (mIsFirst == 0) {
                    mIsFirst++;
                } else {
                    LatLng car1 = new LatLng(currLat, currLog);
                    //目的地坐标集合
                    carsLatLng = new ArrayList<>();
                    carsLatLng.add(car1);
                }
                LatLng go1 = new LatLng(jsonObject.getDouble("dlat"), jsonObject.getDouble("dlng"));
                //出发地坐标集合
                goLatLng = new ArrayList<>();
                goLatLng.add(go1);

                currLat = jsonObject.getDouble("dlat");
                currLog = jsonObject.getDouble("dlng");

                putCar();
                showRunningCar();
            }
        } else if (what == GET_ORDER_DETAILS) {
            if (mMapView.getVisibility() == View.GONE) {
                mMapView.setVisibility(View.VISIBLE);
            }
            Log.e("response:", "response:" + response);
            try {
                orderInfo = response;
                OrderDetailBean orderDetailBean = JSON.parseObject(response, OrderDetailBean.class);
                startLatLng = new LatLng(orderDetailBean.getLatitude(), orderDetailBean.getLongitude());
                status = orderDetailBean.getStatus();
                if (orderDetailBean.getStatus() != 1) {
                    did = orderDetailBean.getDid();
                    getDriverInfo(orderDetailBean.getDid());
                    if (orderDetailBean.getStatus() == 2 || orderDetailBean.getStatus() == 3) {
                        LatLng car1 = new LatLng(orderDetailBean.getDlat(), orderDetailBean.getDlng());
                        //出发地坐标集合
                        carsLatLng = new ArrayList<>();
                        carsLatLng.add(car1);
                    }
                    if (TextUtils.equals(flag, "MainActivity")) {//被接单 取消定时器
                        if (mc2 != null) {
                            mc2.cancel();
                        }
                    }
                } else {
                    //取消订单
                    if (!TextUtils.equals(flag, "MainActivity")) {
                        if (orderDetailBean.getType() == 1) {
                            setCancelReason(userDataPreference.getOrderId());
                        }
                    }
                }
                unFinishedOrder(orderDetailBean.getStatus(), orderDetailBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == GET_ORDER_DRIVER) {
            //setData
            try {
                driverInfo = response;
                OrderDriverBean data = JSON.parseObject(response, OrderDriverBean.class);
                setDriverInfo(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == CANCEL_ORDER) {
            Log.e("response:", "CANCEL_ORDER:" + flag);
            if (TextUtils.equals(flag, "MainActivity")) {
                ToastUtil.showToast(this, "无人接单，订单已自动取消");
            } else {
                ToastUtil.showToast(this, "订单已取消");
            }
            userDataPreference.setOrderId(0);
            startActivity(new Intent(this, MainActivity.class));
            SysApplication.getInstance().exit();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == GET_NEAR_DRIVER) {
            Log.e("456", "onCodeError");
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        if (what == GET_NEAR_DRIVER) {
            Log.e("456", "onFailed");
        }
    }

    /**
     * 司机信息
     *
     * @param data
     */
    private void setDriverInfo(OrderDriverBean data) {
        driverPhone = data.getPhone();
        Glide.with(this)
                .load(data.getPhoto())
                .placeholder(R.drawable.ic_head)
                .error(R.drawable.ic_head)
                .crossFade()
                .centerCrop()
                .into(profileImage);
        diverName.setText(TextUtils.isEmpty(data.getUsername()) ? "司机" : data.getUsername());
        rating_star.setText(data.getScore() + "");
        tvBillCount.setText(data.getCountorders() + "单");
        carNum.setText(data.getCarnum());
        carType.setText(data.getColor() + "•" + data.getBrand());
        if (data.getType() == 1) {
            diverType.setText("社会车");//车辆分类[1社会车,2自营专车,3高端车]
        } else if (data.getType() == 2) {
            diverType.setText("自营专车");//车辆分类[1社会车,2自营专车,3高端车]
        } else {
            diverType.setText("高端车");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (timer == null) {
                        if (mTimerTask != null) {
                            mTimerTask.cancel(); // 将原任务从队列中移除
                        }
                        timer = new Timer();
                        mTimerTask = new MyTimerTask(); // 新建一个任务
                        timer.schedule(mTimerTask, 0, 1000 * 20); // 经过20s后再次执行
                    }
                    break;
                case 1:
                    getDriverPosition(did);
                    break;
                case 2:
                    if (userDataPreference.getOrderId() != 0) {
                        getOrderDetails(userDataPreference.getOrderId(), userBean.getId());
                    }
                    break;
                case 3:
                    if (orderTimer != null) {
                        orderTimer.cancel();
                    }
                    orderTimer = new Timer();
                    if (orderTimerTask != null) {
                        orderTimerTask.cancel(); // 将原任务从队列中移除
                    }
                    orderTimerTask = new OrderStatueTimerTask(); // 新建一个任务
                    orderTimer.schedule(orderTimerTask, 0, 1000 * 5); // 经过20s后再次执行
                    break;
                case 4:
//                    toAnother(userDataPreference.getOrderId(), userBean.getId());
                    break;
            }

        }
    };


    /**
     * 继承 CountDownTimer 防范
     * <p>
     * 重写 父类的方法 onTick() 、 onFinish()
     */
    boolean isToAnother = true;

    class MyCountDownTimer2 extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (TextUtils.equals(flag, "MainActivity")) {
                setCancelReason(userDataPreference.getOrderId());
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished / 1000 < 180) {
                if (isToAnother) {
                    isToAnother = false;
                    handler.sendEmptyMessage(4);
                }
            }
        }
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }

    class OrderStatueTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(2);
        }
    }

    //放入司机车辆位置
    private void putCar() {
        //清除旧集合
        mAmap.clear();
        if (showMarks == null) {
            showMarks = new ArrayList<>();
        }
        for (int j = 0; j < showMarks.size(); j++) {
            showMarks.get(j).remove();
        }
        mPositionMark.setPosition(startLatLng);
        //依次放入静态图标
        for (int i = 0; i < carsLatLng.size(); i++) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.white_car);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(carsLatLng.get(i).longitude, carsLatLng.get(i).latitude))
                    .icon(icon);
            showMarks.add(mAmap.addMarker(markerOptions));
            Animation startAnimation = new AlphaAnimation(0, 1);
            startAnimation.setDuration(600);
            //设置所有静止车的角度
            showMarks.get(i).setRotateAngle(new Random().nextInt(359));
            showMarks.get(i).setAnimation(startAnimation);
            showMarks.get(i).startAnimation();
        }
    }

    /**
     * 展示平滑移动车辆
     */
    private void showRunningCar() {
        mAmap.clear();//清空地图覆盖物
        smoothMarkers = null;//清空旧数据
        smoothMarkers = new ArrayList<>();
        mPositionMark.setPosition(startLatLng);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.white_car);
        //循环
        for (int i = 0; i < carsLatLng.size(); i++) {
            //放入路线
            double[] newoords = {carsLatLng.get(i).longitude, carsLatLng.get(i).latitude,
                    goLatLng.get(i).longitude, goLatLng.get(i).latitude};
            coords = newoords;
            //移动车辆
            movePoint(icon);
        }
    }

    //平滑移动
    public void movePoint(BitmapDescriptor bitmap) {
        // 获取轨迹坐标点
        List<LatLng> points = readLatLngs();
        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(mAmap);
        smoothMarkers.add(smoothMarker);
        int num = smoothMarkers.size() - 1;
        // 设置滑动的图标
        smoothMarkers.get(num).setDescriptor(bitmap);
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置滑动的轨迹左边点
        smoothMarkers.get(num).setPoints(subList);
        // 设置滑动的总时间
        smoothMarkers.get(num).setTotalDuration(10);
        // 开始滑动
        smoothMarkers.get(num).startSmoothMove();
    }

    //获取路线
    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.lexing.passenger.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String extras = intent.getStringExtra(KEY_EXTRAS);
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extras);
                if (jsonObject.containsKey("payload")) {
                    String payload = jsonObject.getString("payload");
                    if (payload.equals(ConfigUtil.PAYLOAD_LOGOUT)) {
                        new UserDataPreference(context).reMoveKry("token");
                        context.startActivity(new Intent(context, LoginActivity.class));
                        SysApplication.getInstance().exit();
                    } else {
                        setCostomMsg(extras);
                    }
                } else {
                    setCostomMsg(extras);
                }
            }
        }
    }

    private void setCostomMsg(String extraInfo) {
        if (!TextUtils.isEmpty(extraInfo)) {
            JSONObject jsonObject = JSON.parseObject(extraInfo);
            if (jsonObject.containsKey("orderid")) {
                int orderid = jsonObject.getInteger("orderid");
                userDataPreference.setOrderId(orderid);
                getOrderDetails(orderid, userBean.getId());
            }
        }
    }

//    boolean isForbidden;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            if (status == 1 || status == 2) {
                startActivity(new Intent(this, CancelOrderActivity.class));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("您有未完成订单，请先完成再退出");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

        return super.onKeyDown(keyCode, event);
    }
}
