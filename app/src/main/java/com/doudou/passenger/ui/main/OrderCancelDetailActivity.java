package com.doudou.passenger.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.models.OrderCancelBean;
import com.doudou.passenger.data.models.OrderDriverBean;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.views.RoundAngleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class OrderCancelDetailActivity extends BaseActivity {

    @BindView(R.id.profile_image)
    RoundAngleImageView profileImage;
    @BindView(R.id.diverName)
    TextView diverName;
    @BindView(R.id.tvDriverType)
    TextView diverType;
    @BindView(R.id.tvBillCount)
    TextView tvBillCount;
    @BindView(R.id.carNum)
    TextView carNum;
    @BindView(R.id.carType)
    TextView carType;
    @BindView(R.id.rating_star)
    TextView ratingStar;
    @BindView(R.id.tvCancelReason)
    TextView tvCancelReason;

    String driverPhone;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_cancel_detail);
        setTitle("取消原因");
        ButterKnife.bind(this);

        showOrderDialog();
        setBackToMainAtyFromCancel();
    }
    private void showOrderDialog() {
        if (null != getIntent().getExtras()) {
            Bundle bundle = getIntent().getExtras();
            String extraInfo = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                OrderCancelBean cancelBean = JSON.parseObject(extraInfo, OrderCancelBean.class);
                String cancelReason = cancelBean.getReason();
                tvCancelReason.setText(getString(R.string.cancel_reason, cancelReason));
                setDriverInfo(cancelBean.getUser());
                SysApplication.getInstance().exit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (data.getType()==1){
            diverType.setText("社会车");//车辆分类[1社会车,2自营专车,3高端车]
        }else if (data.getType()==2){
            diverType.setText("自营专车");//车辆分类[1社会车,2自营专车,3高端车]
        }else{
            diverType.setText("高端车");
        }
    }


    @OnClick({R.id.imgCallDiver, R.id.imgCallDiverVoice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCallDiver:
                showMessageDialog();
                break;
            case R.id.imgCallDiverVoice:
                break;
        }
    }

    /**
     * Show message dialog.
     */
    public void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("打电话联系乘客");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + driverPhone));
                if (ActivityCompat.checkSelfPermission(OrderCancelDetailActivity.this,
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
}
