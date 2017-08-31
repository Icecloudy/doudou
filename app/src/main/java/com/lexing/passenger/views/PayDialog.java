package com.lexing.passenger.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lexing.passenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/2.
 */

public class PayDialog {
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.PayPwdEditText)
    com.lexing.passenger.views.PayPwdEditText PayPwdEditText;
    private Context context;
    private Dialog dialog;
    private Display display;
    PayCallBack payCallBack;

    public interface PayCallBack{
        void onFinish(String str);
    }

    public PayDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public PayDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pay, null);
        RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        ButterKnife.bind(this, view);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        PayPwdEditText.initStyle(R.drawable.shape_white_solid_bg, 6, 0.33f, R.color.gray, R.color.text_title, 20);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.75), LinearLayout.LayoutParams.FILL_PARENT));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PayPwdEditText.setFocus();
            }
        }, 100);

        return this;
    }
    public PayDialog setOnTextFinishListener(PayCallBack callBack){
        this.payCallBack = callBack;
        PayPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener() {
            @Override
            public void onFinish(String str) {//密码输入完后的回调
                if (payCallBack != null){
                    payCallBack.onFinish(str);
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
    public void cleanPwd(){
        PayPwdEditText.clearText();
    }

}
