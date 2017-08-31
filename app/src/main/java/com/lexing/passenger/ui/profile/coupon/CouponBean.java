package com.lexing.passenger.ui.profile.coupon;

/**
 * Created by Administrator on 2017/5/22.
 * aid(int) 优惠券id,
 * money(double) 金额,
 * type(double) 类型[1代金券,2折扣券],
 * top(int) 折扣券最高抵消金额（0不封顶）,
 * discount(double) 折,
 * starttime(string) 生效日期,
 * endtime(string) 失效日期,
 * status(int) 状态[1可用,2已用,3失效]
 */

public class CouponBean {
    private int aid;
    private double money;
    private int type;
    private int top;
    private double discount;
    private String starttime;
    private String endtime;
    private int status;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
