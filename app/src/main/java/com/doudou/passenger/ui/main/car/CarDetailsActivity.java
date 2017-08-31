package com.doudou.passenger.ui.main.car;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.R;
import com.doudou.passenger.data.models.CarDetailsBean;
import com.doudou.passenger.data.models.CarModelChildBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.BaseActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.squareup.picasso.Picasso;
import com.yolanda.nohttp.rest.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarDetailsActivity extends BaseActivity implements HttpListener {

    @BindView(R.id.carDetailsListView)
    ListView carDetailsListView;


    ImageView carPhoto;
    TextView guidePrice;
    TextView carName;
    TextView carModel;


    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_car_details);
        ButterKnife.bind(this);
        setTitle("详情");
        addHeadView();
        String id = getIntent().getExtras().getString("id");
        getCarDetail(id);
        getCarDetailList(id);
    }


    private void addHeadView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.headview_car_details, null);
        carPhoto = ButterKnife.findById(headView, R.id.carPhoto);
        guidePrice = ButterKnife.findById(headView, R.id.guidePrice);
        carName = ButterKnife.findById(headView, R.id.carName);
        carModel = ButterKnife.findById(headView, R.id.carModel);

        carDetailsListView.addHeaderView(headView);
    }


    private void getCarDetailList(String id) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_DETAILS_LIST)
                .add("id", id);
        request(100, baseRequest, this, false, true);
    }

    private void getCarDetail(String id) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.GET_CAR_DETAILS)
                .add("id", id);
        request(101, baseRequest, this, false, true);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == 100) {
            try {
                List<CarDetailsBean> childBeanList = JSON.parseArray(response, CarDetailsBean.class);
                setDetailsAdapter(childBeanList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == 101) {
            try {
                List<CarModelChildBean> childBeanList = JSON.parseArray(response, CarModelChildBean.class);
                setDetailsData(childBeanList.get(0));
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

    private void setDetailsAdapter(List<CarDetailsBean> childBeanList) {
        CarDetailsAdapter detailsAdapter = new CarDetailsAdapter(childBeanList, this);
        carDetailsListView.setAdapter(detailsAdapter);
    }

    private void setDetailsData(CarModelChildBean data) {
        guidePrice.setText(getString(R.string.car_guide_price, data.getMin() + "-" + data.getMax()));
        Picasso.with(this)
                .load(data.getPhoto())
                .into(carPhoto);
        carName.setText(data.getModel());
        carModel.setText(data.getAllocation());
    }

}
