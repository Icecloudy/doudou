package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/4/28.
 * payload(string):'payment' 前往支付订单
 * price(double) 车费
 * minutes(int) 行程时间(分钟)
 * orderid(int) 订单id
 * nmber(string) 订单号
 * did(int) 司机id
 */

public class OrderPayBean {
    private String payload;
    private double price;
    private int minutes;
    private int orderid;
    private String nmber;
    private int did;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getNmber() {
        return nmber;
    }

    public void setNmber(String nmber) {
        this.nmber = nmber;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }
}
