package com.lexing.passenger.ui.profile.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.main.MainActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.IdNoUtils;
import com.lexing.passenger.utils.StringUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class AuthenticationActivity extends BaseActivity implements View.OnClickListener, HttpListener {

    @BindView(R.id.realName)
    EditText realName;
    @BindView(R.id.idCard)
    EditText idCard;
    UserDataPreference userDataPreference;

    String idcard;
    String truename;
    int i = 0;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_authentication);
        setTitle(R.string.real_name_authentication);
        setRightText(R.string.skip, this);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);

        setTextWatch();
        Login();

    }

    private void setTextWatch() {
        realName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    setRightTextGone();
                } else {
                    setRightTextVisible();
                }
            }
        });
        idCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    setRightTextGone();
                } else {
                    setRightTextVisible();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        //TODO
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        truename = realName.getText().toString();
        idcard = idCard.getText().toString();
        if (TextUtils.isEmpty(truename)) {
            ToastUtil.showToast(this, "请输入真实姓名");
        } else if (!IdNoUtils.isIdcard(idcard)) {
            ToastUtil.showToast(this, "身份证号码格式不正确");
        } else if (!StringUtil.isChinese(truename)) {
            ToastUtil.showToast(this, "请输入中文姓名");
        } else {
            auth();
        }
    }

    private void auth() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.REGISTER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("truename", truename)
                .add("idcard", idcard);
        request(0, baseRequest, this, false, true);
    }

    private void Login() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                .add("clientid", JPushInterface.getRegistrationID(this))
                .add("account", userDataPreference.getAccount());
        request(1, baseRequest, this, false, false);
    }


    @Override
    public void onSucceed(int what, String response) {
        if (what == 0) {
            Login();
        } else {
            try {
                if (i > 0) {
                    showMsg("提交成功");
                    UserBean userBean = JSON.parseObject(response, UserBean.class);
                    userDataPreference.setUserInfo(response);
                    userDataPreference.setToken(userBean.getToken());
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    UserBean userBean = JSON.parseObject(response, UserBean.class);
                    userDataPreference.setUserInfo(response);
                    userDataPreference.setToken(userBean.getToken());
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
}
