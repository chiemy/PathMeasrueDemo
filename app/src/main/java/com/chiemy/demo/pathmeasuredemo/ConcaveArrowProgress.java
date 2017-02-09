package com.chiemy.demo.pathmeasuredemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;

/**
 * Created: chiemy
 * Date: 17/2/8
 * Description:
 */

public class ConcaveArrowProgress extends ConcaveArrowView {
    private static final int DEFAULT_MAX = 1000;

    private PathMeasure mPathMeasure;
    private int mMaxProgress;
    private int mProgress;
    private Path mDst = new Path();

    public ConcaveArrowProgress(Context context) {
        this(context, null);
    }

    public ConcaveArrowProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConcaveArrowProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConcaveArrowProgress);
        int maxProgress = ta.getInteger(R.styleable.ConcaveArrowProgress_max, DEFAULT_MAX);
        int progress = ta.getInteger(R.styleable.ConcaveArrowProgress_progress, 0);
        setMaxProgress(maxProgress);
        setProgress(progress);
        mPathMeasure = new PathMeasure();
    }

    @Override
    protected void initPath(Path path) {
        super.initPath(path);
        mPathMeasure.setPath(path, false);
    }

    @Override
    protected void drawPath(Canvas canvas) {
        mDst.reset();
        mPathMeasure.getSegment(0, mPathMeasure.getLength() * mProgress / 1000f, mDst, true);
        canvas.drawPath(mDst, getPaint());
    }

    public void setProgress(int progress) {
        mProgress = Math.max(0, progress);
        mProgress = Math.min(mMaxProgress, mProgress);
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            return;
        }
        if (maxProgress == 0) {
            maxProgress = DEFAULT_MAX;
        }
        mMaxProgress = maxProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }
}
