package com.doudou.passenger.data;

import android.content.Context;


/**
 * Created by jimmy on 2016/11/14.
 */

public class UserDataPreference extends BaseDataPreference {
    public UserDataPreference(Context context) {
        super();
        GlobalPreference globalSp = new GlobalPreference(context);
        int uid = globalSp.getCurrentUid();
        sharedPreferences = context.getSharedPreferences("user_data_" + uid,
                Context.MODE_PRIVATE);

    }

    public String getToken() {
        return getDataString("token");
    }

    public void setToken(String token) {
        setDataString("token", token);
    }


    public void setAccount(String mobile) {
        setDataString("mobile", mobile);
    }

    public String getAccount() {
        return getDataString("mobile");
    }


    public String getUserInfo() {
        return getDataString("userInfo", "");
    }

    public void setUserInfo(String s) {

        setDataString("userInfo", s);
    }

    public String getDefaultAddress(int type) {
        return getDataString("userInfo"+type, "");
    }

    public void setDefaultAddress(int type,String s) {

        setDataString("userInfo"+type, s);
    }

    public String getOrderDriverData(){
        return getDataString("orderDriverData", "");
    }
    public void setOrderDriverData(String s) {

        setDataString("orderDriverData", s);
    }
    public void setOrderState(int state){
        setDataInt("order_state",state);
    }
    public int getOrderState(){
        return getDataInt("order_state");
    }
    public void setOrderId(int id){
        setDataInt("order_id",id);
    }
    public int getOrderId(){
        return getDataInt("order_id");
    }
    public void setStartPosition(String state){
        setDataString("start_position",state);
    }
    public String getStartPosition(){
        return getDataString("start_position");
    }


    public void clear() {
        // 退出登录如果同时选择清空数据，使用此方法
        sharedPreferences.edit().clear().apply();
    }
    public void reMoveKry(String key) {
        // 退出登录如果同时选择清空数据，使用此方法
        super.removeData(key);
    }
    public void setVoice(boolean voice){
        setDataBoolean("remind", voice);
    }
    public boolean getVoice(){
        return  getDataBoolean("remind");
    }
}
