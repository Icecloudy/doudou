package com.doudou.passenger.ui.main.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.models.AllCarList;
import com.doudou.passenger.data.models.BannerBean;
import com.doudou.passenger.data.models.CarBean;
import com.doudou.passenger.data.models.CarMallBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.views.CarSlideBar;
import com.doudou.passenger.views.WrapHeightGridView;
import com.squareup.picasso.Picasso;
import com.yolanda.nohttp.rest.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarListActivity extends BaseActivity implements HttpListener {
    @BindView(R.id.listview_all_city)
    ExpandableListView listviewAllCity;
    @BindView(R.id.tv_letter_overlay)
    TextView tvLetterOverlay;
    @BindView(R.id.side_letter_bar)
    CarSlideBar mLetterBar;

    private WrapHeightGridView gridView;
    private Banner banner;

    CarListAdapter carListAdapter;
    private List<AllCarList> mCar = new ArrayList<>();

    HotCarGridAdapter hotCarGridAdapter;
    private List<CarBean> mHotCar = new ArrayList<>();

    private List<String> imgUrl = new ArrayList<>();

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_car_list);
        setTitle("豆豆车行");

        ButterKnife.bind(this);

        addHeadView();
        getBanner();
        getCarList();

    }

    /**
     * 添加头
     */
    private void addHeadView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_hot_car, null);
        banner = ButterKnife.findById(headView, R.id.banner);
        gridView = ButterKnife.findById(headView, R.id.gridview_hot_car);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Picasso.with(context).load((String) path).into(imageView);
            }
        });
        listviewAllCity.addHeaderView(headView);
        mLetterBar.setOverlay(tvLetterOverlay);
        mLetterBar.setOnLetterChangedListener(new CarSlideBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = carListAdapter.getLetterPosition(letter);
                listviewAllCity.setSelection(position);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CarListActivity.this, CarModelActivity.class);
                intent.putExtra("title", mHotCar.get(position).getBrand());
                intent.putExtra("id", mHotCar.get(position).getId());
                startActivity(intent);
            }
        });

        listviewAllCity.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent intent = new Intent(CarListActivity.this, CarModelActivity.class);
                intent.putExtra("title", mCar.get(groupPosition).getBrands().get(childPosition).getBrand());
                intent.putExtra("id", mCar.get(groupPosition).getBrands().get(childPosition).getId());
                startActivity(intent);

                return false;
            }
        });

    }

    private static final int GET_CAR = 0x001;
    private static final int GET_BANNER = 0x002;

    private void getCarList() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_LIST);
        request(GET_CAR, baseRequest, this, false, true);
    }

    private void getBanner() {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_BANNER);
        request(GET_BANNER, baseRequest, this, false, false);
    }

    int i = 0;

    @Override
    public void onSucceed(int what, String response) {

        if (what == GET_CAR) {
            try {
                CarMallBean carMallBean = JSON.parseObject(response, CarMallBean.class);
                setHotCarGridAdapter(carMallBean);
                setCarListAdapter(carMallBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == GET_BANNER) {
            try {
                List<BannerBean> bannerList = JSON.parseArray(response, BannerBean.class);
                setBanner(bannerList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void setHotCarGridAdapter(CarMallBean carMallBean) {
        mHotCar = carMallBean.getHot();
        hotCarGridAdapter = new HotCarGridAdapter(this, mHotCar);
        gridView.setAdapter(hotCarGridAdapter);
    }

    private void setCarListAdapter(CarMallBean carMallBean) {
        mCar = carMallBean.getList();
        carListAdapter = new CarListAdapter(this, mCar);
        listviewAllCity.setAdapter(carListAdapter);
        listviewAllCity.setGroupIndicator(null);
        if (mCar != null && mCar.size() > 0) {
            for (int i = 0; i < mCar.size(); i++) {
                listviewAllCity.expandGroup(i);
            }
        }
        listviewAllCity.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    private void setBanner(List<BannerBean> advData) {
        for (int i = 0; i < advData.size(); i++) {
            imgUrl.add(advData.get(i).getPicture());
        }
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImages(imgUrl)
                .start();
    }


}
