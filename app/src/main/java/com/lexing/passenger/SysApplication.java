package com.lexing.passenger;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/12.
 */

public class SysApplication extends Application {
    private ArrayList<Activity> mList = new ArrayList<>();
    private ArrayList<Activity> mInvoiceList = new ArrayList<>();
    private static SysApplication instance;

    private SysApplication() {
    }


    public synchronized static SysApplication getInstance() {
        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void addInvoiceActivity(Activity activity) {
        mInvoiceList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishInvoice() {
        try {
            for (Activity activity : mInvoiceList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
