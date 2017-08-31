package com.doudou.passenger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WaveView extends View {
    private float mInitialRadius;   // 初始波纹半径
    private float mMaxRadiusRate = 0.95f;   // 如果没有设置mMaxRadius，可mMaxRadius = 最小长度 * mMaxRadiusRate;
    private float mMaxRadius;   // 最大波纹半径
    private long mDuration = 1500; // 一个波纹从创建到消失的持续时间
    private int mSpeed = 1500;   // 波纹的创建速度，每500ms创建一个
    private Interpolator mInterpolator = new LinearInterpolator();

    private List<Circle> mCircles = new ArrayList<>();
    private boolean mIsRunning;
    private boolean mMaxRadiusSet;

    private Paint mPaint;
    private long mLastCreateTime;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setStyle(Paint.Style.FILL);
    }

    public void setStyle(Paint.Style style) {
        mPaint.setStyle(style);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!mMaxRadiusSet) {
            mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
        }
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        this.mMaxRadiusRate = maxRadiusRate;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 开始
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            new Runnable() {
                @Override
                public void run() {
                    if (mIsRunning) {
                        long currentTime = System.currentTimeMillis();
//                        if (currentTime - mLastCreateTime < mSpeed) {
//                            return;
//                        }
                        addCircle();//增加一个圆
                        invalidate();
                        mLastCreateTime = currentTime;
                        postDelayed(this, mSpeed);
                    }
                }
            }.run();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        mIsRunning = false;
    }

    protected void onDraw(Canvas canvas) {
        Iterator<Circle> iterator = mCircles.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, circle.getRadius(), mPaint);
            } else {
                //将超过持续时间的圆remove掉。
                iterator.remove();
            }
        }
        if (mCircles.size() > 0) {
            //每隔20毫秒重新绘制
            postInvalidateDelayed(20);
        }
    }

    public void setInitialRadius(float radius) {
        mInitialRadius = radius;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public void setMaxRadius(float maxRadius) {
        this.mMaxRadius = maxRadius;
        mMaxRadiusSet = true;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }

    public boolean getState(){
        return mIsRunning;
    }

    public void addCircle(){
        mCircles.add(new Circle());
    }

    class Circle {
        private long mCreateTime;

        public Circle() {
            this.mCreateTime = System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - mInterpolator.getInterpolation(percent)) * 255);
        }

        public float getRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }
}
