package com.doudou.passenger.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doudou.passenger.R;
import com.doudou.passenger.ui.main.booking.SelectTimeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/2.
 */

public class FlightDialog {

    @BindView(R.id.editAirNum)
    EditText editAirNum;
    @BindView(R.id.layoutFlightTime)
    RelativeLayout layoutFlightTime;
    @BindView(R.id.flightTime)
    TextView flightTime;
    private Context context;
    private Dialog dialog;
    private Display display;
    FlightCallBack payCallBack;

    public interface FlightCallBack {
        void onFinish(String str);
    }

    public FlightDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public FlightDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_flight, null);
        RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        ButterKnife.bind(this, view);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocus();
            }
        }, 100);
        // 调整dialog背景大小
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.95), LinearLayout.LayoutParams.FILL_PARENT));

        return this;
    }

    public FlightDialog setOnTextFinishListener(FlightCallBack callBack) {
        this.payCallBack = callBack;
        editAirNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)){
                    layoutFlightTime.setVisibility(View.VISIBLE);
                    context.startActivity(new Intent(context, SelectTimeActivity.class));
                }
                if (payCallBack != null){
                    payCallBack.onFinish(s.toString());
                }
            }
        });
        return this;
    }


    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void setFocus() {
        editAirNum.requestFocus();
        editAirNum.setFocusable(true);
        showKeyBord(editAirNum);
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public void showKeyBord(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

    }

}
