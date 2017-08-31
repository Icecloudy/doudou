package com.lexing.passenger.wxapi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 1.车费支付 回到confirmAty
 * 2.充值
 * 3.替他人充值
 * 4.邮费
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @BindView(R.id.pay_success)
    TextView paySuccess;
    @BindView(R.id.pay_fail)
    TextView payFail;

    private IWXAPI api;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wxpay_entry);
        setTitle("支付结果");
        ButterKnife.bind(this);

        api = WXAPIFactory.createWXAPI(this, ConfigUtil.WECHAT_PAY_APPID);
        api.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            finish();
        }
    };

    @Override
    public void onResp(BaseResp resp) {
        Log.e("==========wxpay", "onPayFinish, errCode = " + resp.errStr);
        ConfigUtil.WECHAT_PAY_CODE = resp.errCode;
        if (resp.errCode == 0) {
            paySuccess.setVisibility(View.VISIBLE);
            payFail.setVisibility(View.GONE);
        } else {
            paySuccess.setVisibility(View.GONE);
            payFail.setVisibility(View.VISIBLE);
//            payFail.setText("支付失败：" + resp.errStr);
        }
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }
}
