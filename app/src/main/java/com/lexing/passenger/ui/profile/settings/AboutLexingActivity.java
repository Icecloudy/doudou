package com.lexing.passenger.ui.profile.settings;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lexing.passenger.R;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.StringUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutLexingActivity extends BaseActivity implements HttpListener {


    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.wechat)
    TextView wechat;
    @BindView(R.id.sina)
    TextView sina;
    @BindView(R.id.email)
    TextView email;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_lexing);
        setTitle(R.string.about_lexing);
        ButterKnife.bind(this);
        tvVersion.setText(getString(R.string.version_info, StringUtil.getPackageVersionName(this)));
        getUrl(103);
    }


    @OnClick(R.id.layoutWeChat)
    public void onViewClicked() {
    }

    private void getUrl(int tid) {//栏目[101用户协议,102充值协议,103关于豆豆]
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_SYSTEM_AGREEMENT)
                .add("tid", tid);
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            AboutBean aboutBean = JSON.parseObject(response,AboutBean.class);
            setData(aboutBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
    private void setData(AboutBean data){
        wechat.setText(data.getWechat());
        email.setText(data.getEmail());
        sina.setText(data.getSina());
    }

}
