package com.lexing.passenger.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.SysApplication;
import com.lexing.passenger.nohttp.CallServer;
import com.lexing.passenger.nohttp.HttpListener;
import com.lexing.passenger.ui.main.CancelOrderActivity;
import com.lexing.passenger.ui.main.MainActivity;
import com.lexing.passenger.utils.ToastUtil;
import com.yolanda.nohttp.rest.Request;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private FrameLayout mContentLayout;
    private TextView rightText;
    private String isBackToMain = "";

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("", getClass().getName());
        getDelegate().setContentView(R.layout.activity_base);

        mCoordinatorLayout = ButterKnife.findById(this, R.id.coordinatorlayout);
        mAppBarLayout = ButterKnife.findById(this, R.id.appbarlayout);
        mToolbar = ButterKnife.findById(this, R.id.toolbar);
        mContentLayout = ButterKnife.findById(this, R.id.content);
        rightText = ButterKnife.findById(this, R.id.toolbar_right);

        setSupportActionBar(mToolbar);
        setBackBar(true);
        onActivityCreate(savedInstanceState);

        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backMethod();
            }
        });
    }
    //-------------- NoHttp -----------//

    public void request(int what, Request<String> request, HttpListener callback, boolean canCancel, boolean isLoading) {
        request.setCancelSign(this);
        CallServer.getRequestInstance().add(this, what, request, callback, canCancel, isLoading);

    }

    @Override
    protected void onDestroy() {
        CallServer.getRequestInstance().cancelBySign(this);
        super.onDestroy();
    }
    // -------------------- BaseActivity的辅助封装 --------------------- //

    protected abstract void onActivityCreate(Bundle savedInstanceState);

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    public FrameLayout getContentLayout() {
        return mContentLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(title);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void setTitle(int titleId) {
//		mToolbar.setTitle(titleId);
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(titleId);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void setSubtitle(CharSequence title) {
        mToolbar.setSubtitle(title);
    }

    public void setSubtitle(int titleId) {
        mToolbar.setSubtitle(titleId);
    }

    public void setSubtitleTextColor(int color) {
        mToolbar.setSubtitleTextColor(color);
    }

    public void setTitleTextColor(int color) {
        mToolbar.setTitleTextColor(color);
    }

    public void setBackBar(boolean isShow) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(isShow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (isShow) {
            mToolbar.setNavigationIcon(R.drawable.ic_back);
        } else {
            mToolbar.setNavigationIcon(null);
        }
    }

    public void setContentBackground(int color) {
        mContentLayout.setBackgroundResource(color);
    }

    public void setRightText(CharSequence title, OnClickListener onClickListener) {
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(title);
        rightText.setOnClickListener(onClickListener);
    }

    public void setRightText(int titleId, OnClickListener onClickListener) {
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(titleId);
        rightText.setOnClickListener(onClickListener);
    }

    public void setRightTextGone() {
        rightText.setVisibility(View.GONE);
    }

    public void setRightTextVisible() {
        rightText.setVisibility(View.VISIBLE);
    }

    public void setRightTextColor(int colorId) {
        rightText.setTextColor(colorId);
    }

    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentLayout, true);
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.addView(view, params);
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backMethod();
            return true;
        }
        return onOptionsItemSelectedCompat(item);
    }

    private void backMethod() {
        if (isBackToMain.equals(CANCEL_TYPE_A)) {
            startActivity(new Intent(this, CancelOrderActivity.class));
            isBackToMain = "";
        } else if (isBackToMain.equals(CANCEL_TYPE_B)) {
            startActivity(new Intent(BaseActivity.this, MainActivity.class));
            SysApplication.getInstance().exit();
            isBackToMain = "";
            finish();
        } else if (isBackToMain.equals(CANCEL_TYPE_C)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您有未完成订单，请先完成再退出");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            finish();
        }
    }

    public void setOnBackListener(OnClickListener onClickListener) {
        mToolbar.setNavigationOnClickListener(onClickListener);
    }

    public static final String CANCEL_TYPE_A = "cancelOrder";
    public static final String CANCEL_TYPE_B = "cancelReason";
    public static final String CANCEL_TYPE_C = "can_not_cancel";

    public void setBackToMainAty() {
        isBackToMain = CANCEL_TYPE_A;
    }

    public void setBackToFinish() {
        isBackToMain = CANCEL_TYPE_C;
    }

    public void setBackToMainAtyFromCancel() {
        isBackToMain = CANCEL_TYPE_B;
    }

    protected boolean onOptionsItemSelectedCompat(MenuItem item) {
        return false;
    }

    public ViewGroup getContentRoot() {
        return mContentLayout;
    }


    /**
     * Show message dialog.
     *
     * @param title   title.
     * @param message message.
     */
    public void showMessageDialog(CharSequence title, CharSequence message, CharSequence positiveText, DialogInterface.OnClickListener listener) {
        if (TextUtils.isEmpty(positiveText))
            positiveText = getText(R.string.i_know);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, listener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Show message dialog.
     *
     * @param titleId titleId.
     * @param message message.
     */
    public void showMessageDialog(int titleId, CharSequence message, CharSequence positiveText, DialogInterface.OnClickListener listener) {
        if (TextUtils.isEmpty(positiveText))
            positiveText = getText(R.string.i_know);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, listener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.show();
    }

    public void showMsg(String msg) {
        ToastUtil.showToast(this, msg);
    }

    public void showMsg(int msg) {
        ToastUtil.showToast(this, msg);
    }
}
