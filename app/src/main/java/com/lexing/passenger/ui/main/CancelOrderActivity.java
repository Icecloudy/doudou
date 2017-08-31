package com.lexing.passenger.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CancelOrderActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, HttpListener {


    @BindView(R.id.cancelReasonGR)
    RadioGroup cancelReasonGR;

    @BindView(R.id.editFeedbackContent)
    EditText editFeedbackContent;
    @BindView(R.id.slow)
    RadioButton slow;
    @BindView(R.id.traffic)
    RadioButton traffic;
    @BindView(R.id.timeOut)
    RadioButton timeOut;
    @BindView(R.id.doNotWanna)
    RadioButton doNotWanna;
    private String cancelReason = "";
    String str = "";

    UserDataPreference userDataPreference;
    int orderId;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cancel_order);
        setTitle(R.string.cancel_order);
        ButterKnife.bind(this);
        cancelReasonGR.setOnCheckedChangeListener(this);
        userDataPreference = new UserDataPreference(this);
        if (getIntent().getExtras() != null) {
            orderId = getIntent().getExtras().getInt("orderId");
        }
    }


    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (TextUtils.isEmpty(cancelReason)) {
            editFeedbackContent.setInputType(InputType.TYPE_CLASS_TEXT);
            str = editFeedbackContent.getText().toString();
        } else {
            editFeedbackContent.setInputType(InputType.TYPE_NULL);
            str = cancelReason;
        }
        if (!TextUtils.isEmpty(str)) {
            if (orderId == 0) {
                orderId = userDataPreference.getOrderId();
            }
            setCancelReason(orderId, str);
        } else {
            ToastUtil.showToast(this, "请选择一个取消原因");
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.slow:
                cancelReason = slow.getText().toString();
                break;
            case R.id.traffic:
                cancelReason = traffic.getText().toString();
                break;
            case R.id.timeOut:
                cancelReason = timeOut.getText().toString();
                break;
            case R.id.doNotWanna:
                cancelReason = doNotWanna.getText().toString();
                break;
        }
        editFeedbackContent.setText("");
    }

    /**
     * usertype
     *
     * @param orderid
     * @param reason
     */
    private void setCancelReason(int orderid, String reason) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.CANCEL_ORDER)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype", 1)
                .add("orderid", orderid)
                .add("reason", reason);
        request(0, baseRequest, this, false, false);
    }

    @Override
    public void onSucceed(int what, String response) {
        showMsg("取消成功");
        startActivity(new Intent(this, MainActivity.class));
        SysApplication.getInstance().exit();
        finish();
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
}
