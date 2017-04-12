package com.chiemy.demo.pathmeasuredemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import static android.animation.ObjectAnimator.ofInt;

/**
 * Created: chiemy
 * Date: 17/4/12
 * Description:
 */

public class CalendarAnimView extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint;
    private DotDrawable mDotOneDrawable, mDotTwoDrawable, mDotThreeDrawable;
    private LineDrawable mLineOneDrawable, mLineTwoDrawable, mLineThreeDrawable;

    private static final int STATE_LOADING = 1;
    private static final int STATE_SUCCESS = 2;
    private int mState;

    private int mLineWidth = 15;

    private int mPointLeft;
    private float mLineLeft;

    private int mColor = Color.parseColor("#DD4B39");

    private Drawable mHookDrawable;

    public CalendarAnimView(Context context) {
        super(context);
        init();
    }

    public CalendarAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 抗锯齿(边缘平衡无锯齿)
        mPaint.setAntiAlias(true);
        // 防抖动(颜色过渡自然)
        mPaint.setDither(true);

        mDotOneDrawable = new DotDrawable(mColor);
        mDotOneDrawable.setCallback(this);

        mDotTwoDrawable = new DotDrawable(mColor);
        mDotTwoDrawable.setCallback(this);

        mDotThreeDrawable = new DotDrawable(mColor);
        mDotThreeDrawable.setCallback(this);

        mLineOneDrawable = new LineDrawable(mColor);
        mLineOneDrawable.setCallback(this);

        mLineTwoDrawable = new LineDrawable(mColor);
        mLineTwoDrawable.setCallback(this);

        mLineThreeDrawable = new LineDrawable(mColor);
        mLineThreeDrawable.setCallback(this);

        Drawable drawable = getResources().getDrawable(R.mipmap.hook);
        mHookDrawable = new ClipDrawable(drawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        mHookDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mHookDrawable.setLevel(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointLeft = w / 5;
        mLineLeft = w * 0.35f;
        mDotOneDrawable.setBounds(0, 0, mLineWidth, mLineWidth);
        mDotTwoDrawable.setBounds(mDotOneDrawable.getBounds());
        mDotThreeDrawable.setBounds(mDotOneDrawable.getBounds());
        mDotOneDrawable.setAlpha(0);
        mDotTwoDrawable.setAlpha(0);
        mDotThreeDrawable.setAlpha(0);

        mLineOneDrawable.setBounds(0, 0, (int) (w * 0.4f), mLineWidth);
        mLineTwoDrawable.setBounds(mLineOneDrawable.getBounds());
        mLineThreeDrawable.setBounds(0, 0, (int) (w * 0.3f), mLineWidth);
        mLineOneDrawable.setEndPosition(0);
        mLineTwoDrawable.setEndPosition(0);
        mLineThreeDrawable.setEndPosition(0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mState == STATE_LOADING) {
            drawPoint(canvas);
            drawLine(canvas);
        } else if (mState == STATE_SUCCESS) {
            drawHook(canvas);
        }
    }

    private void drawPoint(Canvas canvas) {
        canvas.save();
        canvas.translate(mPointLeft, getHeight() * 0.4f);
        mDotOneDrawable.draw(canvas);

        canvas.translate(0, getHeight() * 0.16f);
        mDotTwoDrawable.draw(canvas);

        canvas.translate(0, getHeight() * 0.16f);
        mDotThreeDrawable.draw(canvas);

        canvas.restore();
    }

    private void drawLine(Canvas canvas) {
        canvas.save();
        canvas.translate(mLineLeft, getHeight() * 0.4f);
        mLineOneDrawable.draw(canvas);

        canvas.translate(0, getHeight() * 0.16f);
        mLineTwoDrawable.draw(canvas);

        canvas.translate(0, getHeight() * 0.16f);
        mLineThreeDrawable.draw(canvas);

        canvas.restore();
    }

    private void drawHook(Canvas canvas) {
        canvas.save();
        canvas.translate((getWidth() - mHookDrawable.getIntrinsicWidth()) / 2,
                0.6f * getHeight() - mHookDrawable.getIntrinsicHeight() / 2);
        mHookDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable dr) {
        return dr instanceof DotDrawable
                || dr instanceof LineDrawable
                || super.verifyDrawable(dr);
    }

    public void showLoading() {
        mState = STATE_LOADING;
        invalidate();
        mLineOneDrawable.show(mShowListener);
    }

    public void showSuccess() {
        mState = STATE_SUCCESS;
        invalidate();
        ObjectAnimator animator = ObjectAnimator
                .ofInt(mHookDrawable, "level", 0, 10000)
                .setDuration(700);
        animator.start();
    }

    private DrawableAnimatorListener mShowListener = new DrawableAnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animator, Drawable drawable) {
            if (drawable == mLineOneDrawable) {
                mLineTwoDrawable.show(this);
            } else if (drawable == mLineTwoDrawable) {
                mLineThreeDrawable.show(this);
            } else if (drawable == mLineThreeDrawable) {
                mDotOneDrawable.fadeIn(this);
            } else if (drawable == mDotOneDrawable) {
                mDotTwoDrawable.fadeIn(this);
            } else if (drawable == mDotTwoDrawable) {
                mDotThreeDrawable.fadeIn(this);
            } else if (drawable == mDotThreeDrawable) {
                mLineOneDrawable.hide(mHideListener);
            }
        }
    };

    private DrawableAnimatorListener mHideListener = new DrawableAnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animator, Drawable drawable) {
            if (drawable == mLineOneDrawable) {
                mDotOneDrawable.fadeOut(this);
            } else if (drawable == mDotOneDrawable) {
                mLineTwoDrawable.hide(this);
            } else if (drawable == mLineTwoDrawable) {
                mDotTwoDrawable.fadeOut(this);
            } else if (drawable == mDotTwoDrawable) {
                mLineThreeDrawable.hide(this);
            } else if (drawable == mLineThreeDrawable) {
                mDotThreeDrawable.fadeOut(this);
            } else if (drawable == mDotThreeDrawable) {
                mLineOneDrawable.show(mShowListener);
            }
        }
    };

    private interface DrawableAnimatorListener {
        void onAnimationEnd(Animator animator, Drawable drawable);
    }

    private class DotDrawable extends Drawable {
        private Paint mPaint;
        private int mAlpha;

        private float mRadius;
        private int mCenterX;
        private int mCenterY;

        public DotDrawable(int color) {
            mPaint = new Paint();
            // 抗锯齿(边缘平衡无锯齿)
            mPaint.setAntiAlias(true);
            // 防抖动(颜色过渡自然)
            mPaint.setDither(true);
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mCenterX = bounds.width() / 2;
            mCenterY = bounds.height() / 2;
            mRadius = Math.min(mCenterX, mCenterY);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            mAlpha = alpha;
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            if (mPaint.getColorFilter() != colorFilter) {
                mPaint.setColorFilter(colorFilter);
            }
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            if (mAlpha == 0) {
                return PixelFormat.TRANSPARENT;
            } else if (mAlpha == 255) {
                return PixelFormat.OPAQUE;
            }
            return PixelFormat.TRANSLUCENT;
        }

        public void fadeIn(DrawableAnimatorListener listener) {
            buildAlphaAnimator(listener, 0, 255).start();
        }

        public void fadeOut(DrawableAnimatorListener listener) {
            buildAlphaAnimator(listener, 255, 0).start();
        }

        @NonNull
        private ObjectAnimator buildAlphaAnimator(final DrawableAnimatorListener listener,
                                                  int from,
                                                  int to) {
            ObjectAnimator animator =
                    ofInt(this, "alpha", from, to)
                            .setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    listener.onAnimationEnd(animation, DotDrawable.this);
                }
            });
            return animator;
        }
    }

    private class LineDrawable extends Drawable {
        private Paint mPaint;
        private int mAlpha;

        private int mWidth;
        private int mLength;
        private int mHalfWidth;

        private PathMeasure mPathMeasure;

        private Path mDst = new Path();
        private Path mPath;

        private float mEndPosition = 1f;
        private float mStartPosition;

        private Animator mAnimator;

        public LineDrawable(int color) {
            mPaint = new Paint();
            // 抗锯齿(边缘平衡无锯齿)
            mPaint.setAntiAlias(true);
            // 防抖动(颜色过渡自然)
            mPaint.setDither(true);
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            mPathMeasure = new PathMeasure();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            // canvas.drawLine(0, mHalfWidth, mLength, mHalfWidth, mPaint);
            mDst.reset();
            mPathMeasure.getSegment(mPathMeasure.getLength() * mStartPosition,
                    mPathMeasure.getLength() * mEndPosition, mDst, true);
            canvas.drawPath(mDst, mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            mWidth = bounds.height();
            mHalfWidth = mWidth / 2;
            mLength = bounds.width();

            mPaint.setStrokeWidth(mWidth);

            mPath = new Path();
            mPath.moveTo(0, mHalfWidth);
            mPath.lineTo(mLength, mHalfWidth);

            mPathMeasure.setPath(mPath, false);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            mAlpha = alpha;
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            if (mPaint.getColorFilter() != colorFilter) {
                mPaint.setColorFilter(colorFilter);
            }
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            if (mAlpha == 0) {
                return PixelFormat.TRANSPARENT;
            } else if (mAlpha == 255) {
                return PixelFormat.OPAQUE;
            }
            return PixelFormat.TRANSLUCENT;
        }

        private static final String TAG = "LineDrawable";

        public void setEndPosition(float endPosition) {
            mEndPosition = endPosition;
            invalidateSelf();
        }

        public void setStartPosition(float startPosition) {
            mStartPosition = startPosition;
            invalidateSelf();
        }

        public void show(DrawableAnimatorListener listener) {
            setStartPosition(0);
            mAnimator = buildAnimator(listener, "endPosition");
            mAnimator.start();
        }

        public void hide(DrawableAnimatorListener listener) {
            mAnimator = buildAnimator(listener, "startPosition");
            mAnimator.start();
        }

        @NonNull
        private ObjectAnimator buildAnimator(final DrawableAnimatorListener listener, String property) {
            final ObjectAnimator animator = ObjectAnimator
                    .ofFloat(this, property, 0f, 1f)
                    .setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (listener != null) {
                        listener.onAnimationEnd(animator, LineDrawable.this);
                    }
                }
            });
            return animator;
        }
    }
}
