package com.lexing.passenger.ui.profile.record;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripRecordActivity extends BaseActivity {

    @BindView(R.id.tab_record_title)
    TabLayout tabRecordTitle;
    @BindView(R.id.vp_record_pager)
    ViewPager vpRecordPager;

    private RecordvpAdapter recordvpAdapter;
    private List<Fragment> list_fragment;                         //fragment列表
    private List<String> list_Title;
    private RealTimeFragment realTimeFragment;
    private OrderFragment orderFragment;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_trip_record);
        ButterKnife.bind(this);
        initData();
        getmToolbar().setVisibility(View.GONE);
    }

    private void initData(){
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_Title = new ArrayList<>();
        list_Title.add("实时");
        list_Title.add("预约");

        //初始化各fragment
        realTimeFragment = new RealTimeFragment();
        orderFragment = new OrderFragment();
        list_fragment = new ArrayList<>();

        list_fragment.add(realTimeFragment);
        list_fragment.add(orderFragment);


        //设置TabLayout的模式
        tabRecordTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        tabRecordTitle.addTab(tabRecordTitle.newTab().setText(list_Title.get(0)));
        tabRecordTitle.addTab(tabRecordTitle.newTab().setText(list_Title.get(1)));

        recordvpAdapter = new RecordvpAdapter(getSupportFragmentManager(),list_fragment,list_Title);
        //viewpager加载adapter
        vpRecordPager.setAdapter(recordvpAdapter);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tabRecordTitle.setupWithViewPager(vpRecordPager);
        //tab_FindFragment_title.set
    }

    @OnClick(R.id.imgBack)
    public void onViewClicked() {
        finish();
    }
}
