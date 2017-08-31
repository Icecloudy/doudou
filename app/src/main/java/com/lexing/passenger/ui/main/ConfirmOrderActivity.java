package com.lexing.passenger.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.OrderDetailBean;
import com.lexing.passenger.data.models.OrderDriverBean;
import com.lexing.passenger.data.models.PayPasswordBean;
import com.lexing.passenger.data.models.PayResult;
import com.lexing.passenger.data.models.PrepayInfo;
import com.lexing.passenger.data.models.UserBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.login.LoginActivity;
import com.lexing.passenger.ui.profile.coupon.CouponActivity;
import com.lexing.passenger.ui.profile.coupon.CouponBean;
import com.lexing.passenger.ui.profile.profile.SetPasswordActivity;
import com.lexing.passenger.ui.profile.wallet.PayActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.StringUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.lexing.passenger.utils.secure.Md5;
import com.lexing.passenger.views.PayDialog;
import com.lexing.passenger.views.RoundAngleImageView;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yolanda.nohttp.rest.Response;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lexing.passenger.R.id.rating_star;

/**
 * 进入之后 ---- 获取免密支付状态
 * 1.已设置 是否免密           未设置  提示去设置密码
 * <p>
 * 2.免  不免
 */
public class ConfirmOrderActivity extends BaseActivity implements HttpListener {


    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.profile_image)
    RoundAngleImageView profileImage;
    @BindView(R.id.diverName)
    TextView diverName;
    @BindView(R.id.tvDriverType)
    TextView diverType;
    @BindView(rating_star)
    TextView ratingStar;
    @BindView(R.id.tvBillCount)
    TextView tvBillCount;
    @BindView(R.id.carNum)
    TextView carNum;
    @BindView(R.id.carType)
    TextView carType;
    @BindView(R.id.tvOrderMoney)
    TextView tvOrderMoney;
    @BindView(R.id.tvOrderTime)
    TextView tvOrderTime;
    @BindView(R.id.tvOrderDistance)
    TextView tvOrderDistance;
    @BindView(R.id.balancePay)
    RadioButton balancePay;
    @BindView(R.id.wxPay)
    RadioButton wxPay;
    @BindView(R.id.aliPay)
    RadioButton aliPay;
    @BindView(R.id.payRadioGroup)
    RadioGroup payRadioGroup;
    @BindView(R.id.Discount)
    TextView Discount;
    @BindView(R.id.layoutCoupon)
    RelativeLayout layoutCoupon;
    UserDataPreference userDataPreference;
    boolean isSetPP;
    boolean isFreePwd;
    int payType;
    PayDialog dialog;

    OrderDetailBean orderPayBean;
    UserBean userBean;
    private static final int GET_PP = 0x001;
    private static final int PAY = 0x002;
    private static final int ALI_PAY = 0x003;
    private static final int WX_PAY = 0x004;
    private static final int GET_COUPON = 0x005;
    int flag = 0;
    private String orderInfo;

    private String driverPhone;//司机号码
    private String driverInfo;//司机信息
    private String orderDetail;

    private int couponId;
    private IWXAPI wxApi;

    public static boolean isForeground = false;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_confirm_order);
        setTitle(R.string.confirm_order);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        getPayPwd("");
        setData();
        wxApi = WXAPIFactory.createWXAPI(this, ConfigUtil.WECHAT_PAY_APPID);
        getCoupon();
        registerMessageReceiver();
    }


    private void setData() {
        if (getIntent().getExtras() != null) {
            orderDetail = getIntent().getExtras().getString("orderInfo");
            orderPayBean = JSON.parseObject(orderDetail, OrderDetailBean.class);
            driverInfo = getIntent().getExtras().getString("driverInfo");
            OrderDriverBean data = JSON.parseObject(driverInfo, OrderDriverBean.class);


            tvOrderMoney.setText(getString(R.string.order_money, StringUtil.doubleFormat(orderPayBean.getPrice())));
            String time = String.valueOf(StringUtil.compareDate(orderPayBean.getGotime(), orderPayBean.getOvertime()));
            tvOrderTime.setText(getString(R.string.order_time, time));
            tvOrderDistance.setText(getString(R.string.order_distance, StringUtil.doubleFormat(orderPayBean.getDistance())));
            orderNumber.setText(getString(R.string.order_number, orderPayBean.getNmber()));


            setDriverInfo(data);
        }

        payRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.balancePay:
                        payType = 1;
                        break;
                    case R.id.wxPay:
                        payType = 2;
                        break;
                    case R.id.aliPay:
                        payType = 3;
                        break;
                }
            }
        });
    }

    /**
     * 司机信息
     *
     * @param data
     */
    private void setDriverInfo(OrderDriverBean data) {
        driverPhone = data.getPhone();
        Glide.with(this)
                .load(data.getPhoto())
                .placeholder(R.drawable.ic_head)
                .error(R.drawable.ic_head)
                .crossFade()
                .centerCrop()
                .into(profileImage);
        diverName.setText(TextUtils.isEmpty(data.getUsername()) ? "司机" : data.getUsername());
        ratingStar.setText(data.getScore() + "");
        tvBillCount.setText(data.getCountorders() + "单");
        carNum.setText(data.getCarnum());
        carType.setText(data.getColor() + "•" + data.getBrand());
        if (data.getType() == 1) {
            diverType.setText("社会车");//车辆分类[1社会车,2自营专车,3高端车]
        } else if (data.getType() == 2) {
            diverType.setText("自营专车");//车辆分类[1社会车,2自营专车,3高端车]
        } else {
            diverType.setText("高端车");
        }
    }

    /**
     * Show message dialog.
     */
    public void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("打电话联系司机");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + driverPhone));
                if (ActivityCompat.checkSelfPermission(ConfirmOrderActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @OnClick({R.id.imgCallDiver, R.id.imgCallDiverVoice, R.id.confirmPay, R.id.layoutCoupon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCallDiver:
                showMessageDialog();
                break;
            case R.id.imgCallDiverVoice:
                break;
            case R.id.confirmPay:
                if (payType == 0) {
                    ToastUtil.showToast(this, "请选择付款方式");
                } else {
                    confirmPay(payType);
                }
                break;
            case R.id.layoutCoupon:
                Intent intent = new Intent(this, CouponActivity.class);
                intent.putExtra("flag", 1);
                startActivityForResult(intent, 1001);
                break;
        }
    }

    private void confirmPay(int payType) {
        switch (payType) {
            case 1:
                if (orderPayBean.getPrice() > userBean.getBalance()) {
                    if (couponId != 0) {
                        balancePay();
                    } else {
                        showMessageDialog("提示", "余额不足，请去充值", "去充值", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ConfirmOrderActivity.this, PayActivity.class);
                                startActivity(intent);
                            }
                        });
                        ToastUtil.showToast(this, "余额不足，请选择其他方式付款");
                    }
                } else {
                    if (!isSetPP) {//go to set payPwd
                        showMessageDialog("提示", "未设置支付密码，去设置", getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ConfirmOrderActivity.this, SetPasswordActivity.class);
                                intent.putExtra("title", getString(R.string.set_pay_pwd));
                                startActivityForResult(intent, 1000);
                            }
                        });
                    } else {//pay
                        if (isFreePwd) {
                            balancePay();
                        } else {
                            dialog = new PayDialog(this).builder();
                            dialog.setOnTextFinishListener(new PayDialog.PayCallBack() {
                                @Override
                                public void onFinish(String str) {
                                    getPayPwd(Md5.getMd5(str));
                                }
                            }).show();
                        }
                    }
                }
                break;
            case 2:
                if (!wxApi.isWXAppInstalled()) {
                    showMsg("您没有安装微信哦");
                } else {
                    wxPay("豆豆打车车费" + tvOrderMoney.getText().toString());
                }
                break;
            case 3:
                aliPay("豆豆打车车费" + tvOrderMoney.getText().toString());
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                flag = 0;
                getPayPwd("");
            } else if (requestCode == 1001) {
                couponId = data.getExtras().getInt("id");
                double money = data.getExtras().getDouble("money");
                double disCount = data.getExtras().getDouble("discount");
                if (money > 0) {
                    Discount.setText(String.valueOf(money) + "元");
                } else {
                    Discount.setText(String.valueOf(disCount) + "折");
                }
            }
        }

    }

    /**
     * * string phone 手机号
     * token
     * status 状态[1可用,2全部(含可用、已用、失效)]
     */
    private void getCoupon() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_COUPON)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("status", 1);
        request(GET_COUPON, baseRequest, this, false, false);
    }

    private void getPayPwd(String paypass) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_PAY_PASSWORD)
                .add("tel", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("type", 1);
        if (!TextUtils.isEmpty(paypass)) {
            baseRequest.add("paypass", paypass);
        }
        request(GET_PP, baseRequest, this, false, true);
    }

    //余额支付
    private void balancePay() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.BALANCE_PAY)
                .add("nmber", orderPayBean.getNmber())
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("couponid", couponId);
        request(PAY, baseRequest, this, false, true);
    }

    //支付宝支付
    private void aliPay(String body) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.ALI_PAY)
                .add("nmber", orderPayBean.getNmber())
                .add("body", body)
                .add("couponid", couponId);
        request(ALI_PAY, baseRequest, this, false, true);
    }

    //微信
    private void wxPay(String body) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.WX_PAY)
                .add("nmber", orderPayBean.getNmber())
                .add("body", body)
                .add("couponid", couponId);
        request(WX_PAY, baseRequest, this, false, true);
    }

    /**
     * paypass(int) 0未设置密码,1已设置,
     * unpaypass(int) 0免密(默认),1需密
     *
     * @param what
     * @param response
     * @return data 2. pass(int) 0密码不匹配,1匹配
     */

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_PP) {
            try {
                PayPasswordBean passwordBean = JSON.parseObject(response, PayPasswordBean.class);

                if (flag > 0) {
                    if (passwordBean.getPass() == 0) {
                        //密码错误,重新输入
                        ToastUtil.showToast(this, "密码错误,请重试");
                        if (dialog.isShowing()) {
                            dialog.cleanPwd();
                        }
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        balancePay();
                    }
                } else {
                    if (passwordBean.getPaypass() == 0) {//1
                        isSetPP = false;
                    } else {
                        isSetPP = true;
                    }
                    if (passwordBean.getUnpaypass() == 0) {//0
                        isFreePwd = true;
                    } else {
                        isFreePwd = false;
                    }
                }
                flag++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == PAY) {
            ToastUtil.showToast(this, "支付成功");
            Intent intent = new Intent(ConfirmOrderActivity.this, OpinionDriverActivity.class);
            intent.putExtra("orderInfo", orderDetail);
            intent.putExtra("driverInfo", driverInfo);
            Logger.d(driverInfo);
            startActivity(intent);
        } else if (what == ALI_PAY) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("pay")) {
                orderInfo = jsonObject.getString("pay");
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
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
            try {
                List<CouponBean> couponBean = JSON.parseArray(response, CouponBean.class);
                if (couponBean != null && couponBean.size() > 0) {
                    couponId = couponBean.get(0).getAid();
                    double money = couponBean.get(0).getMoney();
                    double disCount = couponBean.get(0).getDiscount();
                    if (money > 0) {
                        Discount.setText(String.valueOf(money) + "元");
                    } else {
                        Discount.setText(String.valueOf(disCount) + "折");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == PAY) {//200支付完成/201支付失败/202参数不合法/203非法请求/213余额不足
            if (code == 213) {
                showMsg("支付失败");
            }
        } else if (what == ALI_PAY || what == WX_PAY) {
            ToastUtil.showToast(this, msg);
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        if (ConfigUtil.WECHAT_PAY_CODE != 100) {
            if (ConfigUtil.WECHAT_PAY_CODE == 0) {
                ConfigUtil.WECHAT_PAY_CODE = 100;
                Intent intent = new Intent(ConfirmOrderActivity.this, OpinionDriverActivity.class);
                intent.putExtra("orderInfo", orderDetail);
                intent.putExtra("driverInfo", driverInfo);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Intent intent = new Intent(ConfirmOrderActivity.this, OpinionDriverActivity.class);
                        intent.putExtra("orderInfo", orderDetail);
                        intent.putExtra("driverInfo", driverInfo);
                        Logger.d(driverInfo);
                        startActivity(intent);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(ConfirmOrderActivity.this, "支付失败，请重试");
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
            PayTask alipay = new PayTask(ConfirmOrderActivity.this);
            Map<String, String> aliRes = alipay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = aliRes;
            mHandler.sendMessage(msg);
        }
    };
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.lexing.passenger.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String extras = intent.getStringExtra(KEY_EXTRAS);
                JSONObject jsonObject = JSON.parseObject(extras);
                if (jsonObject.containsKey("payload")) {
                    String payload = jsonObject.getString("payload");
                    if (payload.equals(ConfigUtil.PAYLOAD_LOGOUT)) {
                        new UserDataPreference(context).reMoveKry("token");
                        context.startActivity(new Intent(context, LoginActivity.class));
                        SysApplication.getInstance().exit();
                        //
                    }
                }
            }
        }
    }

}
