package com.lexing.passenger.ui.main.airport.data;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by Administrator on 2017/6/1.
 * "qf": "两江国际机场", /*起飞
 * "dd": "南京禄口国际机场T2", /*到达
 * "jhqftime": "12:00", /*计划起飞时间
 * "sjqftime": "", /*实际起飞时间
 * "jhddtime": "13:50", /*计划到达时间
 * "sjddtime": "", /*实际到达时间
 * "state": "计划", /*状态，计划、起飞、到达、延误、取消
 * "stateid": "1", /*状态标识 1、2、3、4、5
 * "djk": "11", /*登机口
 * "zjgt": "--", /*值机柜台
 * "xlzp": "08" /*行李闸口
 */


public class FlightList implements IPickerViewData {
    private String qf;
    private String dd;

    private String qf_city;
    private String dd_city;

    private String jhqftime;
    private String sjqftime;
    private String jhddtime;
    private String sjddtime;

    private String jhqftime_full;
    private String sjqftime_full;
    private String jhddtime_full;
    private String sjddtime_full;
    private String state;
    private String stateid;
    private String djk;
    private String zjgt;
    private String xlzp;

    public String getQf_city() {
        return qf_city;
    }

    public void setQf_city(String qf_city) {
        this.qf_city = qf_city;
    }

    public String getDd_city() {
        return dd_city;
    }

    public void setDd_city(String dd_city) {
        this.dd_city = dd_city;
    }

    public String getJhqftime_full() {
        return jhqftime_full;
    }

    public void setJhqftime_full(String jhqftime_full) {
        this.jhqftime_full = jhqftime_full;
    }

    public String getSjqftime_full() {
        return sjqftime_full;
    }

    public void setSjqftime_full(String sjqftime_full) {
        this.sjqftime_full = sjqftime_full;
    }

    public String getJhddtime_full() {
        return jhddtime_full;
    }

    public void setJhddtime_full(String jhddtime_full) {
        this.jhddtime_full = jhddtime_full;
    }

    public String getSjddtime_full() {
        return sjddtime_full;
    }

    public void setSjddtime_full(String sjddtime_full) {
        this.sjddtime_full = sjddtime_full;
    }

    public String getQf() {
        return qf;
    }

    public void setQf(String qf) {
        this.qf = qf;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public String getJhqftime() {
        return jhqftime;
    }

    public void setJhqftime(String jhqftime) {
        this.jhqftime = jhqftime;
    }

    public String getSjqftime() {
        return sjqftime;
    }

    public void setSjqftime(String sjqftime) {
        this.sjqftime = sjqftime;
    }

    public String getJhddtime() {
        return jhddtime;
    }

    public void setJhddtime(String jhddtime) {
        this.jhddtime = jhddtime;
    }

    public String getSjddtime() {
        return sjddtime;
    }

    public void setSjddtime(String sjddtime) {
        this.sjddtime = sjddtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateid() {
        return stateid;
    }

    public void setStateid(String stateid) {
        this.stateid = stateid;
    }

    public String getDjk() {
        return djk;
    }

    public void setDjk(String djk) {
        this.djk = djk;
    }

    public String getZjgt() {
        return zjgt;
    }

    public void setZjgt(String zjgt) {
        this.zjgt = zjgt;
    }

    public String getXlzp() {
        return xlzp;
    }

    public void setXlzp(String xlzp) {
        this.xlzp = xlzp;
    }

    @Override
    public String getPickerViewText() {
        return qf_city + " - " + dd_city + "  " + jhqftime + " - " + jhddtime;
    }
}
