package com.doudou.passenger.ui.main.airport;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.OrderDetailBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.main.DestinationActivity;
import com.doudou.passenger.ui.main.airport.data.FlightList;
import com.doudou.passenger.ui.main.airport.data.flightTimeBean;
import com.doudou.passenger.ui.main.booking.OrderCallingActivity;
import com.doudou.passenger.ui.main.task.LocationTask;
import com.doudou.passenger.ui.main.task.PositionEntity;
import com.doudou.passenger.ui.main.task.RouteTask;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.StringUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickAirActivity extends BaseActivity implements RouteTask.OnRouteCalculateListener,
        HttpListener, GeocodeSearch.OnGeocodeSearchListener {


    @BindView(R.id.tvAirNum)
    TextView tvAirNum;
    @BindView(R.id.tvBookingTime)
    TextView tvBookingTime;
    @BindView(R.id.editSetOut)
    TextView editSetOut;
    @BindView(R.id.setOUtVoice)
    ImageView setOUtVoice;
    @BindView(R.id.tvDestination)
    TextView tvDestination;
    @BindView(R.id.destinationVoice)
    ImageView destinationVoice;
    @BindView(R.id.tvEstimatedCost)
    TextView tvEstimatedCost;
    @BindView(R.id.btnCallCarImmediately)
    AppCompatButton btnCallCarImmediately;

    private int mCost;
    private String time;
    private RouteTask mRouteTask;
    UserDataPreference userDataPreference;

    private GeocodeSearch geocoderSearch;

    @BindColor(R.color.colorPrimary)
    int btnColor;
    private ArrayList<flightTimeBean> options1Items = new ArrayList<>();
    String flightTime;
    int delayed;
    private int distance;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pick_air);
        setTitle("接机");
        ButterKnife.bind(this);
        initView();

        userDataPreference = new UserDataPreference(this);
        mRouteTask = RouteTask.getInstance(getApplicationContext());
        mRouteTask.addRouteCalculateListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        setBtnBg();
    }


    private void initView() {
        for (int i = 0; i < 9; i++) {
            options1Items.add(new flightTimeBean("航班到达后" + (i + 1) + "0分钟"));
        }
    }

    private void setBtnBg() {
        if (TextUtils.isEmpty(tvAirNum.getText().toString()) ||
                TextUtils.isEmpty(tvBookingTime.getText().toString()) ||
                TextUtils.isEmpty(editSetOut.getText().toString()) ||
                TextUtils.isEmpty(tvDestination.getText().toString()) ||
                TextUtils.isEmpty(tvEstimatedCost.getText().toString())) {
            btnCallCarImmediately.setBackgroundResource(R.drawable.bg_enable_clickable);
            btnCallCarImmediately.setEnabled(false);
        } else {
            btnCallCarImmediately.setBackgroundResource(R.drawable.bg_primary_clickable_radius);
            btnCallCarImmediately.setEnabled(true);
        }
    }


    /**
     * 响应地理编码
     */
    public void getLatlon(final String name, final String city) {
        GeocodeQuery query = new GeocodeQuery(name, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }


    private static final int REQUEST_CODE_FLIGHT = 1000;
    private static final int REQUEST_CODE_CAR = 1001;
    private static final int REQUEST_CODE_START = 1002;
    private static final int REQUEST_CODE_END = 1003;

    @OnClick({R.id.tvBookingTime, R.id.editSetOut, R.id.setOUtVoice, R.id.tvDestination, R.id.destinationVoice, R.id.btnCallCarImmediately, R.id.tvAirNum})
    public void onViewClicked(View view) {
        Intent intent = null;
        int requestCode = 0;
        switch (view.getId()) {
            case R.id.tvBookingTime:
                if (TextUtils.isEmpty(tvAirNum.getText().toString())) {
                    showMsg("请先选择航班");
                } else {
                    ShowPickerView();
                }
                break;
            case R.id.editSetOut:
                break;
            case R.id.setOUtVoice:
                break;
            case R.id.tvDestination:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                requestCode = REQUEST_CODE_END;
                break;
            case R.id.destinationVoice:
                intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("flag", "end");
                requestCode = REQUEST_CODE_END;
                intent.putExtra("isVoice", true);
                break;
            case R.id.btnCallCarImmediately:
                if (TextUtils.equals(editSetOut.getText().toString(), tvDestination.getText().toString())) {
                    ToastUtil.showToast(this, "不可设置相同的上车地点和到达地点");
                } else if (distance < 500) {
                    ToastUtil.showToast(this, "起终点距离不能小于500米，请重新选择");
                } else {
                    sendOrder(editSetOut.getText().toString(), tvDestination.getText().toString(),
                            mRouteTask.getStartPoint().latitude, mRouteTask.getStartPoint().longitude,
                            mRouteTask.getEndPoint().latitude, mRouteTask.getEndPoint().longitude,
                            mCost, time, tvAirNum.getText().toString(), delayed);
                }
                break;
            case R.id.tvAirNum:
                intent = new Intent(this, FlightActivity.class);
                requestCode = REQUEST_CODE_FLIGHT;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FLIGHT:
                    String flightMsg = data.getExtras().getString("flightMsg");
                    String flightNum = data.getExtras().getString("flightNum");
                    tvAirNum.setText(flightNum);
                    Logger.d("flightMsg:" + flightMsg);
                    try {
                        FlightList flightLists = JSON.parseObject(flightMsg, FlightList.class);
                        if (TextUtils.isEmpty(flightLists.getSjddtime_full())) {
                            flightTime = flightLists.getJhddtime_full();
                        } else {
                            flightTime = flightLists.getSjddtime_full();
                        }
                        String msg = flightLists.getDd() + "(" + flightTime.substring(5, flightTime.length()) + "到达)";
                        editSetOut.setText(msg);
                        getLatlon(flightLists.getDd(), flightLists.getDd_city());
                        addForeColorSpan("航班到达后20分钟");
                        time = StringUtil.getSpecifiedDayBefore(flightTime, 20);
                        setBtnBg();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void ShowPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText();
                addForeColorSpan(tx);
                delayed = Integer.parseInt(tx.substring(5, 7));
                time = StringUtil.getSpecifiedDayBefore(flightTime, delayed);
                setBtnBg();
            }
        })
                .setTitleText("当地起飞时间")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setSubmitColor(btnColor)//确定按钮文字颜色
                .setCancelColor(btnColor)//取消按钮文字颜色
                .setContentTextSize(18)
                .setOutSideCancelable(false)// default is true
                .build();

        pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.show();
    }

    /**
     * @param addresses  上车地址
     * @param down       下车详细地址
     * @param latitude   上车纬度
     * @param longitude  上车经度
     * @param latitudel  目的地纬度
     * @param longitudel 目的地经度
     * @param delayed    延后分钟数(接机)
     * @ string booktime 预约用车时间（2017-05-10 11:40 ）
     * @ int type 预定车辆分类[2专车,3商务车]
     * * @param string flight 航班(接机)
     */
    private void sendOrder(String addresses, String down, double latitude, double longitude
            , double latitudel, double longitudel, int mCost, String booktime, String flight, int delayed) {
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
                .add("flight", flight)
                .add("delayed", delayed);
        CallServer.getRequestInstance().add(this, 0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
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
    }

    @Override
    public void onFailed(int what, Response<String> response) {

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
                mRouteTask.setStartPoint(new PositionEntity(address.getLatLonPoint().getLatitude(), address.getLatLonPoint().getLongitude(), address.getFormatAddress()));
                LocationTask.province = address.getProvince();
                LocationTask.city = address.getCity();
                LocationTask.area = address.getDistrict();
            }
        }
    }

    @Override
    public void onRouteCalculate(float cost, float distance, int duration) {
        tvDestination.setText(mRouteTask.getEndPoint().address);
        tvEstimatedCost.setText((int) cost + "元");
        this.mCost = (int) cost;
        this.distance = (int) (distance * 1000);
        setBtnBg();
    }
}
