package com.chiemy.demo.pathmeasuredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomAnimActivity extends AppCompatActivity {
    private CalendarLoadingView mCalendarLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_anim);

        mCalendarLoadingView = (CalendarLoadingView) findViewById(R.id.calendar_loading_view);
        mCalendarLoadingView.showLoading();

        mCalendarLoadingView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCalendarLoadingView.showSuccess();
            }
        }, 5000);
    }
}
