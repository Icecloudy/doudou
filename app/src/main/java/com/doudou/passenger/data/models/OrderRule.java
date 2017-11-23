package com.doudou.passenger.data.models;

/**
 * Created by Icefly on 2017/11/9.
 */

public class OrderRule {
    private double start_price;          //起步价
    private double price;                //公里数单价
    private double time_price;           //时长单价
    private double remote = 10;          //远途公里数
    private double long_distance_price;
    private double night_price;          //夜间行驶费
    private double extra_price;          //附加费用
    private int type;

    public double getStart_price() {
        return start_price;
    }

    public void setStart_price(double start_price) {
        this.start_price = start_price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTime_price() {
        return time_price;
    }

    public void setTime_price(double time_price) {
        this.time_price = time_price;
    }

    public double getRemote() {
        return remote;
    }

    public void setRemote(double remote) {
        this.remote = remote;
    }

    public double getLong_distance_price() {
        return long_distance_price;
    }

    public void setLong_distance_price(double long_distance_price) {
        this.long_distance_price = long_distance_price;
    }

    public double getNight_price() {
        return night_price;
    }

    public void setNight_price(double night_price) {
        this.night_price = night_price;
    }

    public double getExtra_price() {
        return extra_price;
    }

    public void setExtra_price(double extra_price) {
        this.extra_price = extra_price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
