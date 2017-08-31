package com.doudou.passenger.data.models;

/**
 * Created by Administrator on 2017/4/27.
 * payload(string):'shipping' 司机正前来接驾
 * photo(string) 司机头像
 * username 司机昵称
 * score(double) 评分
 * countorders(int) 接单总数
 * carnum(string) 车牌号
 * color(string) 颜色
 * brand(string) 车型
 * type(int) 车辆分类[1社会车,2自营专车,3高端车]
 * phone(string) 司机电话
 * orderid(int) 订单id
 * dlat(double) 司机纬度
 * dlng(double) 司机经度
 */

public class OrderDriverBean {
    private String payload;
    private String photo;
    private String username;
    private double score;
    private int countorders;
    private String carnum;
    private String color;
    private String brand;
    private int type;
    private String phone;
    private int orderid;
    private double dlat;
    private double dlng;
    private String did;

    private String name;
    private double service;

    public double getService() {
        return service;
    }

    public void setService(double service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCountorders() {
        return countorders;
    }

    public void setCountorders(int countorders) {
        this.countorders = countorders;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public double getDlat() {
        return dlat;
    }

    public void setDlat(double dlat) {
        this.dlat = dlat;
    }

    public double getDlng() {
        return dlng;
    }

    public void setDlng(double dlng) {
        this.dlng = dlng;
    }
}
