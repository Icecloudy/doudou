package com.doudou.passenger.ui.profile.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.profile.invoice.adapter.InvoiceRecordAdapter;
import com.doudou.passenger.ui.profile.invoice.adapter.InvoiceRecordBean;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.StringUtil;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvoiceRecordActivity extends BaseActivity implements HttpListener {

    InvoiceRecordAdapter recordAdapter;
    @BindView(R.id.recordExListView)
    ExpandableListView recordExListView;
    @BindView(R.id.recordCount)
    TextView recordCount;
    @BindView(R.id.recordCost)
    TextView recordCost;
    @BindView(R.id.recordCB)
    CheckBox recordCB;
    @BindView(R.id.layoutSelect)
    RelativeLayout layoutSelect;
    private List<InvoiceRecordBean> mList = new ArrayList<>();
    private String map;
    UserDataPreference userDataPreference;
    String orderId = "";

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addInvoiceActivity(this);
        setContentView(R.layout.activity_invoice_record);
        ButterKnife.bind(this);
        setTitle("按行程开票");
        userDataPreference = new UserDataPreference(this);
        getOrderRecord();
    }

    /**
     */
    private void getOrderRecord() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_INVOICE_RECORD)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken());
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            mList = JSON.parseArray(response, InvoiceRecordBean.class);
            map = JSON.toJSONString(mList);
            Logger.d("map.put" + JSON.toJSONString(mList));
            setRecordAdapter();
            layoutSelect.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }


    @OnClick({R.id.nextStep, R.id.recordCB})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nextStep:
                nextStep();
                break;
            case R.id.recordCB:
                if (recordCB.isChecked()) {
                    setAllSelect();
                } else {
                    Logger.d("map.get:" + map);
                    setNormal();
                }
                setSelectData();
                break;
        }
    }

    private void nextStep() {
        for (int k = 0; k < mList.size(); k++) {
            for (int l = 0; l < mList.get(k).getOrderList().size(); l++) {
                if (mList.get(k).getOrderList().get(l).isChecked()) {
                    orderId = mList.get(k).getOrderList().get(l).getOrderid() + "," + orderId;
                }
            }
        }

        if (TextUtils.isEmpty(orderId)) {
            showMsg("请选择开票行程");
        } else {
            Intent intent = new Intent(this, EleInvoiceActivity.class);
            intent.putExtra("money", recordCost.getText().toString());
            intent.putExtra("orderId", orderId.substring(0, orderId.length() - 1));
            startActivityForResult(intent,1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000){
            if (resultCode==RESULT_OK){
                getOrderRecord();
            }
        }
    }

    //全选
    private void setAllSelect() {
        for (InvoiceRecordBean recordBean : mList) {
            for (int i = 0; i < recordBean.getOrderList().size(); i++) {
                recordBean.getOrderList().get(i).setChecked(true);
            }
        }
        recordAdapter.setData(mList);
        recordAdapter.notifyDataSetChanged();
    }

    //全选
    private void setNormal() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        mList = JSON.parseArray(map, InvoiceRecordBean.class);
        recordAdapter.setData(mList);
        recordAdapter.notifyDataSetChanged();
    }

    private void setRecordAdapter() {
        recordAdapter = new InvoiceRecordAdapter(this);
        recordAdapter.setData(mList);
        recordExListView.setAdapter(recordAdapter);
        recordExListView.setGroupIndicator(null);
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                recordExListView.expandGroup(i);
            }
        }
        recordAdapter.setOnItemClickListener(new InvoiceRecordAdapter.OnItemClickListener() {
            @Override
            public void clickListener(View view, int j, int i) {
                setSelectData();
                map = JSON.toJSONString(mList);
                Logger.d("map.get" + map);
            }
        });
    }

    private void setSelectData() {
        int count = 0;
        double totalMoney = 0;
        int Allcount = 0;
        for (int k = 0; k < mList.size(); k++) {
            for (int l = 0; l < mList.get(k).getOrderList().size(); l++) {
                Allcount++;
                if (mList.get(k).getOrderList().get(l).isChecked()) {

                    count++;
                    totalMoney += mList.get(k).getOrderList().get(l).getPrice();
                }
            }
        }
        if (count < Allcount) {
            recordCB.setChecked(false);
        } else {
            recordCB.setChecked(true);
        }
        recordCount.setText(String.valueOf(count));
        String moneyStr = StringUtil.doubleFormat(totalMoney);
        recordCost.setText(moneyStr);

    }


}
