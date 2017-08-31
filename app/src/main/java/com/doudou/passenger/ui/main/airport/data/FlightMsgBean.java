package com.doudou.passenger.ui.main.airport.data;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class FlightMsgBean {
    private FlightInfoBean info;
    private List<FlightList> list;

    public FlightInfoBean getInfo() {
        return info;
    }

    public void setInfo(FlightInfoBean info) {
        this.info = info;
    }

    public List<FlightList> getList() {
        return list;
    }

    public void setList(List<FlightList> list) {
        this.list = list;
    }
}
