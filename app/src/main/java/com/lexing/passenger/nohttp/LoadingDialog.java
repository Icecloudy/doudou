package com.lexing.passenger.nohttp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.lexing.passenger.R;


public class LoadingDialog {
	private Context context;
	private Dialog dialog;
	private Display display;


	public LoadingDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public LoadingDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
		RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		mainLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.30), LayoutParams.FILL_PARENT));

		return this;
	}
	public LoadingDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener){
		dialog.setOnCancelListener(onCancelListener);
		return this;
	}

	public LoadingDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}
	public boolean isShowing(){
		return dialog.isShowing();
	}
	public void show() {
		dialog.show();
	}

	public void dismiss(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
}
