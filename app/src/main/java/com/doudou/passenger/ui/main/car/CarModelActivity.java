package com.doudou.passenger.ui.main.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.models.CarModelBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarModelActivity extends BaseActivity implements HttpListener {

    @BindView(R.id.carModelListView)
    ExpandableListView carModelListView;
    private String carId;

    private CarModelAdapter carModelAdapter;
    private List<CarModelBean> mCar = new ArrayList<>();

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_car_model);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("title");
            setTitle(title);
            carId = getIntent().getExtras().getString("id");
            getCarModel(carId);
        }
    }


    private void getCarModel(String id) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_MODEL)
                .add("id", id);
        request(0, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        try {
            mCar = JSON.parseArray(response, CarModelBean.class);
            setCarModelAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        showMsg(msg);
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    private void setCarModelAdapter() {
        carModelAdapter = new CarModelAdapter(this, mCar);
        carModelListView.setAdapter(carModelAdapter);

        carModelListView.setGroupIndicator(null);
        if (mCar != null && mCar.size() > 0) {
            for (int i = 0; i < mCar.size(); i++) {
                carModelListView.expandGroup(i);
            }
        }
        carModelListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //TODO
                Intent intent = new Intent(CarModelActivity.this, CarDetailsActivity.class);
                intent.putExtra("id", carId);
                startActivity(intent);
                return false;
            }
        });

    }

}
