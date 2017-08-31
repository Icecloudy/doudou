package com.doudou.passenger.ui.profile.settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.PayPasswordBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.secure.Md5;
import com.doudou.passenger.views.PayDialog;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FreePwdPayActivity extends BaseActivity implements HttpListener {

    @BindView(R.id.switchPay)
    Switch switchPay;
    UserDataPreference userDataPreference;
    PayDialog dialog;
    boolean isCheck;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_free_pwd_pay);
        setTitle(R.string.free_pay);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        getPayPwd();


    }

    private static final int GET_STATE = 0x001;
    private static final int SET_STATE = 0x002;

    /**
     * * @param number tel 手机号 (必须)
     */
    private void setPwdState(String paypass, int state) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.SET_PAY_STATE)
                .add("token", userDataPreference.getToken())
                .add("tel", userDataPreference.getAccount())
                .add("type", 1)
                .add("paypass", paypass)
                .add("unpaypass", state); //unpaypass 支付密码模式[101需密,102免密(默认)]
        request(SET_STATE, baseRequest, this, false, true);
    }

    private void getPayPwd() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_PAY_PASSWORD)
                .add("tel", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("type", 1);
        request(GET_STATE, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == SET_STATE) {
            if (dialog != null) {
                dialog.dismiss();
            }
            showMsg("修改成功");
        } else {
            PayPasswordBean passwordBean = JSON.parseObject(response, PayPasswordBean.class);
            if (passwordBean.getUnpaypass() == 0) {//0免密(默认),1需密
                isCheck = true;
            } else {
                isCheck = false;
            }
            switchPay.setChecked(isCheck);

            switchPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dialog = new PayDialog(FreePwdPayActivity.this).builder();
                        dialog.setOnTextFinishListener(new PayDialog.PayCallBack() {
                            @Override
                            public void onFinish(String str) {
                                setPwdState(Md5.getMd5(str), 102);
                            }
                        }).show();
                        isCheck = false;
                    } else {
                        setPwdState("", 101);
                        isCheck = true;
                    }

                }

            });
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == SET_STATE) {
            if (dialog != null) {
                dialog.dismiss();
            }
            switchPay.setChecked(isCheck);
            showMsg("修改失败");
        }

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
}
