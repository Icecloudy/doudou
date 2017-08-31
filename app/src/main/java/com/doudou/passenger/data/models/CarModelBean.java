package com.doudou.passenger.data.models;

import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */

public class CarModelBean {

    private CarModelGroupBean type;

    private List<CarModelChildBean> model;

    public List<CarModelChildBean> getModel() {
        return model;
    }

    public void setModel(List<CarModelChildBean> model) {
        this.model = model;
    }

    public CarModelGroupBean getType() {
        return type;
    }

    public void setType(CarModelGroupBean type) {
        this.type = type;
    }

}
