package com.doudou.passenger.ui.profile.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.doudou.passenger.R;
import com.doudou.passenger.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExtraInfoInvoiceActivity extends BaseActivity {

    @BindView(R.id.editExtra)
    EditText editExtra;
    @BindView(R.id.editCode)
    EditText editCode;
    @BindView(R.id.editAddressMobile)
    EditText editAddressMobile;
    @BindView(R.id.editBankCount)
    EditText editBankCount;

    String taxpayer;
    String addresstel;
    String remark;
    String bankcard;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_extra_info_invoice);
        ButterKnife.bind(this);
        setTitle("更多信息");
        taxpayer = getIntent().getExtras().getString("taxpayer");
        addresstel = getIntent().getExtras().getString("addresstel");
        bankcard = getIntent().getExtras().getString("bankcard");
        remark = getIntent().getExtras().getString("remark");
        editExtra.setText(remark);
        editCode.setText(taxpayer);
        editAddressMobile.setText(addresstel);
        editBankCount.setText(bankcard);
    }



    /**
     * * @param string taxpayer 纳税人
     * * @param string addresstel 地址、电话
     * *@param string bankcard 开户行及账号
     * * @param string remark 备注(可为空)
     */
    @OnClick(R.id.confirm)
    public void onViewClicked() {
        Intent intent = new Intent();
        int count = 0;
        if (!TextUtils.isEmpty(editExtra.getText().toString())) {
            intent.putExtra("remark", editExtra.getText().toString());
            count++;
        }
        if (!TextUtils.isEmpty(editCode.getText().toString())) {
            intent.putExtra("taxpayer", editCode.getText().toString());
            count++;
        }
        if (!TextUtils.isEmpty(editAddressMobile.getText().toString())) {
            intent.putExtra("addresstel", editAddressMobile.getText().toString());
            count++;
        }
        if (!TextUtils.isEmpty(editBankCount.getText().toString())) {
            intent.putExtra("bankcard", editBankCount.getText().toString());
            count++;
        }
        intent.putExtra("count", count);
        setResult(RESULT_OK,intent);
        finish();
    }
}
