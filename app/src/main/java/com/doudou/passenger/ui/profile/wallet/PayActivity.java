package com.doudou.passenger.ui.profile.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.jaeger.library.StatusBarUtil;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.PayResult;
import com.doudou.passenger.data.models.PrepayInfo;
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.profile.settings.UserAgreementActivity;
import com.doudou.passenger.ui.profile.wallet.adapter.MoneyAdapter;
import com.doudou.passenger.ui.profile.wallet.data.MoneyBean;
import com.doudou.passenger.ui.profile.wallet.data.PayBean;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.StringUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.doudou.passenger.R.id.aliPay;

public class PayActivity extends AppCompatActivity implements HttpListener {


    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.moneyRecyclerView)
    RecyclerView moneyRecyclerView;
    @BindView(R.id.editMoney)
    EditText editMoney;
    @BindView(R.id.payRadioGroup)
    RadioGroup payRadioGroup;
    @BindView(R.id.tvPayTips)
    TextView tvPayTips;

    @BindView(R.id.payAgreement)
    AppCompatTextView payAgreement;
    MoneyAdapter moneyAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List<MoneyBean> mList = new ArrayList<>();

    UserDataPreference userDataPreference;

    private static final int ALI_PAY = 0x001;
    private static final int WX_PAY = 0x002;
    private static final int GET_USER = 0x003;
    private static final int GET_DISCOUNT = 0x004;

    private String body;
    private double money;
    private int payType;
    private String orderInfo;

    @BindColor(R.color.colorPrimary)
    int statusBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, statusBar, 0);
        addForeColorSpan();
        wxApi = WXAPIFactory.createWXAPI(this, ConfigUtil.WECHAT_PAY_APPID);

        userDataPreference = new UserDataPreference(this);
        UserBean userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        tvBalance.setText(userBean.getBalance() + "");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getPayData();
    }

    private void addForeColorSpan() {
        SpannableString spanString = new SpannableString(getString(R.string.pay_agreement));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spanString.setSpan(span, 14, getString(R.string.pay_agreement).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        payAgreement.setText(spanString);
    }

    private void initData() {

        payRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case aliPay:
                        payType = 1;
                        break;
                    case R.id.wxPay:
                        payType = 2;
                        break;
                }
            }
        });
        editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    moneyAdapter.cleanItem();
                    money = StringUtil.parseDoubleMoney(editMoney.getText().toString());
                }
            }
        });

    }

    @OnClick({R.id.btnPay, R.id.payAgreement, R.id.toolbar_right, R.id.tvPayForOther})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btnPay:
                submit();
                break;
            case R.id.payAgreement:
                intent = new Intent(this, UserAgreementActivity.class);
                intent.putExtra("title", "充值协议");
                break;
            case R.id.toolbar_right:
                intent = new Intent(this, PayHistoryActivity.class);
                break;
            case R.id.tvPayForOther:
                intent = new Intent(this, PayForOtherActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private IWXAPI wxApi;

    private void submit() {
        if (money == 0) {
            ToastUtil.showToast(this, "请输入充值金额");
        } else if (payType == 0) {
            ToastUtil.showToast(this, "请选择支付类型");
        } else {
            body = "豆豆打车充值" + money + "元";
            if (payType == 1) {
                aliPay(money, body);
            } else if (payType == 2) {
                if (!wxApi.isWXAppInstalled()) {
                    ToastUtil.showToast(this, "您没有安装微信哦");
                } else {
                    wxPay(money, body);
                }

            }
        }
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return onOptionsItemSelectedCompat(item);
    }

    protected boolean onOptionsItemSelectedCompat(MenuItem item) {
        return false;
    }

    /**
     * 支付宝支付 //支付充值费用
     */

    private void aliPay(double money, String body) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.ALI_PAY)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("money", money)
                .add("body", body);
        CallServer.getRequestInstance().add(this, ALI_PAY, baseRequest, this, false, true);
    }

    //微信
    private void wxPay(double money, String body) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.WX_PAY)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("money", money)
                .add("body", body);
        CallServer.getRequestInstance().add(this, WX_PAY, baseRequest, this, false, true);
    }

    private void getUser() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                .add("clientid", JPushInterface.getRegistrationID(this))
                .add("account", userDataPreference.getAccount());
        CallServer.getRequestInstance().add(this, GET_USER, baseRequest, this, false, false);
    }

    //微信
    private void getPayData() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_DISCOUNT)
                .add("tid", 1)
                .add("page", 1)
                .add("token", userDataPreference.getToken());
        CallServer.getRequestInstance().add(this, GET_DISCOUNT, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == ALI_PAY) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("pay")) {
                orderInfo = jsonObject.getString("pay");
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        } else if (what == GET_USER) {
            try {
                UserBean userBean = JSON.parseObject(response, UserBean.class);
                userDataPreference.setUserInfo(response);
                userDataPreference.setToken(userBean.getToken());
                userDataPreference.setAccount(userBean.getAccount());
                tvBalance.setText(userBean.getBalance() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == WX_PAY) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("pay")) {
                String wxPay = jsonObject.getString("pay");
                PrepayInfo prepayInfo = JSON.parseObject(wxPay, PrepayInfo.class);
                PayReq req = new PayReq();
                req.appId = prepayInfo.getAppid();
                req.partnerId = prepayInfo.getPartnerid();
                req.prepayId = prepayInfo.getPrepayid();
                req.nonceStr = prepayInfo.getNoncestr();
                req.timeStamp = prepayInfo.getTimestamp();
                req.packageValue = "Sign=WXPay";
                req.sign = prepayInfo.getSign();
                ToastUtil.showToast(this, "正常调起支付");
                wxApi.sendReq(req);
            }
        } else {
            Log.e("123", response);
            PayBean payBean = JSON.parseObject(response, PayBean.class);
            setPayData(payBean);
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtil.WECHAT_PAY_CODE != 100) {
            if (ConfigUtil.WECHAT_PAY_CODE == 0) {
                //更新本地余额
                getUser();
            }
            ConfigUtil.WECHAT_PAY_CODE = 100;
        }
    }

    private void setPayData(PayBean data) {
        if (data.getMoneyList() != null && data.getMoneyList().size() > 0) {
            for (int i = 0; i < data.getMoneyList().size(); i++) {
                mList.add(new MoneyBean(data.getMoneyList().get(i), (int) (data.getMoneyList().get(i) * data.getMoney()), false));
            }
            moneyRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            moneyRecyclerView.setItemAnimator(new DefaultItemAnimator());
            moneyAdapter = new MoneyAdapter(mList, this);
            moneyRecyclerView.setAdapter(moneyAdapter);
            moneyAdapter.setOnItemClickListener(new MoneyAdapter.OnItemClickListener() {
                @Override
                public void clickListener(View view, int i) {
                    money = mList.get(i).getPayMoney();
                    editMoney.setText("");
                }
            });
            try {
                tvPayTips.setText(getString(R.string.pay_tips,
                        data.getMoneyList().get(0),(int) (data.getMoneyList().get(0) * data.getMoney()),
                        data.getMoneyList().get(1),(int) (data.getMoneyList().get(1) * data.getMoney()),
                        data.getMoneyList().get(2),(int) (data.getMoneyList().get(2) * data.getMoney())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Logger.d("resultInfo:" + resultInfo + "\nresultStatus:" + resultStatus);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(PayActivity.this, "支付成功");
                        //更新本地余额
                        getUser();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(PayActivity.this, "支付取消");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(PayActivity.this, "支付失败，请重试");
                    }
                    break;
                }
                default:
                    break;
            }

        }

        ;
    };
    Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
            PayTask alipay = new PayTask(PayActivity.this);
            Map<String, String> aliRes = alipay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = aliRes;
            mHandler.sendMessage(msg);
        }
    };
}
