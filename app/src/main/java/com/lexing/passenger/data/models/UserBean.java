package com.lexing.passenger.data.models;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 * data{
 * id: 用户id,
 * nickname昵称,
 * account手机号,
 * integral积分,
 * balance余额,
 * photo头像,
 * sex性别[
 * 1男,
 * 0女
 * ],
 * truename真实姓名,
 * idcard身份证号码,
 * token用户凭证
 * }
 */

public class UserBean {
    private int id;
    private String nickname;
    private String account;
    private int integral;
    private double balance;
    private String photo;
    private String truename;
    private String idcard;
    private String token;
    private int sex;

    private String hotline;

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }
    private List<Unpayorder> unpayorder;

    public List<Unpayorder> getUnpayorder() {
        return unpayorder;
    }

    public void setUnpayorder(List<Unpayorder> unpayorder) {
        this.unpayorder = unpayorder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
