package com.doudou.passenger.ui.profile.invoice;

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
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.profile.invoice.adapter.InvoiceHisAdapter;
import com.doudou.passenger.ui.profile.invoice.data.InvoiceHistoryBean;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceHistoryActivity extends BaseActivity implements HttpListener {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;

    private int page = 1;
    private List<InvoiceHistoryBean> mList = new ArrayList<>();
    UserDataPreference userDataPreference;
    InvoiceHisAdapter tripRecordAdapter;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_real_time);
        ButterKnife.bind(this);
        setTitle("开票历史");
        emptyView.setText("无开票记录");
        userDataPreference = new UserDataPreference(this);
        getOrderRecord(page);
        setRefreshLayout();
    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getOrderRecord(page);

                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getOrderRecord(page);

                    }
                }, 2000);
            }
        });
    }

    /**
     * @param page type 订单类型[1实时,2预约] 司机端不用传该参数
     */
    private void getOrderRecord(int page) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.OPEN_INVOICE__LIST)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("page", page);
        request(0, baseRequest, this, false, true);
    }


    @Override
    public void onSucceed(int what, String response) {
        try {
            List<InvoiceHistoryBean> beanList = JSON.parseArray(response, InvoiceHistoryBean.class);
            if (page > 1) {
                mList.addAll(beanList);
                refreshLayout.finishLoadmore();
            } else {
                if (mList != null) {
                    mList.clear();
                }
                mList.addAll(beanList);
                refreshLayout.finishRefreshing();
            }
            setTripRecordAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (page > 1) {
            refreshLayout.finishLoadmore();
            if (code == 205) {
                ToastUtil.showToast(this, "没有更多数据");
            }
        } else {
            refreshLayout.finishRefreshing();
            emptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void setTripRecordAdapter() {
        tripRecordAdapter = new InvoiceHisAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(tripRecordAdapter);
    }
}
