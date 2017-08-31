package com.lexing.passenger.ui.main.airport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.OptionsPickerView;
import com.lexing.passenger.R;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.LoadingDialog;
import com.lexing.passenger.ui.main.airport.data.FlightList;
import com.lexing.passenger.ui.main.airport.data.FlightMsgBean;
import com.lexing.passenger.ui.main.airport.data.flightTimeBean;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.StringUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlightActivity extends Activity {

    @BindView(R.id.editAirNum)
    EditText editAirNum;
    @BindView(R.id.flightTime)
    TextView flightTime;
    @BindView(R.id.layoutFlightTime)
    RelativeLayout layoutFlightTime;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    @BindColor(R.color.colorPrimary)
    int btnColor;
    private ArrayList<flightTimeBean> options1Items = new ArrayList<>();
    private String time;

    private ArrayList<FlightList> optionsAddItems = new ArrayList<>();

    private RequestQueue requestQueue;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        ButterKnife.bind(this);
        requestQueue = NoHttp.newRequestQueue();

        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocus();
            }
        }, 100);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
        editAirNum.setTransformationMethod(new A2bigA());
    }

    private void initView() {
        options1Items.add(new flightTimeBean(StringUtil.getThreeDays()[0]));
        options1Items.add(new flightTimeBean(StringUtil.getThreeDays()[1]));
        options1Items.add(new flightTimeBean(StringUtil.getThreeDays()[2]));
        editAirNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    layoutFlightTime.setVisibility(View.VISIBLE);
                }
            }
        });

        editAirNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    ShowPickerView();
                    return true;
                }
                return false;
            }
        });

    }

    private void ShowPickerView() {// 弹出选择器
        layoutFlightTime.setVisibility(View.VISIBLE);
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText();
                flightTime.setText(tx);
                //

                getFlightMsg(editAirNum.getText().toString(), getOrderTime());
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
        hideKeyBord();
    }

    private String getOrderTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(Calendar.getInstance().getTime());
        time = flightTime.getText().toString();
        time = time.replace("月", "-");
        time = time.substring(2, time.length() - 1);
        Logger.d("time:" + time);
        return year + "-" + time;
    }

    private void setFocus() {
        editAirNum.requestFocus();
        editAirNum.setFocusable(true);
        showKeyBord(editAirNum);
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public void showKeyBord(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBord() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editAirNum.getWindowToken(), 0);

    }

    @OnClick(R.id.layoutFlightTime)
    public void onViewClicked() {
        ShowPickerView();
    }

    /**
     * name
     * key
     * date
     */

    private void getFlightMsg(String name, String date) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_FLIGHT_MSG, RequestMethod.POST)
                .add("key", ConfigUtil.APP_KEY)
                .add("name", name)
                .add("date", date);
        requestQueue.add(0, baseRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                loadingDialog = new LoadingDialog(FlightActivity.this).builder();
                loadingDialog.setCancelable(false).show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                Logger.json(response.get());
                Log.e("航班",response.get());
                parseJsonData(response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
            }
        });
    }


    private void parseJsonData(String response) {
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.containsKey("result")) {
            String result = jsonObject.getString("result");
            if (!TextUtils.isEmpty(result)) {
                FlightMsgBean msgBean = JSON.parseObject(result, FlightMsgBean.class);
                if (msgBean.getList() != null && msgBean.getList().size() > 0) {
                    if (msgBean.getList().size() == 1) {
                        //setResult
                        Intent intent = new Intent();
                        intent.putExtra("flightMsg", JSON.toJSONString(msgBean.getList().get(0)));
                        intent.putExtra("flightNum", editAirNum.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ShowFlightPickerView(msgBean);
                    }
                }
            }else{
                ToastUtil.showToast(this,"没有该航班信息");
            }
        }
    }

    private void ShowFlightPickerView(FlightMsgBean msgBean) {// 弹出选择器
        if (optionsAddItems != null) {
            optionsAddItems.clear();
        }
        for (int i = 0; i < msgBean.getList().size(); i++) {
            optionsAddItems.add(msgBean.getList().get(i));
        }
        OptionsPickerView addOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //setResult
                Intent intent = new Intent();
                intent.putExtra("flightMsg", JSON.toJSONString(optionsAddItems.get(options1)));
                intent.putExtra("flightNum", editAirNum.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        })
                .setTitleText("选择起终点")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setSubmitColor(btnColor)//确定按钮文字颜色
                .setCancelColor(btnColor)//取消按钮文字颜色
                .setContentTextSize(18)
                .setOutSideCancelable(false)// default is true
                .build();
        Logger.d(JSON.toJSONString(optionsAddItems));
        addOptions.setPicker(optionsAddItems);//一级选择器
        addOptions.show();
    }
}
