package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/22.
 * id(int) 品牌id，
 * brand(string) 品牌名称，
 * picture(string) 品牌图片,
 * hot(int) 点击量
 */

public class CarBean {
    private String id;
    private String brand;
    private String picture;
    private int hot;
    private String first;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
