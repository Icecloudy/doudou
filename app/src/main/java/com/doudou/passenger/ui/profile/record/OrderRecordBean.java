package com.doudou.passenger.ui.profile.record;

import com.doudou.passenger.data.models.OrderDriverBean;

/**
 * Created by Administrator on 2017/5/5.
 * orderid(int) 订单id,
 * addresses(string) 出发地,
 * down(string) 终点,
 * status(int) 订单状态[1未接,2已接,3载客中,4已到达,5已结单,6已评价,7取消],
 * gotime(string) 开始收费时间(2017-05-02 18:40:08)
 * overtime(string) 结束收费时间(2017-05-02 18:40:08)
 * placetime(string) 下单时间(2017-05-02 18:40:08)
 * type(int) 订单类型 [1实时,2预约,3指派]
 * uid(int) 用户id
 * did(int) 司机id
 * <p>
 * //两个端都有返回，但目前只有司机端会用到的：
 * price(double) 总金额
 * nmber(string) 订单号
 * minutes(int) 累计时长（分钟）
 * distance(double) 用车里程
 * <p>
 * <p>
 * //司机端额外获取的用户信息如下：
 * photo(string) 用户头像
 * phone(string) 用户手机
 * nickname(string) 用户姓名
 * // 用户端额外获取的司机信息如下：
 * （这里返回的数据与这个接口获取的基本相同：用户端获取司机个人信息getDriverByOrder）
 * driver{
 * photo(string) 司机头像
 * username 司机昵称
 * score(double) 评分
 * countorders(int) 接单总数
 * orders(int) 今日接单
 * carnum(string) 车牌号
 * color(string) 颜色
 * brand(string) 车型
 * type(int) 车辆分类[1社会车,2自营专车,3高端车]
 * phone(string) 司机电话
 * service(double) 服务分
 * }
 */

public class OrderRecordBean {
    private int orderid;
    private String addresses;
    private String down;
    private int status;
    private String gotime;
    private String overtime;
    private String placetime;
    private int type;
    private int did;

    private int uid;
    private double price;
    private String nmber;
    private int minutes;
    private double distance;

    private String photo;
    private String phone;
    private String nickname;
    private String reason;

    private double latitude;
    private double longitude;
    private double latitudel;
    private double longitudel;

    private OrderDriverBean driver;
    private String booktime;

    public String getBooktime() {
        return booktime;
    }

    public void setBooktime(String booktime) {
        this.booktime = booktime;
    }

    public OrderDriverBean getDriver() {
        return driver;
    }

    public void setDriver(OrderDriverBean driver) {
        this.driver = driver;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public String getPlacetime() {
        return placetime;
    }

    public void setPlacetime(String placetime) {
        this.placetime = placetime;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNmber() {
        return nmber;
    }

    public void setNmber(String nmber) {
        this.nmber = nmber;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
