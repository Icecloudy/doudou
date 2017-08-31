package com.lexing.passenger.nohttp;

import com.yolanda.nohttp.rest.Response;

/**
 * Created by Franco on 2016/11/4 0004.
 */

public interface HttpListener {
	void onSucceed(int what, String response);
	void onCodeError(int what, int code, String msg);
	void onFailed(int what, Response<String> response);
}
