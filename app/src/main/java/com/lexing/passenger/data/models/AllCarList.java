package com.lexing.passenger.data.models;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 * id(int) 品牌id，
 * brand(string) 品牌名称，
 * picture(string) 品牌图片,
 * hot(int) 点击量
 */

public class AllCarList {
    public static final int TYPE_VISIBILITY= 1;
    public static final int TYPE_GONE = 2;
    private int type;
    private String title;
    private List<CarBean> brands;

    public AllCarList() {
    }
    public AllCarList(String title, List<CarBean> brands) {
        this.title = title;
        this.brands = brands;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CarBean> getBrands() {
        return brands;
    }

    public void setBrands(List<CarBean> brands) {
        this.brands = brands;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
