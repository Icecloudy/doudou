package com.doudou.passenger.ui.main.airport.data;

import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 */

public class NewFlightMsgBean {
    private String FlightNo;
    private String cityNumber;
    private String flightDate;
    private String planeModel;
    private VIACities VIACities;

    public String getFlightNo() {
        return FlightNo;
    }

    public void setFlightNo(String flightNo) {
        FlightNo = flightNo;
    }

    public String getCityNumber() {
        return cityNumber;
    }

    public void setCityNumber(String cityNumber) {
        this.cityNumber = cityNumber;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void setPlaneModel(String planeModel) {
        this.planeModel = planeModel;
    }

    public com.doudou.passenger.ui.main.airport.data.VIACities getVIACities() {
        return VIACities;
    }

    public void setVIACities(com.doudou.passenger.ui.main.airport.data.VIACities VIACities) {
        this.VIACities = VIACities;
    }
}
