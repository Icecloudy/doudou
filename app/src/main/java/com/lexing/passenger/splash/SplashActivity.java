package com.lexing.passenger.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.login.LoginActivity;
import com.lexing.passenger.ui.main.MainActivity;
import com.lexing.passenger.ui.main.NavigationActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


/**
 * 进入app判断订单状态
 * A.无订单，直接进入MainAty  B.有订单，一下四种状态
 * <p>
 * 1.叫车中...
 * 2.平滑中...订单被接，司机还未到上车地点
 * 3.行车中...
 * 4.待付款...
 */
public class SplashActivity extends AppCompatActivity implements HttpListener {


    @BindView(R.id.imgAdv)
    ImageView imgAdv;
    private int delayTime = 3000;

    private Runnable jumpToHomeRunnable = new Runnable() {

        @Override
        public void run() {
            jumpToHome();
        }
    };
    private Handler handler = new Handler();
    UserDataPreference userDataPreference;

    AdvData advData = new AdvData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucent(this);
        userDataPreference = new UserDataPreference(this);
        getAdv();
    }


    public void jumpToHome() {
        Intent intent = null;
        if (!TextUtils.isEmpty(userDataPreference.getToken())) {
            BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                    .add("clientid", JPushInterface.getRegistrationID(this))
                    .add("account", userDataPreference.getAccount());
            CallServer.getRequestInstance().add(this, 1, baseRequest, this, false, false);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }


    private void getAdv() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_SYSTEM_AdV)
                .add("com", 1);
        CallServer.getRequestInstance().add(this, 2, baseRequest, this, false, false);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == 1) {
            Intent intent;
            UserBean userBean = JSON.parseObject(response, UserBean.class);

            userDataPreference.setUserInfo(response);
            userDataPreference.setToken(userBean.getToken());
            userDataPreference.setAccount(userBean.getAccount());

            if (userBean.getUnpayorder() != null && userBean.getUnpayorder().size() > 0) {
                intent = new Intent(this, NavigationActivity.class);
                intent.putExtra("flag", "SplashActivity");
                userDataPreference.setOrderId(userBean.getUnpayorder().get(0).getOrderid());
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            try {
                advData = JSON.parseObject(response, AdvData.class);
                Glide.with(this)
                        .load(advData.getPic())
                        .placeholder(R.drawable.welcome)
                        .error(R.drawable.welcome)
                        .centerCrop()
                        .into(imgAdv);
                handler.postDelayed(jumpToHomeRunnable, delayTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {

        if (what == 1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            handler.postDelayed(jumpToHomeRunnable, delayTime);
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        if (what == 1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            handler.postDelayed(jumpToHomeRunnable, delayTime);
        }
    }

    @OnClick(R.id.imgAdv)
    public void onViewClicked() {
        if (advData != null) {
            if (!TextUtils.isEmpty(advData.getUrl())) {
                Uri uri = Uri.parse(advData.getUrl());
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("status", "onWindowFocusChanged");
        if (hasFocus) {
            handler.postDelayed(jumpToHomeRunnable, delayTime);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Exception e) {

            }
        }
    }
}
