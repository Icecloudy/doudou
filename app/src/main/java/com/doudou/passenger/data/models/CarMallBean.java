package com.doudou.passenger.data.models;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 * id(int) 品牌id，
 * brand(string) 品牌名称，
 * picture(string) 品牌图片,
 * hot(int) 点击量
 */

public class CarMallBean {
    private List<AllCarList> list;
    private List<CarBean> hot;

    public List<AllCarList> getList() {
        return list;
    }

    public void setList(List<AllCarList> list) {
        this.list = list;
    }

    public List<CarBean> getHot() {
        return hot;
    }

    public void setHot(List<CarBean> hot) {
        this.hot = hot;
    }
}
