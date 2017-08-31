package com.lexing.passenger.ui.main.car;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.LoadingDialog;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarMallActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;
    UserDataPreference userDataPreference;
    String pwd;

    private RequestQueue requestQueue;
    private LoadingDialog loadingDialog;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_car_mall);
        ButterKnife.bind(this);
        setTitle("豆豆商城");
        userDataPreference = new UserDataPreference(this);
        requestQueue = NoHttp.newRequestQueue();
        getPwd();

    }

    private void initWidget() {
        //String Url = "http://mall.lexingcar.com/appapi/reg.php?key=1234567890&U=" + userDataPreference.getAccount() + "&P=" + pwd;
        String Url = "http://tbk.zzwjhy.com/index.php/m";
        webView.loadUrl(Url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar1.setVisibility(View.GONE);// 加载完网页进度条消失
                    // 可以用代码设置
                } else {
                    progressBar1.setVisibility(View.VISIBLE);// 开始加载网页时显示进度条
                    progressBar1.setProgress(newProgress);// 设置进度值
                    // 可以用代码设置
                }
            }
        });
    }

    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getPwd() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_PWD, RequestMethod.POST)
                .add("tel", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken());
        baseRequest.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
        requestQueue.add(0, baseRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                loadingDialog = new LoadingDialog(CarMallActivity.this).builder();
                loadingDialog.setCancelable(false).show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                Log.e("航班", response.get());
                JSONObject jsonObject = JSON.parseObject(response.get());
                if (jsonObject.containsKey("shop_pwd")) {
                    pwd = jsonObject.getString("shop_pwd");
                    initWidget();
                }
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


}
