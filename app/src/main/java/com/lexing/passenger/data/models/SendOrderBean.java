package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/4.、
 * 叫车成功后返回的订单信息
 */

public class SendOrderBean {
    private int id;
    private String nmber;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNmber() {
        return nmber;
    }

    public void setNmber(String nmber) {
        this.nmber = nmber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
