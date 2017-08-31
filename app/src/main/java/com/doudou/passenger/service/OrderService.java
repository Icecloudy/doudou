package com.doudou.passenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.OrderDetailBean;
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.Timer;
import java.util.TimerTask;

public class OrderService extends Service implements HttpListener {


    MyTimerTask mTimerTask;
    Timer timer;
    private MyCountDownTimer mc1;
    private MyCountDownTimer2 mc2;

    UserBean userBean = new UserBean();
    UserDataPreference userDataPreference;

    int orderId;
    boolean isToAnother = true;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userDataPreference = new UserDataPreference(this);
        userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        orderId = userDataPreference.getOrderId();
//        mc1 = new MyCountDownTimer(6 * 60 * 1000, 60 * 1000);
//        mc1.start();
//        mc2 = new MyCountDownTimer2(60 * 1000, 1000);
//        mc2.start();
//        handler.sendEmptyMessage(0);
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (timer == null) {
                        if (mTimerTask != null) {
                            mTimerTask.cancel(); // 将原任务从队列中移除
                        }
                        timer = new Timer();
                        mTimerTask = new MyTimerTask(); // 新建一个任务
                        timer.schedule(mTimerTask, 1000 * 10, 1000 * 20); // 经过20s后再次执行
                    }
                    break;
                case 1:
                    getOrderDetails(orderId, userBean.getId());
                    break;
                case 2:
                    if (isToAnother) {
                        isToAnother = false;
                        toAnother(orderId, userBean.getId());
                    }
                    break;
            }

        }
    };

    private static final int TOANOTHER = 0x001;
    private static final int GET_ORDER_DETAILS = 0x002;
    private static final int TOONEDRIVER = 0x003;
    private static final int CANCEL_ORDER = 0x004;

    //用户端获取订单数据(订单详细)
    private void toAnother(int orderid, int uid) {
        BaseRequest request = new BaseRequest(ConfigUtil.TO_ANOTHER)
                .add("account", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("orderid", orderid)
                .add("uid", uid);
        CallServer.getRequestInstance().add(this, TOANOTHER, request, this, false, false);
    }

    private void toOneDriver(int orderid, int uid) {
        BaseRequest request = new BaseRequest(ConfigUtil.TO_ONE_DRIVER)
                .add("orderid", orderid)
                .add("uid", uid);
        CallServer.getRequestInstance().add(this, TOONEDRIVER, request, this, false, false);
    }

    private void getOrderDetails(int orderid, int uid) {
        BaseRequest request = new BaseRequest(ConfigUtil.GET_ORDER_DETAILS)
                .add("orderid", orderid)
                .add("uid", uid);
        CallServer.getRequestInstance().add(this, GET_ORDER_DETAILS, request, this, false, false);
    }

    /**
     * usertype
     *
     * @param orderid
     */
    private void setCancelReason(int orderid) {
        BaseRequest baseRequest = new BaseRequest(ConfigUtil.CANCEL_ORDER)
                .add("phone", userDataPreference.getAccount())
                .add("token", userDataPreference.getToken())
                .add("usertype", 1)
                .add("orderid", orderid)
                .add("reason", "没有司机接单");
        CallServer.getRequestInstance().add(this, CANCEL_ORDER, baseRequest, this, false, false);
    }

    @Override
    public void onSucceed(int what, String response) {
        if (what == GET_ORDER_DETAILS) {
            try {
                OrderDetailBean orderDetailBean = JSON.parseObject(response, OrderDetailBean.class);
                if (orderDetailBean.getStatus() != 1) {
                    if (mTimerTask != null) {
                        mTimerTask.cancel(); // 将原任务从队列中移除
                    }
                    if (timer != null) {
                        timer.cancel();
                    }
                    this.stopSelf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == CANCEL_ORDER) {
            ToastUtil.showToast(this, "附近没有司机，已为您取消订单");
            this.stopSelf();
        }else if (what == TOONEDRIVER) {
            this.stopSelf();
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        if (what == TOONEDRIVER) {
            setCancelReason(orderId);
            ToastUtil.showToast(this, msg);
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {

    }

    /**
     * 继承 CountDownTimer 防范
     * <p>
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mc1.cancel();
            mc2.cancel();
            stopTimer();
            //取消订单
            toOneDriver(orderId, userBean.getId());

        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished / (60 * 1000) < 3) {
                handler.sendEmptyMessage(2);//3分钟后
            }

        }
    }

    /**
     * 继承 CountDownTimer 防范
     * <p>
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer2 extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mc2.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }


    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel(); // 将原任务从队列中移除
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
