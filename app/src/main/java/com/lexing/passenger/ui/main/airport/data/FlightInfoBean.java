package com.lexing.passenger.ui.main.airport.data;

/**
 * Created by Administrator on 2017/6/1.
 * "from": "两江国际机场", /*始发
 * "to":"烟台蓬莱国际机场", /*终点
 * "fno":"SC4728", /*航班号
 * "company":"山东航空", /*航空公司
 * "date":"2015-06-05", /*日期
 * "zql":"26.67%", /*准确率
 * "from_weather":"阴", /*始发 天气
 * "to_weather":"多云", /*终点 天气
 * "from_weather_id":"02", /*始发天气 标识
 * "to_weather_id":"01" /*终点天气 标识
 */


public class FlightInfoBean {
    private String from;
    private String to;
    private String fno;
    private String company;
    private String date;
    private String zql;
    private String from_weather;
    private String to_weather;
    private String from_weather_id;
    private String to_weather_id;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFno() {
        return fno;
    }

    public void setFno(String fno) {
        this.fno = fno;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getZql() {
        return zql;
    }

    public void setZql(String zql) {
        this.zql = zql;
    }

    public String getFrom_weather() {
        return from_weather;
    }

    public void setFrom_weather(String from_weather) {
        this.from_weather = from_weather;
    }

    public String getTo_weather() {
        return to_weather;
    }

    public void setTo_weather(String to_weather) {
        this.to_weather = to_weather;
    }

    public String getFrom_weather_id() {
        return from_weather_id;
    }

    public void setFrom_weather_id(String from_weather_id) {
        this.from_weather_id = from_weather_id;
    }

    public String getTo_weather_id() {
        return to_weather_id;
    }

    public void setTo_weather_id(String to_weather_id) {
        this.to_weather_id = to_weather_id;
    }
}
