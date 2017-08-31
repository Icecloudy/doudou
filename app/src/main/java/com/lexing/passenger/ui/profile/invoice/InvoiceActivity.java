package com.lexing.passenger.ui.profile.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvoiceActivity extends BaseActivity {


    @BindView(R.id.tvTrip)
    TextView tvTrip;
    @BindView(R.id.tvHistory)
    TextView tvHistory;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addInvoiceActivity(this);
        setContentView(R.layout.activity_invoice);
        setTitle(R.string.invoice);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvTrip, R.id.tvHistory})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tvTrip:
                intent = new Intent(this, InvoiceRecordActivity.class);
                break;
            case R.id.tvHistory:
                intent = new Intent(this, InvoiceHistoryActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
