package com.doudou.passenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.doudou.passenger.SysApplication;
import com.doudou.passenger.data.UserDataPreference;
import com.doudou.passenger.ui.login.LoginActivity;
import com.doudou.passenger.ui.main.ConfirmOrderActivity;
import com.doudou.passenger.ui.main.MainActivity;
import com.doudou.passenger.ui.main.NavigationActivity;
import com.doudou.passenger.ui.main.OrderCancelDetailActivity;
import com.doudou.passenger.ui.main.booking.OrderCallingActivity;
import com.doudou.passenger.utils.ConfigUtil;
import com.doudou.passenger.utils.ExampleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";


    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    @Override
    public void onReceive(Context context, Intent intent) {
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));


        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
            if (!NavigationActivity.isForeground) {
                setIntent(context, bundle);
            }
            return;
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
        try {
            if (!new UserDataPreference(context).getVoice()) {
                String extraInfo = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extraInfo)) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extraInfo);
                    if (jsonObject.containsKey("payload")) {
                        String payload = jsonObject.getString("payload");
                        if (payload.equals(ConfigUtil.PAYLOAD_shipping)) {
                            playVoice("司机正前来接驾");
                        } else if (payload.equals(ConfigUtil.PAYLOAD_startCharging)) {
                            playVoice("订单开始收费");
                        } else if (payload.equals(ConfigUtil.PAYLOAD_driverReady)) {
                            playVoice("司机已到达上车地点，请尽快上车");
                        } else if (payload.equals(ConfigUtil.PAYLOAD_cancelOrder)) {
                            playVoice("您的订单已被取消");
                        } else if (payload.equals(ConfigUtil.PAYLOAD_payment)) {
                            playVoice("您的订单已完成，请支付");
                        } else if (payload.equals(ConfigUtil.PAYLOAD_SUC)) {
                            playVoice("下单成功");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIntent(Context context, Bundle bundle) {
        //打开自定义的Activity
        Intent i = null;
        String extraInfo = bundle.getString(JPushInterface.EXTRA_EXTRA);
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extraInfo);
        if (jsonObject.containsKey("payload")) {
            String payload = jsonObject.getString("payload");

            if (payload.equals(ConfigUtil.PAYLOAD_cancelOrder)) {
                i = new Intent(context, OrderCancelDetailActivity.class);
            } else if (payload.equals(ConfigUtil.PAYLOAD_SUC)) {
                //
                i = new Intent(context, MainActivity.class);
            } else if (payload.equals(ConfigUtil.PAYLOAD_txt)) {
                //
//                i = new Intent(context, NewsActivity.class);
            } else if (payload.equals(ConfigUtil.PAYLOAD_LOGOUT)) {
                new UserDataPreference(context).reMoveKry("token");
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                SysApplication.getInstance().exit();
                //
            } else if (payload.equals(ConfigUtil.PAYLOAD_shipping) ||
                    payload.equals(ConfigUtil.PAYLOAD_startCharging) ||
                    payload.equals(ConfigUtil.PAYLOAD_driverReady) ||
                    payload.equals(ConfigUtil.PAYLOAD_payment)) {
                i = new Intent(context, NavigationActivity.class);
                i.putExtra("flag", "MyReceiver");
            }
        }
        if (i != null) {
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity

    private void processCustomMessage(Context context, Bundle bundle) {
        if (NavigationActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(NavigationActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(NavigationActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(NavigationActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        } else if (OrderCallingActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(OrderCallingActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(OrderCallingActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(NavigationActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        } else if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(NavigationActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        } else if (ConfirmOrderActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(ConfirmOrderActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(ConfirmOrderActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(ConfirmOrderActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        }
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {

            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "55");
            //设置音量
            mTts.setParameter(SpeechConstant.VOLUME, "tts_volume");
            //设置语调
            mTts.setParameter(SpeechConstant.PITCH, "tts_pitch");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    // 语记安装助手类
    private void playVoice(String content) {
        // 设置参数
        setParam();
        mTts.startSpeaking(content, mTtsListener);
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
//            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
            } else if (error != null) {
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
}
