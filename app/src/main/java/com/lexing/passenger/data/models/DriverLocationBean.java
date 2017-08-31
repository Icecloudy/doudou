package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/4/23.
 */

public class DriverLocationBean {
    private int id;
    private double dlat;
    private double dlng;
    private String username;
    private String type;
    private String brand;
    private String carnum;
    private String color;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDlat() {
        return dlat;
    }

    public void setDlat(double dlat) {
        this.dlat = dlat;
    }

    public double getDlng() {
        return dlng;
    }

    public void setDlng(double dlng) {
        this.dlng = dlng;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
