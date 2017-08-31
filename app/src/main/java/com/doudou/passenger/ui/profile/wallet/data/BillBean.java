package com.doudou.passenger.ui.profile.wallet.data;

/**
 * Created by Administrator on 2017/4/13.
 * aid(int) 编号,
 * title(string) 消息标题,
 * type(int) 交易类型[101收入,102支出],
 * money(double) 交易额,
 * surplus(double) 交易后剩余,
 * createtime(string) 创建日期,
 * payment(int) 支付类型[1支付宝,2微信,4余额,5积分],
 * remark(string) 备注,
 * status(int) 状态[101未审,102已审]
 */

public class BillBean {
    private int aid;
    private int userid;
    private String title;
    private int usertype;
    private int type;

    private double money;
    private double surplus;
    private String createtime;
    private int payment;
    private String remark;
    private int status;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getSurplus() {
        return surplus;
    }

    public void setSurplus(double surplus) {
        this.surplus = surplus;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
