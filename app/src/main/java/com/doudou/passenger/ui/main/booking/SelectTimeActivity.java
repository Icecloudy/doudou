package com.doudou.passenger.ui.main.booking;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doudou.passenger.R;
import com.doudou.passenger.utils.StringUtil;
import com.doudou.passenger.utils.ToastUtil;
import com.doudou.passenger.views.wheelView.OnWheelChangedListener;
import com.doudou.passenger.views.wheelView.WheelView;
import com.doudou.passenger.views.wheelView.adapter.ArrayWheelAdapter;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 先拿到当前时间
 */

public class SelectTimeActivity extends Activity implements OnWheelChangedListener {

    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.wheelViewDay)
    WheelView wheelViewDay;
    @BindView(R.id.wheelViewHour)
    WheelView wheelViewHour;
    @BindView(R.id.wheelViewMin)
    WheelView wheelViewMin;

    private String[] mDay;
    private String strDay;

    private String[] mHour;
    private String strHour;

    private String[] mMin;
    private String strMin;

    private int currentHour;
    private String[] mLimateHour;

    //    private String[] mLimateMin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        ButterKnife.bind(this);
        getWindow().setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        initUnitData();

    }

    private void initUnitData() {
        wheelViewDay.addChangingListener(this);
        wheelViewHour.addChangingListener(this);
        wheelViewMin.addChangingListener(this);

        currentHour = new Date().getHours();
        if (currentHour == 23) {
            mDay = StringUtil.getTwoDays();
            mLimateHour = new String[24];
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    mLimateHour[i] = "0" + i + "点";
                } else {
                    mLimateHour[i] = i + "点";
                }
            }
        } else {
            mDay = StringUtil.getThreeDays();
            mLimateHour = new String[23 - currentHour];
            for (int i = 0; i < 23 - currentHour; i++) {
                if (currentHour < 9) {
                    mLimateHour[i] = "0" + (i + currentHour + 1) + "点";//9  10
                } else {
                    mLimateHour[i] = (i + currentHour + 1) + "点";
                }
            }
        }
        mHour = new String[24];
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                mHour[i] = "0" + i + "点";
            } else {
                mHour[i] = i + "点";
            }
        }
        mMin = new String[6];
        for (int i = 0; i < 6; i++) {
            if (i < 1) {
                mMin[i] = "0" + i + "分";
            } else {
                mMin[i] = i + "0分";
            }
        }

        wheelViewDay.setViewAdapter(new ArrayWheelAdapter<>(this, mDay));
        wheelViewHour.setViewAdapter(new ArrayWheelAdapter<>(this, mLimateHour));
        wheelViewMin.setViewAdapter(new ArrayWheelAdapter<>(this, mMin));
        wheelViewDay.setVisibleItems(7);
        wheelViewHour.setVisibleItems(7);
        wheelViewMin.setVisibleItems(7);
        strDay = mDay[0];
        if (mLimateHour.length > 0) {
            strHour = mLimateHour[0];
        }
        strMin = mMin[0];


    }

    @OnClick({R.id.btnCancel, R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(strHour) || TextUtils.isEmpty(strMin)) {
                    ToastUtil.showToast(this, "请选择时间");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("time", strDay + strHour + strMin);
                    setResult(RESULT_OK, intent);
                    this.finish();
                }
                break;
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wheelViewDay) {
            strDay = mDay[wheelViewDay.getCurrentItem()];
            if (wheelViewDay.getCurrentItem() == 0) {
                wheelViewHour.setViewAdapter(new ArrayWheelAdapter<>(this, mLimateHour));
            } else {
                wheelViewHour.setViewAdapter(new ArrayWheelAdapter<>(this, mHour));
                strHour = mHour[wheelViewHour.getCurrentItem()];
            }
        } else if (wheel == wheelViewHour) {
            if (wheelViewDay.getCurrentItem() == 0) {
                strHour = mLimateHour[wheelViewHour.getCurrentItem()];
            } else {
                strHour = mHour[wheelViewHour.getCurrentItem()];
            }

        } else if (wheel == wheelViewMin) {
            strMin = mMin[wheelViewMin.getCurrentItem()];
        }
    }
}
