package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/4.
 */

public class OrderCancelBean {
    private int orderid;
    private String reason;
    private String payload;
    private OrderDriverBean user;

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public OrderDriverBean getUser() {
        return user;
    }

    public void setUser(OrderDriverBean user) {
        this.user = user;
    }
}
