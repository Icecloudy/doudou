package com.doudou.passenger.data;

import android.content.Context;
import android.content.SharedPreferences;

public class BaseDataPreference {

    SharedPreferences sharedPreferences;
    public static final String FILE_NAME = "lexing_passenger_data";

    public BaseDataPreference(Context context, String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
    }

    public BaseDataPreference() {
    }


    public BaseDataPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }

    public void setLoginMobile(String value) {
        setDataString("login_mobile", value);
    }

    public String getLoginMobile() {
        return getDataString("login_mobile", "");
    }

    //以后引导页要用到版本更新,第一次进入...
    public void setIsFirstOpenApp(boolean b) {
        sharedPreferences.edit().putBoolean("first_open_app", b).apply();
    }

    public boolean getIsFirstOpenApp() {
        return sharedPreferences.getBoolean("first_open_app", false);
    }

    public void setDataString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getDataString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public String getDataString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setDataLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getDataLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public long getDataLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void setDataInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getDataInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public int getDataInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setDataBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getDataBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public boolean getDataBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void clearData() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * 移除某个数据
     *
     * @param key
     */
    public void removeData(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

}
