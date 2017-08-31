package com.doudou.passenger.ui.main.booking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.doudou.passenger.R;
import com.doudou.passenger.data.models.CarBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessCarActivity extends BaseActivity implements HttpListener{

    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private int page = 1;

    CarAdapter carAdapter;
    private List<CarBean> mList = new ArrayList<>();

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bussiness_car_acitvity);
        ButterKnife.bind(this);
        setTitle("商务车品牌");


        setRefreshLayout();
        getCarInfo(page);


    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getCarInfo(page);

                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getCarInfo(page);

                    }
                }, 2000);
            }
        });
    }

    private void getCarInfo(int page){
        BaseRequest request = new BaseRequest(ConfigUtil.GET_CAR_INFO)
                .add("page",page);
        request(0,request,this,false,true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
          List<CarBean>  carBeanList = JSON.parseArray(response,CarBean.class);
            if (page>1){
                mList.addAll(carBeanList);
                refreshLayout.finishLoadmore();
            }else{
                if (mList!=null){
                    mList.clear();
                }
                mList.addAll(carBeanList);
                refreshLayout.finishRefreshing();
            }
            setTripRecordAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (page>1){
            refreshLayout.finishLoadmore();
            if (code==205){
                ToastUtil.showToast(this,"没有更多数据");
            }
        }else{
            refreshLayout.finishRefreshing();
            emptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }
    private void setTripRecordAdapter() {
        carAdapter = new CarAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(carAdapter);
        carAdapter.setOnItemClickListener(new CarAdapter.OnItemClickListener() {
            @Override
            public void clickListener(View view, int i) {
                Intent intent = new Intent(BusinessCarActivity.this,BookingCarActivity.class);
                intent.putExtra("carTime",mList.get(i).getBrand());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
