package com.doudou.passenger.ui.profile.settings;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.widget.EditText;

import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity implements HttpListener {


    @BindView(R.id.editFeedbackContent)
    EditText editFeedbackContent;
    @BindView(R.id.submit)
    AppCompatButton submit;

    UserDataPreference userDataPreference;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback);
        setTitle(R.string.feedback);
        ButterKnife.bind(this);

        userDataPreference = new UserDataPreference(this);
    }


    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (TextUtils.isEmpty(editFeedbackContent.getText().toString())){
            showMsg("请填写您意见再提交");
        }else{
            setFeedBack(editFeedbackContent.getText().toString());
        }
    }private void setFeedBack(String content){
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.FEEDBACK)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("type", 1)
                .add("content", content);
        request(0, baseRequest, this, false, true);
    }
    @Override
    public void onSucceed(int what, String response) {
        showMsg("提交成功，感谢您的宝贵意见");
        finish();
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

}
