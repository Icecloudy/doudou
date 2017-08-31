package com.lexing.passenger.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.data.models.OrderDetailBean;
import com.lexing.passenger.data.models.OrderDriverBean;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.lexing.passenger.views.RoundAngleImageView;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.lexing.passenger.R.id.rating_star;

public class OpinionDriverActivity extends AppCompatActivity implements HttpListener {


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
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.cb1)
    CheckBox cb1;
    @BindView(R.id.cb2)
    CheckBox cb2;
    @BindView(R.id.cb3)
    CheckBox cb3;
    @BindView(R.id.cb4)
    CheckBox cb4;
    @BindView(R.id.cb5)
    CheckBox cb5;
    @BindView(R.id.editFeedbackContent)
    EditText editFeedbackContent;

    OrderDetailBean orderPayBean;
    String thought;
    UserDataPreference userDataPreference;
    String orderInfo;
    String driverInfo;
    String driverPhone;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final int GET_USER = 0x003;
    @BindColor(R.color.colorPrimary)
    int statusBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_driver);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this,statusBar,0);
        userDataPreference = new UserDataPreference(this);

        if (getIntent().getExtras() != null) {
            driverInfo = getIntent().getExtras().getString("driverInfo");
            orderInfo = getIntent().getExtras().getString("orderInfo");
            Logger.d(orderInfo);
            OrderDriverBean data = JSON.parseObject(driverInfo, OrderDriverBean.class);
            orderPayBean = JSON.parseObject(orderInfo, OrderDetailBean.class);
            setDriverInfo(data);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpinionDriverActivity.this, MainActivity.class));
                SysApplication.getInstance().exit();
                finish();
            }
        });

        getUser();

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

    private String setThought() {
        String thought1 = "";
        if (cb1.isChecked()) {
            thought1 = cb1.getText().toString() + ";";
        } else {
            thought1 = "";
        }

        String thought2 = "";
        if (cb2.isChecked()) {
            thought2 = cb2.getText().toString() + ";";
        } else {
            thought2 = "";
        }

        String thought3 = "";
        if (cb3.isChecked()) {
            thought3 = cb3.getText().toString() + ";";
        } else {
            thought3 = "";
        }

        String thought4 = "";
        if (cb4.isChecked()) {
            thought4 = cb4.getText().toString() + ";";
        } else {
            thought4 = "";
        }

        String thought5 = "";
        if (cb5.isChecked()) {
            thought5 = cb5.getText().toString();
        } else {
            thought5 = "";
        }
        thought = thought1 + thought2 + thought3 + thought4 + thought5;
        if (thought.endsWith(";")) {
            thought = thought.substring(0, thought.length() - 1);
        }
        return thought;
    }

    @OnClick({R.id.imgCallDiver, R.id.imgCallDiverVoice, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCallDiver:
                showMessageDialog();
                break;
            case R.id.imgCallDiverVoice:
                break;
            case R.id.submit:
                if (ratingBar.getRating() != 0) {
                    setRating(orderPayBean.getId(), orderPayBean.getDid(), (int) ratingBar.getRating(),
                            setThought(), editFeedbackContent.getText().toString());
                } else {
                    showMsg("请选择评分");
                }
                break;
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
                if (ActivityCompat.checkSelfPermission(OpinionDriverActivity.this,
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

    /**
     * @param orderid
     * @param did
     * @param score
     * @param thought
     * @param content
     */
    private void setRating(int orderid, int did, int score, String thought, String content) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.SET_THOUGHT)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("orderid", orderid)
                .add("did", did)
                .add("score", score)
                .add("thought", thought)
                .add("content", content);
        CallServer.getRequestInstance().add(this, 0, baseRequest, this, false, true);
    }

    private void getUser() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.LOGIN)
                .add("clientid", JPushInterface.getRegistrationID(this))
                .add("account", userDataPreference.getAccount());
        CallServer.getRequestInstance().add(this, GET_USER, baseRequest, this, false, false);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_USER) {
            try {
                userDataPreference.setUserInfo(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            SysApplication.getInstance().exit();
            finish();
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    public void showMsg(String msg) {
        ToastUtil.showToast(this, msg);
    }

    public void showMsg(int msg) {
        ToastUtil.showToast(this, msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        startActivity(new Intent(this, MainActivity.class));
        SysApplication.getInstance().exit();
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
