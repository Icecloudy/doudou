/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doudou.passenger.nohttp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudou.passenger.R;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.RestResponse;

import java.net.ProtocolException;


/**
 * Created by Franco on 2017-4-19 14:44:15.
 */
public class HttpResponseListener implements OnResponseListener<String> {

    private Activity mActivity;
    private Context mContext;
    /**
     * Dialog.
     */
    private LoadingDialog loadingDialog;
    /**
     * Request.
     */
    private Request<?> mRequest;
    /**
     * 结果回调.
     */
    private HttpListener callback;
    private int errorCode;
    private String msg;
    private String dataString;


    /**
     * @param activity     context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Activity activity, Request<String> request, HttpListener httpCallback, boolean canCancel, boolean isLoading) {
        this.mActivity = activity;
        this.mRequest = request;
        if (activity != null && isLoading) {
            loadingDialog = new LoadingDialog(activity).builder();
            loadingDialog.setCancelable(canCancel)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mRequest.cancel();
                        }
                    });
        }
        this.callback = httpCallback;
    }

    /**
     * @param context      context用来实例化dialog
     * @param request      请求对象
     * @param httpCallback 回调对象
     * @param canCancel    是否允许用户取消请求
     * @param isLoading    是否显示dialog
     */
    public HttpResponseListener(Context context, Request<String> request, HttpListener httpCallback, boolean canCancel,
                                boolean isLoading) {
        this.mContext = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            loadingDialog = new LoadingDialog(context).builder();
            loadingDialog.setCancelable(canCancel)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mRequest.cancel();
                        }
                    });
        }
        this.callback = httpCallback;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (mActivity != null) {
            if (loadingDialog != null && !mActivity.isFinishing() && !loadingDialog.isShowing())
                loadingDialog.show();
        } else if (mContext != null) {
            if (loadingDialog != null && !loadingDialog.isShowing())
                loadingDialog.show();
        }


    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<String> response) {
        if (callback != null) {
            // 这里判断一下http响应码，这个响应码问下你们的服务端你们的状态有几种，一般是200成功。
            // w3c标准http响应码：http://www.w3school.com.cn/tags/html_ref_httpmessages.asp
            int code = response.responseCode();
            if (code == 200 || code == 304) {
               // 如果使用http标准的304重定向到缓存的话，还要判断下304状态码。
                initData(response);
                if (errorCode == ConfigUtil.SUCCESS_CODE) {
                    callback.onSucceed(what, dataString);
                } else {
                    callback.onCodeError(what, errorCode, msg);
//                    showMsg(msg);
                }
            } else { // 如果
                Response<String> error = new RestResponse<>(response.request(),
                        response.isFromCache(),
                        response.getHeaders(),
                        null,
                        response.getNetworkMillis(),
                        new ParseError("数据错误")); // 这里可以传一个你的自定义异常。
                onFailed(what, error); // 去让错误的回调处理。
            }
        }
    }
   private void showMsg(String msg){
        if (mActivity!=null){
            ToastUtil.showToast(mActivity,msg);
        }else{
            ToastUtil.showToast(mContext,msg);
        }
    }
    private void showMsg(int msg){
        if (mActivity!=null){
            ToastUtil.showToast(mActivity,msg);
        }else{
            ToastUtil.showToast(mContext,msg);
        }
    }


    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<String> response) {
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            showMsg( R.string.error_please_check_network);
        } else if (exception instanceof TimeoutError) {// 请求超时
            showMsg( R.string.error_timeout);
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            showMsg( R.string.error_not_found_server);
        } else if (exception instanceof URLError) {// URL是错的
            showMsg( R.string.error_url_error);
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            showMsg( R.string.error_not_found_cache);
        } else if (exception instanceof ProtocolException) {
            showMsg( R.string.error_system_unsupport_method);
        } else if (exception instanceof ParseError) {
            showMsg( R.string.error_parse_data_error);
        } else {
            showMsg( R.string.error_unknow);
        }
        if (callback != null)
            callback.onFailed(what, response);
    }

    /**
     * 获取返回的response
     *
     * @param response
     */
    private void initData(Response<String> response) {
        JSONObject responseObject = JSON.parseObject(response.get());
        Logger.json(response.get());

        if (responseObject.containsKey("code")) {
            errorCode = responseObject.getInteger("code");
        } else {
            errorCode = 0;
        }
        if (responseObject.containsKey("message")) {
            msg = responseObject.getString("message");
        } else {
            msg = "未知错误";
        }
        if (responseObject.containsKey("data") && !TextUtils.isEmpty(responseObject.getString("data"))) {
            dataString = responseObject.getString("data");
        } else {
            dataString = "{}";
        }
    }
}
