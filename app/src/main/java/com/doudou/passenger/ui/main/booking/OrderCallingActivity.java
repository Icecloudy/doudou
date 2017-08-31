package com.doudou.passenger.ui.main.booking;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudou.passenger.R;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.data.models.OrderDetailBean;
import com.doudou.passenger.data.models.UserBean;
import com.doudou.passenger.nohttp.BaseRequest;
import com.doudou.passenger.nohttp.CallServer;
import com.doudou.passenger.nohttp.HttpListener;
import com.doudou.passenger.ui.login.LoginActivity;
import com.doudou.passenger.ui.main.MainActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Response;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderCallingActivity extends Activity implements HttpListener {


    @BindView(R.id.textView1)
    TextView tv1;
    @BindView(R.id.textView2)
    TextView tv2;
    @BindView(R.id.tvDone)
    TextView tvDone;
    @BindView(R.id.lLayout_bg)
    LinearLayout lLayoutBg;
    private Display display;

    private MyCountDownTimer mc1;
    private MyCountDownTimer2 mc2;

    MyTimerTask mTimerTask;
    Timer timer;

    UserBean userBean = new UserBean();
    UserDataPreference userDataPreference;

    int orderId;
    boolean isToAnother = true;
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dialog);
        ButterKnife.bind(this);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        // 调整dialog背景大小
        lLayoutBg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85),
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (getIntent().getExtras() != null) {
            orderId = getIntent().getExtras().getInt("orderId");
        }
        userDataPreference = new UserDataPreference(this);
        userBean = JSON.parseObject(userDataPreference.getUserInfo(), UserBean.class);
        mc1 = new MyCountDownTimer(6 * 60 * 1000, 60 * 1000);
        mc1.start();
        mc2 = new MyCountDownTimer2(60 * 1000, 1000);
        mc2.start();
        handler.sendEmptyMessage(0);
        registerMessageReceiver();
    }

    @OnClick(R.id.tvDone)
    public void onClick() {
        stopTimer();
        setCancelReason(orderId);

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
            tv1.setText("00");
            tv2.setText("00");
            stopTimer();
            //取消订单
            toOneDriver(orderId, userBean.getId());

        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv1.setText("0" + millisUntilFinished / (60 * 1000));
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
            // tv2.setText("60");
            mc2.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if ((millisUntilFinished / (1000)) < 10) {
                tv2.setText("0" + millisUntilFinished / 1000);
            } else {
                tv2.setText(millisUntilFinished / 1000 + "");
            }

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
                        timer.schedule(mTimerTask, 1000 * 10, 1000 * 30); // 经过20s后再次执行
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


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
        stopTimer();
        mc1.cancel();
        mc2.cancel();
        handler.removeMessages(2);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

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
                    ToastUtil.showToast(this, "预约成功，请在行程记录查看行程详情");
                    startActivity(new Intent(this, MainActivity.class));
                    SysApplication.getInstance().exit();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (what == CANCEL_ORDER) {
            ToastUtil.showToast(this, "订单已取消");
            finish();
        } else if (what == TOONEDRIVER) {
            ToastUtil.showToast(this, "预约成功，请在行程记录查看行程详情");
            startActivity(new Intent(this, MainActivity.class));
            SysApplication.getInstance().exit();
            finish();
        } else if (what == TOANOTHER) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("type")) {
                int type = jsonObject.getInteger("type");
                if (type == 3) {
                    ToastUtil.showToast(this, "预约成功，请在行程记录查看行程详情");
                    startActivity(new Intent(this, MainActivity.class));
                    SysApplication.getInstance().exit();
                    finish();
                }
            }
        }
    }

    @Override
    public void onCodeError(int what, int code, String msg) {
        ToastUtil.showToast(this, msg);
        if (what == TOONEDRIVER) {
            setCancelReason(orderId);
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        if (what == TOONEDRIVER) {
            setCancelReason(orderId);
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


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.lexing.passenger.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String extras = intent.getStringExtra(KEY_EXTRAS);
                JSONObject jsonObject = JSON.parseObject(extras);
                if (jsonObject.containsKey("payload")) {
                    String payload = jsonObject.getString("payload");
                    if (payload.equals(ConfigUtil.PAYLOAD_SUC)) {
                        ToastUtil.showToast(OrderCallingActivity.this, "预约成功，请在行程记录查看行程详情");
                        startActivity(new Intent(OrderCallingActivity.this, MainActivity.class));
                        SysApplication.getInstance().exit();
                        finish();
                    } else if (payload.equals(ConfigUtil.PAYLOAD_LOGOUT)) {
                        new UserDataPreference(context).reMoveKry("token");
                        context.startActivity(new Intent(context, LoginActivity.class));
                        SysApplication.getInstance().exit();
                        //
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}
