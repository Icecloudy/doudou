package com.lexing.passenger.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lexing.passenger.R;


/**
 * Created by Administrator on 2016/11/9 0009.
 */

public class MyBottomSheetDialog {
	private Context context;
	private TextView sheetTextText1;
	private TextView sheetTextText2;
	private TextView sheetTextTextCancel;
	private BottomSheetDialog bottomSheetDialog;

	public MyBottomSheetDialog(@Nullable Context context) {
		this.context = context;
	}

	public MyBottomSheetDialog build() {
		View view = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet, null);
		sheetTextText1 = (TextView) view.findViewById(R.id.sheetTextText1);
		sheetTextText2 = (TextView) view.findViewById(R.id.sheetTextText2);
		sheetTextTextCancel = (TextView) view.findViewById(R.id.sheetTextTextCancel);

		bottomSheetDialog = new BottomSheetDialog(context);
		bottomSheetDialog.setContentView(view);
		sheetTextTextCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				bottomSheetDialog.dismiss();
			}
		});
		return this;
	}

	public MyBottomSheetDialog setFirstItem(CharSequence text, final View.OnClickListener onClickListener) {
		if (TextUtils.isEmpty(text)) {
			sheetTextText1.setText("第一项");
		}
		sheetTextText1.setText(text);
		sheetTextText1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickListener.onClick(view);
				bottomSheetDialog.dismiss();
			}
		});

		return this;
	}

	public MyBottomSheetDialog setSecondItem(CharSequence text, final View.OnClickListener onClickListener) {
		if (TextUtils.isEmpty(text)) {
			sheetTextText1.setText("第二项");
		}
		sheetTextText2.setText(text);
		sheetTextText2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickListener.onClick(view);
				bottomSheetDialog.dismiss();
			}
		});
		return this;
	}

	public void show() {
		bottomSheetDialog.show();
	}
}
