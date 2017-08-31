package com.doudou.passenger.ui.profile.score;

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
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.ui.profile.wallet.adapter.BillAdapter;
import com.doudou.passenger.ui.profile.wallet.data.BillBean;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyScoreActivity extends BaseActivity implements HttpListener{

    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.tvScoreCount)
    TextView tvScoreCount;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    BillAdapter billAdapter;
    private List<BillBean> mList = new ArrayList<>();


    UserDataPreference userDataPreference;
    private int page = 1;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_score);
        setTitle(R.string.my_score);
        ButterKnife.bind(this);

        userDataPreference = new UserDataPreference(this);
        UserBean userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        tvScoreCount.setText(userBean.getIntegral() + "");

        getBill(page);
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
                        getBill(page);

                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getBill(page);

                    }
                }, 2000);
            }
        });
    }

    private void setBillAdapter() {
        billAdapter = new BillAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(billAdapter);
    }

    /**
     * @param page
     */
    private void getBill(int page) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_SCORE_LIST)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("page", page);
        request(0, baseRequest, this, false, true);

    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            List<BillBean> beanList = JSON.parseArray(response, BillBean.class);
            if (page>1){
                mList.addAll(beanList);
                refreshLayout.finishLoadmore();
            }else{
                if (mList!=null){
                    mList.clear();
                }
                mList.addAll(beanList);
                refreshLayout.finishRefreshing();
            }
            setBillAdapter();

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
}
