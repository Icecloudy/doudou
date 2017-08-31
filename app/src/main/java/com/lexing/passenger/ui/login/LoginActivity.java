package com.lexing.passenger.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lexing.passenger.R;
import com.lexing.passenger.data.BaseDataPreference;
import com.lexing.passenger.data.GlobalPreference;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.main.MainActivity;
import com.lexing.passenger.ui.main.NavigationActivity;
import com.lexing.passenger.ui.profile.profile.AuthenticationActivity;
import com.lexing.passenger.ui.profile.settings.UserAgreementActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.StringUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.lexing.passenger.utils.secure.Md5;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements HttpListener {
    private String TAG = this.getClass().getSimpleName();


    @BindView(R.id.btn_identifying_code)
    Button btnIdentifyingCode;
    @BindView(R.id.phone)
    EditText editPhone;
    @BindView(R.id.password)
    EditText editPassword;
    @BindView(R.id.login)
    AppCompatButton btnLogin;
    @BindView(R.id.user_agreement)
    AppCompatTextView tvUserAgreement;
    String phone;
    private TimeCount time;
    private static final int GET_CODE = 0x001;
    private static final int LOGIN = 0x002;
    String registerId;
    String code = Md5.getMd5("123");
    //        String code = "123456";
    BaseDataPreference baseDataPreference;
    boolean isFirst = true;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        addForeColorSpan();
        setTitle(R.string.quick_login);
        setBackBar(false);
        registerId = JPushInterface.getRegistrationID(this);
        baseDataPreference = new BaseDataPreference(this);

//        editPhone.setText("13049375553");
//        editPassword.setText("123456");
        if (!TextUtils.isEmpty(baseDataPreference.getLoginMobile())) {
            isFirst = false;
            btnIdentifyingCode.setVisibility(View.GONE);
            editPassword.setVisibility(View.GONE);
            editPhone.setText(baseDataPreference.getLoginMobile());
            editPhone.setSelection(editPhone.getText().toString().length());
        }

    }

    private void addForeColorSpan() {
        SpannableString spanString = new SpannableString(getString(R.string.user_agreement));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spanString.setSpan(span, 12, getString(R.string.user_agreement).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUserAgreement.setText(spanString);
    }

    @OnClick({R.id.btn_identifying_code, R.id.login, R.id.user_agreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_identifying_code:
                getCode();
                break;
            case R.id.login:
                Login();
                break;
            case R.id.user_agreement:
                Intent intent = new Intent(this, UserAgreementActivity.class);
                intent.putExtra("title", "用户协议");
                startActivity(intent);
                break;
        }
    }

    //获取验证码
    private void getCode() {
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            ToastUtil.showToast(this, "请输入手机号");
        } else if (!StringUtil.isMobileNO(editPhone.getText().toString())) {
            ToastUtil.showToast(this, "手机格式不正确");
        } else {
            BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CODE)
                    .add("account", editPhone.getText().toString());
            request(GET_CODE, baseRequest, this, false, true);
        }
    }


    private void Login() {
        phone = editPhone.getText().toString();
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            ToastUtil.showToast(this, "请输入手机号");
        } else {
            if (isFirst) {
                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    ToastUtil.showToast(this, "请输入验证码");
                } else if (!code.equals(editPassword.getText().toString())) {
                    ToastUtil.showToast(this, "验证码不正确");
                } else {
                    BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                            .add("clientid", registerId)
                            .add("account", phone);
                    request(LOGIN, baseRequest, this, false, true);
                }
            } else {
                if (!phone.equals(baseDataPreference.getLoginMobile())) {
                    isFirst = true;
                    btnIdentifyingCode.setVisibility(View.VISIBLE);
                    editPassword.setVisibility(View.VISIBLE);
                } else {
                    BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                            .add("clientid", registerId)
                            .add("account", phone);
                    request(LOGIN, baseRequest, this, false, true);
                }
            }
        }


    }

    @Override
    public void onSucceed(int what, String response) {
        Log.d(TAG, response);
        if (what == GET_CODE) {
            ToastUtil.showToast(this, "验证码已发生到您的手机,请注意查收");
            time.start();
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.containsKey("codes")) {
                code = jsonObject.getString("codes");
            }
        } else {
            try {
                baseDataPreference.setLoginMobile(phone);
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                GlobalPreference sp = new GlobalPreference(this);
                sp.setCurrentUid(userBean.getId());

                UserDataPreference userDataPreference = new UserDataPreference(this);
                userDataPreference.setUserInfo(response);
                userDataPreference.setToken(userBean.getToken());
                userDataPreference.setAccount(userBean.getAccount());
                Intent intent;
                if (userBean.getUnpayorder() != null && userBean.getUnpayorder().size() > 0) {
                    intent = new Intent(this, NavigationActivity.class);
                    intent.putExtra("flag", "LoginActivity");
                    userDataPreference.setOrderId(userBean.getUnpayorder().get(0).getOrderid());
                } else {
                    if (TextUtils.isEmpty(userBean.getIdcard())) {
                        intent = new Intent(this, AuthenticationActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                }
                ToastUtil.showToast(this, "登录成功");
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
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

