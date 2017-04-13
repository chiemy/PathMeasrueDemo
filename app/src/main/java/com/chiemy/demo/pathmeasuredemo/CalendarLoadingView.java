package com.chiemy.demo.pathmeasuredemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created: chiemy
 * Date: 17/4/13
 * Description:
 */

public class CalendarLoadingView extends FrameLayout {
    private static final int DEFAULT_DURATION = 300;
    private ImageView mImageView;
    private CustomLoadingView mCustomLoadingView;
    private float mBgRadius;

    public CalendarLoadingView(@NonNull Context context) {
        super(context);
        init();
    }

    public CalendarLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mImageView = new ImageView(getContext());
        addView(mImageView, generateLayoutParams());

        mCustomLoadingView = new CustomLoadingView(getContext());
        mCustomLoadingView.setImageResource(R.mipmap.calendar);
        addView(mCustomLoadingView, generateLayoutParams());
    }

    @NonNull
    private LayoutParams generateLayoutParams() {
        LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER;
        return params1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mImageView.getDrawable() == null) {
            float radius = (float) Math.hypot(mCustomLoadingView.getMeasuredWidth(), mCustomLoadingView.getMeasuredHeight()) / 2;
            mBgRadius = radius + dpToPx(50);
            mImageView.setImageDrawable(buildShapeDrawable(mBgRadius));
        }
    }

    @NonNull
    private ShapeDrawable buildShapeDrawable(float radius) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(Color.parseColor("#f2f2f4"));
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setDither(true);
        shapeDrawable.setIntrinsicHeight((int) radius * 2);
        shapeDrawable.setIntrinsicWidth((int) radius * 2);
        return shapeDrawable;
    }

    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void showLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                showBgEnterAnimation();
                startCalendarEnterAnimation();
            }
        });
    }

    private void showBgEnterAnimation() {
        ObjectAnimator bgAnimator = buildBgViewAnimation(0f, 1f);
        bgAnimator.setDuration(DEFAULT_DURATION);
        bgAnimator.start();
    }

    @NonNull
    private ObjectAnimator buildBgViewAnimation(float fromScale, float toScale) {
        return ObjectAnimator.ofPropertyValuesHolder(mImageView,
                PropertyValuesHolder.ofFloat("scaleX", fromScale, toScale),
                PropertyValuesHolder.ofFloat("scaleY", fromScale, toScale));
    }

    private void startCalendarEnterAnimation() {
        Animation animation = buildCalendarViewAnimation(true, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCustomLoadingView.showLoading();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setStartOffset(DEFAULT_DURATION);
        animation.setDuration(DEFAULT_DURATION);
        mCustomLoadingView.startAnimation(animation);
    }

    @NonNull
    private Animation buildCalendarViewAnimation(boolean enter, Animation.AnimationListener listener) {
        float fromAlpha;
        float toAlpha;
        float fromY;
        float toY;

        Interpolator interpolator;
        if (enter) {
            fromAlpha = 0;
            toAlpha = 1f;
            fromY = -1f;
            toY = 0f;
            interpolator = new OvershootInterpolator(0.5f);
        } else {
            fromAlpha = 1f;
            toAlpha = 0f;
            fromY = 0f;
            toY = 1f;
            interpolator = new AnticipateOvershootInterpolator(0.5f);
        }

        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        TranslateAnimation translateAnimation
                = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, fromY,
                TranslateAnimation.RELATIVE_TO_PARENT, toY);
        translateAnimation.setInterpolator(interpolator);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(listener);
        return animationSet;
    }

    public void showSuccess(final Animator.AnimatorListener listener) {
        mCustomLoadingView.showSuccess(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Animator animator = buildBgViewAnimation(1f, 0f);
                animator.setDuration(DEFAULT_DURATION);
                animator.setStartDelay(2 * DEFAULT_DURATION);
                animator.addListener(listener);
                animator.start();

                Animation calendarViewAnimation = buildCalendarViewAnimation(false, null);
                calendarViewAnimation.setStartOffset(DEFAULT_DURATION);
                calendarViewAnimation.setDuration(DEFAULT_DURATION);
                calendarViewAnimation.setFillAfter(true);
                mCustomLoadingView.startAnimation(calendarViewAnimation);
            }
        });
    }

}
