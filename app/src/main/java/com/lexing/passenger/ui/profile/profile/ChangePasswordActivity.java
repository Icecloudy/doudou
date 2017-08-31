package com.lexing.passenger.ui.profile.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {


    @BindView(R.id.tvPayPwd)
    TextView tvPayPwd;

    private String title;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_password);
        setTitle(R.string.change_password);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.tvPayPwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvPayPwd:
                title = tvPayPwd.getText().toString();
                break;
        }
        Intent intent = new Intent(this,SetPasswordActivity.class);
        intent.putExtra("title",title);
        startActivity(intent);
        finish();
    }

}
