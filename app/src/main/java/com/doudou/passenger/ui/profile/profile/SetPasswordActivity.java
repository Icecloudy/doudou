package com.doudou.passenger.ui.profile.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.doudou.passenger.utils.secure.Md5;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class SetPasswordActivity extends BaseActivity implements HttpListener {
    @BindView(R.id.btn_identifying_code)
    Button btnIdentifyingCode;
    @BindView(R.id.editMobile)
    EditText editMobile;
    @BindView(R.id.editCode)
    EditText editCode;
    @BindView(R.id.editPwd)
    EditText editPwd;
    @BindView(R.id.editConPwd)
    EditText editConPwd;
    @BindView(R.id.submit)
    AppCompatButton submit;
    @BindView(R.id.password)
    TextView tvPwd;
    UserDataPreference userDataPreference;

    String code = Md5.getMd5("123");

    String phone;
    private TimeCount time;

    private static final int GET_CODE = 0x001;
    private static final int SET_PP = 0x002;
    boolean isPay;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("title");
            if (title.contains("设置")) {
                tvPwd.setText(R.string.set_pwd_space);
                isPay = false;
            } else {
                isPay = true;
                tvPwd.setText(R.string.change_pwd_space);
            }
            setTitle(title);
        }
        userDataPreference = new UserDataPreference(this);
    }


    @OnClick({R.id.btn_identifying_code, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_identifying_code:
                getCode();
                break;
            case R.id.submit:
                setPayPassword();
                break;
        }
    }

    //获取验证码
    private void getCode() {
        phone = editMobile.getText().toString();
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
        if (TextUtils.isEmpty(editMobile.getText().toString())) {
            ToastUtil.showToast(this, "请输入手机号");
        } else if (!userDataPreference.getAccount().equals(editMobile.getText().toString())) {
            ToastUtil.showToast(this, "请输入本账户手机号");
        } else {
            if (isPay) {
                changePayPwd();
            } else {
                BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CODE)
                        .add("account", editMobile.getText().toString())
                        .add("clientid", JPushInterface.getRegistrationID(this));
                request(GET_CODE, baseRequest, this, false, true);
            }
        }
    }


    private void setPayPassword() {

        if (TextUtils.isEmpty(editMobile.getText().toString())) {
            ToastUtil.showToast(this, "请输入手机号");
        } else if (TextUtils.isEmpty(editCode.getText().toString())) {
            ToastUtil.showToast(this, "请输入验证码");
        } else if (!code.equals(editCode.getText().toString())) {
            ToastUtil.showToast(this, "验证码不正确");
        } else if (TextUtils.isEmpty(editPwd.getText().toString())) {
            ToastUtil.showToast(this, "请输入密码");
        } else if (TextUtils.isEmpty(editConPwd.getText().toString())) {
            ToastUtil.showToast(this, "请再次输入密码");
        }  else if (editPwd.getText().toString().length()<6) {
            ToastUtil.showToast(this, "请输入6位密码");
        }else if (!editConPwd.getText().toString().equals(editPwd.getText().toString())) {
            ToastUtil.showToast(this, "两次密码不一致");
        }  else if (!phone.equals(editMobile.getText().toString())) {
            ToastUtil.showToast(this, "手机号不正确");
        } else {
            BaseRequest baseRequest = new BaseRequest(ConfigUtil.SET_PAY_PASSWORD)
                    .add("tel", userDataPreference.getAccount())
                    .add("token", userDataPreference.getToken())
                    .add("type", 1)
                    .add("paypass", Md5.getMd5(editPwd.getText().toString()));
            CallServer.getRequestInstance().add(this, SET_PP, baseRequest, this, false, true);
        }
    }

    /**
     * = phone 手机号
     * = usertype 类型[1乘客,2司机]
     */
    private void changePayPwd() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.CHANGE_PAY_PWD)
                .add("phone", userDataPreference.getAccount())
                .add("usertype", 1);
        CallServer.getRequestInstance().add(this, GET_CODE, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_CODE) {
            ToastUtil.showToast(this, "验证码已发生到您的手机,请注意查收");
            time.start();
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.containsKey("codes")) {
                code = jsonObject.getString("codes");
            }
        } else {
            ToastUtil.showToast(this, "密码设置完成，请牢记");
            finish();
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    // 定时器，倒计时
    @SuppressWarnings("deprecation")
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btnIdentifyingCode.setTextColor(getResources().getColor(
                    R.color.colorPrimary));
            btnIdentifyingCode.setText("重新验证");
            btnIdentifyingCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            btnIdentifyingCode.setTextColor(Color.GRAY);
            btnIdentifyingCode.setClickable(false);
            btnIdentifyingCode.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}
