package com.chiemy.demo.pathmeasuredemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ConcaveArrowProgress mConcaveArrowProgress;
    private ConcaveArrowView mConcaveArrowView;

    private CalendarAnimView mCalendarAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int progress = 200;
        mConcaveArrowProgress = (ConcaveArrowProgress) findViewById(R.id.progressBar);
        mConcaveArrowProgress.setProgress(progress);
        mConcaveArrowProgress.setArrowColor(getResources().getColor(R.color.colorAccent));

        mConcaveArrowView = (ConcaveArrowView) findViewById(R.id.arrowView2);

        SeekBar progressSeekBar = (SeekBar) findViewById(R.id.seekBar_progress);
        progressSeekBar.setOnSeekBarChangeListener(this);
        progressSeekBar.setProgress(progress);

        SeekBar speedSeekBar = (SeekBar) findViewById(R.id.seekBar_speed);
        speedSeekBar.setOnSeekBarChangeListener(this);
        speedSeekBar.setProgress((int) mConcaveArrowView.getMoveSpeed());

        mCalendarAnimView = (CalendarAnimView) findViewById(R.id.calendar_anim_view);
        mCalendarAnimView.post(new Runnable() {
            @Override
            public void run() {
                mCalendarAnimView.showLoading();
            }
        });
        mCalendarAnimView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCalendarAnimView.showSuccess();
            }
        }, 4000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
        if (seekBar.getId() == R.id.seekBar_speed) {
            mConcaveArrowView.setMoveSpeed(p);
        } else {
            mConcaveArrowProgress.setProgress(p);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.seekBar_speed) {
            mConcaveArrowView.setMoveSpeed(seekBar.getProgress());
        }
    }
}
