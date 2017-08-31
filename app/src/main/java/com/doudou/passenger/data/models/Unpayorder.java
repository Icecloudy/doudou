package com.doudou.passenger.data.models;

/**
 * Created by Administrator on 2017/5/3.
 * orderid(int) 订单id
 * status(int) 订单状态[1未接,2已接,3载客中,4已到达,5已结单,6已评价,7取消] status<5
 */

public class Unpayorder {
    private int orderid;
    private int status;

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
