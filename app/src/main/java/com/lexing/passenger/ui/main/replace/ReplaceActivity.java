package com.lexing.passenger.ui.main.replace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.OrderDetailBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.main.DestinationActivity;
import com.lexing.passenger.ui.main.booking.OrderCallingActivity;
import com.lexing.passenger.ui.main.booking.SelectTimeActivity;
import com.lexing.passenger.ui.main.task.LocationTask;
import com.lexing.passenger.ui.main.task.RouteTask;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.StringUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Juillet on 2017/5/19.
 * <p>
 * 主城的话，没有很多好玩儿的地方，不过夜景超美，美食超多
 * 磁器口古镇，吃得多
 * 洪崖洞 现实版的千与千寻
 * 吃地道的重庆火锅，
 * 解放碑必须要去嘛
 * 坐过江索道
 * 吃山城小汤圆，正宗的酸辣粉
 * 火锅能吃辣建议吃渣渣老火锅，提前预定，否则起码等2个小时
 * 特别是在南滨路
 * 身后是喜来登国际金融中心，对面是解放碑（渝中区夜景）
 * 北滨路的话也是超美的
 * 夸我大重庆，我都不思考的
 * 吃酸辣粉一定去磁器口，看别人用手打出来的酸辣粉，还有“号子”，毛血旺
 * 也是很好吃的重庆特色
 * 再要一份重庆的凉面，
 * 吃火锅建议只当蒜和醋 放一点点盐就好 正宗的火锅油碟搭配
 */
public class ReplaceActivity extends BaseActivity implements RouteTask.OnRouteCalculateListener,
        HttpListener, GeocodeSearch.OnGeocodeSearchListener {
    @BindView(R.id.tvBookingTime)
    TextView tvBookingTime;

    @BindView(R.id.sexRadioGroup)
    RadioGroup sexRadioGroup;
    @BindView(R.id.tvReplaceMobile)
    EditText tvReplaceMobile;
    @BindView(R.id.tvReplaceUser)
    EditText tvReplaceUser;
    @BindView(R.id.editSetOut)
    TextView editSetOut;
    @BindView(R.id.tvDestination)
    TextView tvDestination;
    @BindView(R.id.tvEstimatedCost)
    TextView tvEstimatedCost;
    @BindView(R.id.btnCallCarImmediately)
    AppCompatButton btnCallCarImmediately;

    private int mCost;
    private int distance;
    private String time;
    private int mSex;
    private RouteTask mRouteTask;
    UserDataPreference userDataPreference;
    GeocodeSearch geocoderSearch;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_replace);
        ButterKnife.bind(this);
        setTitle("代叫");
        initView();
        userDataPreference = new UserDataPreference(this);
        mRouteTask = RouteTask.getInstance(getApplicationContext());
        mRouteTask.addRouteCalculateListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        setBtnBg();
    }


    private void initView() {
        sexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.man:
                        mSex = 1;
                        break;
                    case R.id.woman:
                        mSex = 0;
                        break;
                }
            }
        });
        tvReplaceMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnBg();
            }
        });
    }


    @OnClick({R.id.tvBookingTime, R.id.editSetOut, R.id.setOUtVoice, R.id.tvDestination, R.id.destinationVoice, R.id.btnCallCarImmediately})
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
            case R.id.btnCallCarImmediately:
                if (TextUtils.equals(editSetOut.getText().toString(), tvDestination.getText().toString())) {
                    ToastUtil.showToast(this, "不可设置相同的上车地点和到达地点");
                } else if (distance < 500) {
                    ToastUtil.showToast(this, "起终点距离不能小于500米，请重新选择");
                } else {
                    if (StringUtil.isMobileNO(tvReplaceMobile.getText().toString())) {

                        sendOrder(editSetOut.getText().toString(), tvDestination.getText().toString(),
                                mRouteTask.getStartPoint().latitude, mRouteTask.getStartPoint().longitude,
                                mRouteTask.getEndPoint().latitude, mRouteTask.getEndPoint().longitude,
                                mCost, getOrderTime(), tvReplaceUser.getText().toString(), mSex, tvReplaceMobile.getText().toString());
                    } else {
                        showMsg("手机格式不正确");
                    }
                }
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
        return year + "-" + time;
    }

    private static final int REQUEST_CODE_TIME = 1001;
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
                TextUtils.isEmpty(tvReplaceMobile.getText().toString()) ||
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
            , double latitudel, double longitudel, int mCost, String booktime, String nickname, int sex, String phone) {
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
                .add("type", 2)
                .add("nickname", nickname)
                .add("sex", sex)// 1 男 0 女
                .add("phone", phone);

        CallServer.getRequestInstance().add(this, 0, baseRequest, this, false, true);
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                LocationTask.province = result.getRegeocodeAddress().getProvince();
                LocationTask.city = result.getRegeocodeAddress().getCity();
                LocationTask.area = result.getRegeocodeAddress().getDistrict();
                Logger.d(LocationTask.province + LocationTask.city + LocationTask.area);
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRouteTask.removeRouteCalculateListener(this);
    }

}
