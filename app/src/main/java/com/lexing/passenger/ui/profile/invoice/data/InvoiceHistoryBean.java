package com.lexing.passenger.ui.profile.invoice.data;

/**
 * Created by Administrator on 2017/4/18.
 * data {
 * departure(string) 出发地,
 * destination(string) 目的地,
 * money(double) 金额,
 * createtime(string) 开票日期
 * key(string) 支付订单号，
 * payment(int) 支付方式[1支付宝,2微信,3货到付款(默认),4包邮]
 * status(int) 付款状态[1已付,2未付(默认)]
 * }
 */

public class InvoiceHistoryBean {
    private String departure;
    private String destination;
    private double money;
    private String createtime;
    private String key;
    private int payment;
    private int status;

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
