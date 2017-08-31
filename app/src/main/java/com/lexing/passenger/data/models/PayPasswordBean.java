package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/2.
 */

public class PayPasswordBean {
    private int paypass;//0未设置密码,1已设置
    private int unpaypass;//0免密(默认),1需密
    private int pass;// 0密码不匹配,1匹配

    public int getPaypass() {
        return paypass;
    }

    public void setPaypass(int paypass) {
        this.paypass = paypass;
    }

    public int getUnpaypass() {
        return unpaypass;
    }

    public void setUnpaypass(int unpaypass) {
        this.unpaypass = unpaypass;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }
}
