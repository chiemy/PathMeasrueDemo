package com.chiemy.demo.pathmeasuredemo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created: chiemy
 * Date: 17/2/8
 * Description:
 */

public class ConcaveArrowView extends View {
    private static final int DEFAULT_LENGTH = 50;
    private static final int DEFAULT_HEIGHT = 20;

    private Paint mPaint;
    private float mArrowLength;
    private float mArrowHeight;
    private float mAdvance;
    private int mArrowColor;
    private float mMoveSpeed; // 像素/秒

    private Path mPath;
    private Path nConcaveArrow;

    private ObjectAnimator mPhaseAnimator;

    public ConcaveArrowView(Context context) {
        this(context, null);
    }

    public ConcaveArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConcaveArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConcaveArrowView);
        mArrowLength = ta.getDimensionPixelOffset(R.styleable.ConcaveArrowView_arrow_length, 0);
        mArrowHeight = ta.getDimensionPixelOffset(R.styleable.ConcaveArrowView_arrow_height, 0);
        mAdvance = ta.getDimensionPixelOffset(R.styleable.ConcaveArrowView_arrow_spacing, 0);
        if (mArrowLength <= 0) {
            mArrowLength = DEFAULT_LENGTH;
        }
        if (mArrowHeight <= 0) {
            mArrowHeight = DEFAULT_HEIGHT;
        }
        if (mAdvance <= 0) {
            mAdvance = mArrowLength * 1.2f;
        }
        mArrowColor = ta.getColor(R.styleable.ConcaveArrowView_arrow_color, Color.RED);
        mMoveSpeed = ta.getInteger(R.styleable.ConcaveArrowView_arrow_move_speed, (int) mAdvance);
        ta.recycle();
        init();
    }

    private void init() {
        // Create a straight line
        mPath = new Path();
        nConcaveArrow = makeConcaveArrow(mArrowLength, mArrowHeight);

        mPaint = new Paint();
        mPaint.setColor(mArrowColor);
        mPaint.setStrokeWidth(mArrowHeight);
        mPaint.setAntiAlias(true);
        setPhase(0);

        startPhaseAnimator();
    }

    private void startPhaseAnimator() {
        cancelAnimator();
        if (mMoveSpeed > 0) {
            float durationInSecond = mAdvance / mMoveSpeed;
            mPhaseAnimator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f).setDuration((int) (durationInSecond * 1000));
            mPhaseAnimator.setRepeatMode(ObjectAnimator.RESTART);
            mPhaseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            mPhaseAnimator.setInterpolator(new LinearInterpolator());
            mPhaseAnimator.start();
        }
    }

    private void cancelAnimator() {
        if (mPhaseAnimator != null) {
            mPhaseAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            height = (int) mArrowHeight + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initPath(mPath);
    }

    @CallSuper
    protected void initPath(Path path) {
        path.reset();
        path.moveTo(-mAdvance, getPaddingTop());
        path.lineTo(getWidth(), getPaddingTop());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath(canvas);
    }

    protected void drawPath(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }

    public void setPhase(float phase) {
        mPaint.setPathEffect(createPathEffect(phase * mAdvance));
        invalidate();
    }

    private PathEffect createPathEffect(float phase) {
        return new PathDashPathEffect(nConcaveArrow, mAdvance,
                phase, PathDashPathEffect.Style.MORPH);
    }

    private Path makeConcaveArrow(float length, float height) {
        Path p = new Path();
        p.moveTo(0.0f, 0.0f);
        p.lineTo(length, 0.0f);
        p.lineTo(length + height / 4.0f, height / 2.0f);
        p.lineTo(length, height);
        p.lineTo(0, height);
        p.lineTo(height / 4.0f, height / 2.0f);
        p.close();
        return p;
    }

    public void setArrowColor(@ColorInt int arrowColor) {
        mArrowColor = arrowColor;
        mPaint.setColor(mArrowColor);
        invalidate();
    }

    public void setArrowHeight(@FloatRange(from = 1) float arrowHeight) {
        mArrowHeight = arrowHeight;
        requestLayout();
    }

    public void setArrowLength(@FloatRange(from = 1) float arrowLength) {
        mArrowLength = arrowLength;
        requestLayout();
    }

    public void setMoveSpeed(@FloatRange(from = 0) float moveSpeed) {
        mMoveSpeed = moveSpeed;
        startPhaseAnimator();
    }

    public float getMoveSpeed() {
        return mMoveSpeed;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimator();
    }

}
