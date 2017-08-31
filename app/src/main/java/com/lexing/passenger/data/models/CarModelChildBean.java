package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/23.
 * "mid": "7",
 * "model": "奥迪RS 5",
 * "photo": "http://lexing.51edn.com./Public/Uploads/20170512/5915a725e71d4.jpg",
 * "allocation": "4.0T   双离合",
 * "max": "150.6",
 * "min": null
 *
 *
 */

public class CarModelChildBean {
    private String mid;
    private String model;
    private String photo;
    private String allocation;
    private String max;
    private String min;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAllocation() {
        return allocation;
    }

    public void setAllocation(String allocation) {
        this.allocation = allocation;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
