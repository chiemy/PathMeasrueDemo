package com.chiemy.demo.pathmeasuredemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import static android.R.attr.width;

/**
 * Created: chiemy
 * Date: 17/2/9
 * Description:
 */

public class CircleProgressBar extends View implements ValueAnimator.AnimatorUpdateListener {
    private Paint mPaint;
    private Path mPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;
    private int mRadius;
    private int mStrokeWidth = 10;

    private float mPathLength;
    private float mPathPercent;

    private ValueAnimator mValueAnimator;
    private ObjectAnimator mRotateAnimator;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        mRadius = ta.getDimensionPixelOffset(R.styleable.CircleProgressBar_radius, 0);
        if (mRadius <= 0) {
            mRadius = 100;
        }
        mStrokeWidth = ta.getDimensionPixelOffset(R.styleable.CircleProgressBar_stroke_width, 0);
        if (mStrokeWidth <= 0) {
            mStrokeWidth = 10;
        }
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();
        mPathMeasure = new PathMeasure();
        mDstPath = new Path();

        mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(2000);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.start();

        mRotateAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, 0, 360);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setDuration(4000);
        mRotateAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = makeSize(widthMeasureSpec);
        int height = makeSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int centerX = w / 2;
        int centerY = h / 2;
        float radius = Math.min(mRadius, Math.min(centerX, centerY)) - mStrokeWidth / 2f;
        mPath.reset();
        mPath.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
        mPathMeasure.setPath(mPath, false);
        mPathLength = mPathMeasure.getLength();
    }

    private int makeSize(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.AT_MOST
                || mode == MeasureSpec.UNSPECIFIED) {
            size = Math.min(mRadius * 2, width);
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDstPath.reset();
        float stop = mPathLength * mPathPercent;
        float start = (float) (stop - ((0.5 - Math.abs(mPathPercent - 0.5)) * mPathLength * 4));
        mPathMeasure.getSegment(start, stop, mDstPath, true);
        canvas.drawPath(mDstPath, mPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == mValueAnimator) {
            mPathPercent = (float) animation.getAnimatedValue();
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        if (mRotateAnimator != null) {
            mRotateAnimator.cancel();
        }
    }
}
