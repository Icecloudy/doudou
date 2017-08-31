package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/3.\
 * id(int) 订单id
 * nmber(string) 订单号
 * addresses(string) 上车地址
 * latitude(double) 上车纬度
 * longitude(double) 上车经度
 * down(string) 下车地址
 * latitudel(double) xia车纬度
 * longitudel(double) xia车经度
 * uid(int) 乘客id
 * did(int) 司机id
 * status(int) 订单状态[1未接,2已接,3载客中,4已到达,5已结单,6已评价,7取消]
 * <p>
 * gotime(string) 开始收费时间(2017-05-02 18:40:08)
 * price(double) 车费
 * placetime(string) 下单时间(2017-05-02 18:40:08)
 * overtime(string) 结束收费时间(2017-05-02 18:40:08)
 * cost(double) 预估费用
 * distance(double) 行驶距离(公里)
 *
 */

public class OrderDetailBean {
    private int id;
    private String nmber;
    private String addresses;
    private double latitude;
    private double longitude;
    private String down;
    private double latitudel;
    private double longitudel;
    private int uid;
    private int did;
    private int status;

    private String gotime;
    private double price;
    private String placetime;
    private String overtime;
    private double cost;
    private double distance;

    private double dlat;
    private double dlng;
    private int type;


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNmber() {
        return nmber;
    }

    public void setNmber(String nmber) {
        this.nmber = nmber;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public double getLatitudel() {
        return latitudel;
    }

    public void setLatitudel(double latitudel) {
        this.latitudel = latitudel;
    }

    public double getLongitudel() {
        return longitudel;
    }

    public void setLongitudel(double longitudel) {
        this.longitudel = longitudel;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGotime() {
        return gotime;
    }

    public void setGotime(String gotime) {
        this.gotime = gotime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPlacetime() {
        return placetime;
    }

    public void setPlacetime(String placetime) {
        this.placetime = placetime;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
