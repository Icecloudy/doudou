package com.lexing.passenger.ui.profile.invoice.adapter;

import com.lexing.passenger.ui.profile.record.TripRecordBean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class InvoiceRecordBean {
    private String date;
    private List<TripRecordBean> orderList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TripRecordBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TripRecordBean> orderList) {
        this.orderList = orderList;
    }
}
