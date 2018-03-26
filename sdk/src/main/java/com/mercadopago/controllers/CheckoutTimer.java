package com.mercadopago.controllers;

import android.os.CountDownTimer;

import com.mercadopago.observers.TimerObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 11/10/16.
 */

public class CheckoutTimer implements Timer {

    private CountDownTimer mCountDownTimer;
    private static CheckoutTimer mCountDownTimerInstance;

    //Time vars
    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private boolean mShowHours = false;
    private Long mMilliSeconds = 0L;
    private Boolean isCountDownTimerOn = false;
    private String mCurrentTime = "";

    //Observer var
    private final List<TimerObserver> timerObservers = new ArrayList<TimerObserver>();
    private CheckoutTimer.FinishListener mFinishListener;

    private CheckoutTimer() {
    }

    public static synchronized CheckoutTimer getInstance() {
        if (mCountDownTimerInstance == null) {
            mCountDownTimerInstance = new com.mercadopago.controllers.CheckoutTimer();
        }
        return mCountDownTimerInstance;
    }

    //If timer is counting down, this method reset the countdown
    @Override
    public void start(long seconds) {
        if (isCountDownTimerOn) {
            mCountDownTimer.cancel();
        }

        setTime(seconds);

        isCountDownTimerOn = true;
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    @Override
    public void stop() {
        isCountDownTimerOn = false;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void setTime(long seconds) {
        if (seconds >= 3600L) {
            mShowHours = true;
        }

        mMilliSeconds = convertToMilliSeconds(seconds);
        createCountDownTimer();
    }

    public void addObserver(TimerObserver timerObserver) {
        timerObservers.add(timerObserver);
    }

    private void notifyOnTimeChangeAllObservers(String timeToShow) {
        for (TimerObserver timerObserver : timerObservers) {
            timerObserver.onTimeChanged(timeToShow);
        }
    }

    @Override
    public void finishCheckout() {
        for (TimerObserver timerObserver : timerObservers) {
            timerObserver.onFinish();
        }
    }

    private void createCountDownTimer() {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeToShow = calculateTime(millisUntilFinished);
                mCurrentTime = timeToShow;
                notifyOnTimeChangeAllObservers(timeToShow);
            }

            @Override
            public void onFinish() {
                String timeToShow = calculateTime(0);
                notifyOnTimeChangeAllObservers(timeToShow);
                stop();
                if (mFinishListener != null) {
                    mFinishListener.onFinish();
                }
            }
        };
    }

    private long convertToMilliSeconds(long seconds) {
        return seconds * 1000L;
    }

    private String calculateTime(long milliSeconds) {
        mSeconds = (milliSeconds / 1000);
        mMinutes = mSeconds / 60;
        mSeconds = mSeconds % 60;

        mHours = mMinutes / 60;
        mMinutes = mMinutes % 60;

        return getFormattedTime();
    }

    private String getFormattedTime() {
        StringBuilder stringBuilder = new StringBuilder();

        if (mShowHours) {
            stringBuilder.append(getTwoDigitNumber(mHours));
            stringBuilder.append(":");
        }

        stringBuilder.append(getTwoDigitNumber(mMinutes));
        stringBuilder.append(":");
        stringBuilder.append(getTwoDigitNumber(mSeconds));

        return stringBuilder.toString();
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    @Override
    public void setOnFinishListener(com.mercadopago.controllers.CheckoutTimer.FinishListener finishListener) {
        mFinishListener = finishListener;
    }

    @Override
    public Boolean isTimerEnabled() {
        return isCountDownTimerOn;
    }

    public String getCurrentTime() {
        return mCurrentTime;
    }
}
