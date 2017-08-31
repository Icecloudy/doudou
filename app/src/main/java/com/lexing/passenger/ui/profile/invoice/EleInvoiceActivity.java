package com.lexing.passenger.ui.profile.invoice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.PayResult;
import com.lexing.passenger.data.models.PrepayInfo;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.ui.profile.invoice.data.GetJsonDataUtil;
import com.lexing.passenger.ui.profile.invoice.data.JsonBean;
import com.lexing.passenger.ui.profile.invoice.data.OpenInvoiceBean;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lexing.passenger.R.id.aliPay;
import static com.lexing.passenger.R.id.wxPay;

public class EleInvoiceActivity extends BaseActivity implements HttpListener {

    @BindView(R.id.editCompanyName)
    EditText editCompanyName;
    @BindView(R.id.tvInvoiceMoney)
    TextView tvInvoiceMoney;
    @BindView(R.id.moreInfo)
    TextView moreInfo;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.submit)
    AppCompatButton submit;
    @BindView(R.id.editMobile)
    EditText editMobile;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.editAddressDetails)
    EditText editAddressDetails;
    @BindView(R.id.payRadioGroup)
    RadioGroup payRadioGroup;
    @BindColor(R.color.colorPrimary)
    int btnColor;

    UserDataPreference userDataPreference;

    String orderId;
    String money;

    String taxpayer = "";
    String addresstel = "";
    String remark = "";
    String bankcard = "";

    int payType = 0;
    private String orderInfo;//aliPay


    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private boolean isLoaded = false;
    private IWXAPI wxApi;
    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ele_invoice);
        ButterKnife.bind(this);
        setTitle("纸质发票");
        wxApi = WXAPIFactory.createWXAPI(this, ConfigUtil.WECHAT_PAY_APPID);
        if (getIntent().getExtras() != null) {
            orderId = getIntent().getExtras().getString("orderId");
            money = getIntent().getExtras().getString("money");
            tvInvoiceMoney.setText(money + "元");
        }
        userDataPreference = new UserDataPreference(this);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);

        setPayRadioGroup();
        if (Double.parseDouble(money) >= 200) {
            payType = 4;
            payRadioGroup.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.moreInfo, R.id.submit, R.id.tvAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moreInfo:
                Intent intent = new Intent(this, ExtraInfoInvoiceActivity.class);
                intent.putExtra("taxpayer", taxpayer);
                intent.putExtra("addresstel", addresstel);
                intent.putExtra("remark", remark);
                intent.putExtra("bankcard", bankcard);
                startActivityForResult(intent, 1000);
                break;
            case R.id.submit:
                submitData();
                break;
            case R.id.tvAddress:
                if (isLoaded) {
                    ShowPickerView();
                } else {
                    showMsg("获取城市失败，请等待");
                }
                break;
        }
    }

    private void setPayRadioGroup() {
        payRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case aliPay:
                        payType = 1;
                        break;
                    case wxPay:
                        payType = 2;
                        break;
                    case R.id.codPay:
                        payType = 3;
                        break;
                }
            }
        });
    }

    private void submitData() {
        if (TextUtils.isEmpty(editCompanyName.getText().toString())) {
            showMsg("请填写公司抬头");
        } else if (TextUtils.isEmpty(editName.getText().toString())) {
            showMsg("请填写收件人");
        } else if (TextUtils.isEmpty(editMobile.getText().toString())) {
            showMsg("请填写联系电话");
        } else if (TextUtils.isEmpty(tvAddress.getText().toString())) {
            showMsg("请选择地区");
        } else if (TextUtils.isEmpty(editAddressDetails.getText().toString())) {
            showMsg("请填写详细地址");
        } else {
            if (Double.parseDouble(money) >= 200) {
                payType = 4;
                payRadioGroup.setVisibility(View.GONE);
                //
                openInvoice(orderId, editCompanyName.getText().toString(), taxpayer, addresstel, bankcard, remark,
                        editName.getText().toString(), editMobile.getText().toString(), tvAddress.getText().toString(),
                        editAddressDetails.getText().toString(), payType);
            } else {
                if (payType == 0) {
                    showMsg("请选择支付方式");
                } else {
                    openInvoice(orderId, editCompanyName.getText().toString(), taxpayer, addresstel, bankcard, remark,
                            editName.getText().toString(), editMobile.getText().toString(), tvAddress.getText().toString(),
                            editAddressDetails.getText().toString(), payType);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(data.getExtras().getString("taxpayer")))
                    taxpayer = data.getExtras().getString("taxpayer");
                if (!TextUtils.isEmpty(data.getExtras().getString("addresstel")))
                    addresstel = data.getExtras().getString("addresstel");
                if (!TextUtils.isEmpty(data.getExtras().getString("bankcard")))
                    bankcard = data.getExtras().getString("bankcard");
                if (!TextUtils.isEmpty(data.getExtras().getString("remark")))
                    remark = data.getExtras().getString("remark");
                if (data.getExtras().getInt("count") > 0) {
                    moreInfo.setText(getString(R.string.more_info_count, data.getExtras().getInt("count")));
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    break;

            }
        }
    };

    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                tvAddress.setText(tx);

            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setSubmitColor(btnColor)//确定按钮文字颜色
                .setCancelColor(btnColor)//取消按钮文字颜色
                .setContentTextSize(18)
                .setOutSideCancelable(false)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {

            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private static final int ALI_PAY = 0x001;
    private static final int WX_PAY = 0x002;
    private static final int SUBMIT = 0x003;

    /**
     * string phone 手机号
     * md5 token
     * string orderid 订单id 用英文逗号分隔多个
     * string title 发票内容	“客运服务费” 只读，用户不可改
     * string company 公司抬头
     * <p>
     * string taxpayer 纳税人
     * string addresstel 地址、电话
     * string bankcard 开户行及账号
     * string remark 备注(可为空)
     * <p>
     * string addressee 收件人
     * string tel 联系电话
     * string area 所在地区
     * string address 详细地址
     * int payment 支付方式[1支付宝,2微信,3货到付款(默认),4包邮]
     */
    private void openInvoice(String orderid, String company, String taxpayer, String addresstel,
                             String bankcard, String remark, String addressee, String tel,
                             String area, String address, int payment) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.OPEN_INVOICE)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("orderid", orderid)
                .add("title", "客运服务费")
                .add("company", company)
                .add("taxpayer", taxpayer)
                .add("addresstel", addresstel)
                .add("bankcard", bankcard)
                .add("remark", remark)
                .add("addressee", addressee)
                .add("tel", tel)
                .add("area", area)
                .add("address", address)
                .add("payment", payment);
        request(SUBMIT, baseRequest, this, false, true);
    }

    private void aliPay(double money,String key) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.ALI_PAY)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("money", money)
                .add("key", key)
                .add("body", "发票邮费");
        request(ALI_PAY, baseRequest, this, false, true);
    }
    private void wxPay(double money,String key) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.WX_PAY)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("money", money)
                .add("key", key)
                .add("body", "发票邮费");
        request(WX_PAY, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == SUBMIT) {
            OpenInvoiceBean openInvoiceBean = JSON.parseObject(response,OpenInvoiceBean.class);
            if (payType == 1) {
                aliPay(openInvoiceBean.getMoney(),openInvoiceBean.getKey());
            } else if (payType == 2) {
                wxPay(openInvoiceBean.getMoney(),openInvoiceBean.getKey());
            } else {
                showMsg("开票成功");
                SysApplication.getInstance().finishInvoice();
                finish();
            }
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
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtil.WECHAT_PAY_CODE!=100){
            if (ConfigUtil.WECHAT_PAY_CODE==0){
                ConfigUtil.WECHAT_PAY_CODE = 100;
                //更新本地余额
                SysApplication.getInstance().finishInvoice();
                finish();
            }
        }
    }
    private static final int SDK_PAY_FLAG = 1;
    private Handler mAliHandler = new Handler() {
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
                        ToastUtil.showToast(EleInvoiceActivity.this, "支付成功");
                        //更新本地余额
                        SysApplication.getInstance().finishInvoice();
                        finish();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(EleInvoiceActivity.this, "支付取消");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(EleInvoiceActivity.this, "支付失败，请重试");
                    }
                    break;
                }
                default:
                    break;
            }

        }
    };
    Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
            PayTask alipay = new PayTask(EleInvoiceActivity.this);
            Map<String, String> aliRes = alipay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = aliRes;
            mAliHandler.sendMessage(msg);
        }
    };
}
