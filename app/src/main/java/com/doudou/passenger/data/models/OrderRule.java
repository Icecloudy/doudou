package com.doudou.passenger.data.models;

import java.util.List;

/**
 * Created by Icefly on 2017/11/9.
 */

public class OrderRule {
    private double began;           //起步价
    private double mileage;         //公里数单价
    private double duration;        //时长单价
    private List<Remote> remote;    //远途公里数
    private List<Times> times;      //夜间时间
    private double night;           //夜间行驶费
    private double additional;      //附加费用

    public double getBegan() {
        return began;
    }

    public void setBegan(double began) {
        this.began = began;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<Remote> getRemote() {
        return remote;
    }

    public void setRemote(List<Remote> remote) {
        this.remote = remote;
    }

    public List<Times> getTimes() {
        return times;
    }

    public void setTimes(List<Times> times) {
        this.times = times;
    }

    public double getNight() {
        return night;
    }

    public void setNight(double night) {
        this.night = night;
    }

    public double getAdditional() {
        return additional;
    }

    public void setAdditional(double additional) {
        this.additional = additional;
    }

    public static class Remote {
        private int distance;
        private double extraCost;

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public double getExtraCost() {
            return extraCost;
        }

        public void setExtraCost(double extraCost) {
            this.extraCost = extraCost;
        }
    }

    public static class Times {
        private String interval;

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }
    }
}
