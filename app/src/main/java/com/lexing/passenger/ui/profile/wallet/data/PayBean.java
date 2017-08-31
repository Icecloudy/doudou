package com.lexing.passenger.ui.profile.wallet.data;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 * {
 * "integralper": 0,
 * "money": 0,
 * "moneyList": [
 * 100,
 * 300,
 * 500,
 * 1000,
 * 3000,
 * 5000
 * ]
 * }
 */

public class PayBean {
    private double integralper;
    private double money;
    private List<Integer> moneyList;

    public double getIntegralper() {
        return integralper;
    }

    public void setIntegralper(double integralper) {
        this.integralper = integralper;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<Integer> getMoneyList() {
        return moneyList;
    }

    public void setMoneyList(List<Integer> moneyList) {
        this.moneyList = moneyList;
    }
}
