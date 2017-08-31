package com.doudou.passenger.ui.profile.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;
import com.doudou.passenger.R;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAgreementActivity extends BaseActivity implements HttpListener{
    @BindView(R.id.webView)
    WebView webView;
    private String title;
    private String url;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_agreement);
        ButterKnife.bind(this);

        title = getIntent().getExtras().getString("title");
        setTitle(title);
        int tid = 0;
        if (TextUtils.equals(title,"用户协议")){
            tid = 101;
        }else if (TextUtils.equals(title,"充值协议")){
            tid = 102;
        }else if (TextUtils.equals(title,"关于豆豆")){
            tid = 103;
        }
        getUrl(tid);
    }

    private void getUrl(int tid){//栏目[101用户协议,102充值协议,103关于豆豆]
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_SYSTEM_AGREEMENT)
                .add("tid", tid);
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.containsKey("url")) {
            url = jsonObject.getString("url");
            webView.loadUrl(url);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
}
