package com.lexing.passenger.ui.profile.record;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lexing.passenger.R;
import com.lexing.passenger.data.UserDataPreference;
import com.lexing.passenger.nohttp.BaseRequest;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.utils.ConfigUtil;
import com.lexing.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderFragment extends Fragment implements HttpListener {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    Unbinder unbinder;
    private View layout;

    private int page = 1;
    private List<TripRecordBean> mList = new ArrayList<>();
    UserDataPreference userDataPreference;
    TripRecordAdapter tripRecordAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (layout == null) {
            layout = inflater.inflate(R.layout.fragment_real_time, container, false);
        }
        unbinder = ButterKnife.bind(this, layout);
        userDataPreference = new UserDataPreference(getActivity());
        getOrderRecord(page);

        setRefreshLayout();
        return layout;
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
     *
     * @param page
     * type 订单类型[1实时,2预约] 司机端不用传该参数
     */
    private void getOrderRecord(int page) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_TRIP_RECORD)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype",1)
                .add("type",2)
                .add("page", page);
        CallServer.getRequestInstance().add(getActivity(), 0, baseRequest, this, false, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            List<TripRecordBean> beanList = JSON.parseArray(response, TripRecordBean.class);
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
                ToastUtil.showToast(getActivity(),"没有更多数据");
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
        tripRecordAdapter = new TripRecordAdapter(mList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(tripRecordAdapter);
    }
}
