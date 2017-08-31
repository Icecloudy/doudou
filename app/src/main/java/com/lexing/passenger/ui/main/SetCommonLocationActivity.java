package com.lexing.passenger.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jaeger.library.StatusBarUtil;
import com.lexing.passenger.R;
import com.lexing.passenger.ui.main.task.InputTipTask;
import com.lexing.passenger.ui.main.task.JsonParser;
import com.lexing.passenger.ui.main.task.PoiSearchTask;
import com.lexing.passenger.ui.main.task.PositionEntity;
import com.lexing.passenger.ui.main.task.RecomandAdapter;
import com.lexing.passenger.ui.main.task.RouteTask;
import com.lexing.passenger.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetCommonLocationActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {

    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.historyListView)
    ListView mRecommendList;
    @BindView(R.id.editDestination)
    EditText mDestinaionText;

    private RecomandAdapter mRecomandAdapter;
    private RouteTask mRouteTask;
    String flag;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    @BindColor(R.color.colorPrimary)
    int statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_common_location);
        StatusBarUtil.setColor(this, statusBar, 0);
        ButterKnife.bind(this);
        flag = getIntent().getExtras().getString("flag");
        String locationHint = getIntent().getExtras().getString("location");
        mDestinaionText.setHint(locationHint);
        mDestinaionText.addTextChangedListener(this);
        mRecomandAdapter = new RecomandAdapter(getApplicationContext());
        mRecommendList.setAdapter(mRecomandAdapter);
        mRecommendList.setOnItemClickListener(this);

        mRouteTask = RouteTask.getInstance(getApplicationContext());

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);

        String city = mRouteTask.getStartPoint().city;
        if (!city.endsWith("市")) {
            city = city + "市";
        }
        location.setText(city);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (RouteTask.getInstance(getApplicationContext()).getStartPoint() == null) {
            Toast.makeText(getApplicationContext(), "检查网络，Key等问题", Toast.LENGTH_SHORT).show();
            return;
        }
        InputTipTask.getInstance(mRecomandAdapter).searchTips(getApplicationContext(), s.toString(),
                RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PositionEntity entity = (PositionEntity) mRecomandAdapter.getItem(position);
        if (entity.latitude == 0 && entity.longitude == 0) {
            PoiSearchTask poiSearchTask = new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
            poiSearchTask.search(entity.address, RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
        } else {
            if (flag.equals("start")) {
                mRouteTask.setStartPoint(entity);
            } else {
                mRouteTask.setEndPoint(entity);
            }
            mRouteTask.search();
            Intent intent = new Intent(this, DestinationActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick({R.id.location, R.id.imgVoice, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgVoice:
                mDestinaionText.setText(null);
                voiceInput();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.location:
                startActivityForResult(new Intent(this, CityPickerActivity.class), 1002);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1002) {
                if (data != null) {
                    String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                    if (!city.endsWith("市")) {
                        city = city + "市";
                    }
                    location.setText(city);
                }
            }
        }

    }

    int ret = 0;

    private void voiceInput() {
        mIatResults.clear();
        setParam();
        boolean isShowDialog = true;
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip("请开始说话…");
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip("请开始说话…");
            }
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");


        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
//            showTip(error.getPlainDescription(true));
        }

    };
    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            showTip("当前正在说话，音量大小：" + volume);
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

    private void showTip(String str) {
        ToastUtil.showToast(this, str);
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        mDestinaionText.setText(resultBuffer.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }
}
