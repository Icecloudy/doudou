package com.lexing.passenger.ui.profile.wallet.data;

import android.widget.Checkable;

/**
 * Created by Administrator on 2017/4/13.
 */

public class MoneyBean implements Checkable {
    private int payMoney;
    private int giveMoney;
    private boolean isChecked;

    public MoneyBean() {
    }

    public MoneyBean(int payMoney, int giveMoney, boolean isChecked) {
        this.payMoney = payMoney;
        this.giveMoney = giveMoney;
        this.isChecked = isChecked;
    }

    public double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(int payMoney) {
        this.payMoney = payMoney;
    }

    public double getGiveMoney() {
        return giveMoney;
    }

    public void setGiveMoney(int giveMoney) {
        this.giveMoney = giveMoney;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        isChecked = !isChecked;
    }
}
