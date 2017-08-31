package com.doudou.passenger.ui.profile.message;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends BaseActivity implements View.OnClickListener, HttpListener {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;
    private List<NewsBean> mList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    UserDataPreference userDataPreference;
    private static final int GET_NEWS = 0x001;
    private static final int CLEAN_NEWS = 0x002;
    int id;
    int position;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_news);
        setTitle("我的消息");
        setRightText("清空", this);
        setRightTextColor(R.color.text_hint);
        ButterKnife.bind(this);
        userDataPreference = new UserDataPreference(this);
        getNews();
    }


    private void getNews() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.MESSAGE)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype", 1);
        request(GET_NEWS, baseRequest, this, false, true);
    }

    private void cleanNews(int id) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.CLEAN_NEWS)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype", 1);
        if (id!=0){
            baseRequest.add("id",id);
        }
        request(CLEAN_NEWS, baseRequest, this, false, true);
    }


    @Override
    public void onClick(View v) {
        id=0;
        cleanNews(id);
    }

    private void setNewsAdapter() {
        newsAdapter = new NewsAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void clickListener(View view, int i) {
                id=mList.get(i).getId();
                position = i;
                cleanNews(id);
            }
        });
    }


    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_NEWS) {
            try {
                mList = JSON.parseArray(response, NewsBean.class);
                setNewsAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == CLEAN_NEWS) {
            if (id==0){
                newsAdapter.removeAllData();
                setRightTextGone();
                emptyView.setVisibility(View.VISIBLE);
            }else{
                newsAdapter.removeData(position);
            }
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == GET_NEWS) {
            if (code==205){
                emptyView.setVisibility(View.VISIBLE);
                setRightTextGone();
            }
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

}
