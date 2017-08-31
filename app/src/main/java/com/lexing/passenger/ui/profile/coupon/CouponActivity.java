package com.lexing.passenger.ui.profile.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.BaseActivity;
import com.lexing.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponActivity extends BaseActivity implements HttpListener {

    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.couponList)
    RecyclerView couponList;
    private CouponAdapter couponAdapter;

    UserDataPreference userDataPreference;
    int status ;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coupon);
        setTitle("优惠券");
        ButterKnife.bind(this);
        if (getIntent().getExtras()!=null){
            status = 1;
        }else{
            status = 2;
        }
        userDataPreference = new UserDataPreference(this);
        getCoupon();
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
                .add("status", status);
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            List<CouponBean> couponBean = JSON.parseArray(response, CouponBean.class);
            initView(couponBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (code == 205)
            emptyView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void initView(final List<CouponBean> mList) {
        couponAdapter = new CouponAdapter(mList);
        couponList.setLayoutManager(new LinearLayoutManager(this));
        couponList.setItemAnimator(new DefaultItemAnimator());
        couponList.setAdapter(couponAdapter);
        if (status==1){
            couponAdapter.setOnItemClickListener(new CouponAdapter.OnItemClickListener() {
                @Override
                public void clickListener(View view, int i) {
                    Intent intent = new Intent();
                    intent.putExtra("id",mList.get(i).getAid());
                    intent.putExtra("money",mList.get(i).getMoney());
                    intent.putExtra("discount",mList.get(i).getDiscount());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
