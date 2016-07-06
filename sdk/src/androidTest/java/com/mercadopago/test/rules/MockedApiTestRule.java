package com.mercadopago.test.rules;

import android.app.Activity;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.mercadopago.test.FakeAPI;
import com.mercadopago.util.JsonUtil;

/**
 * Created by mreverter on 29/2/16.
 */
public class MockedApiTestRule<A extends Activity> extends ActivityTestRule<A> {

    private Boolean intentsActive;
    private Boolean isActivityFinished;

    public MockedApiTestRule(Class<A> activityClass) {
        super(activityClass);
        intentsActive = false;
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
        intentsActive = false;
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        intentsActive = false;
    }

    private void startFakeAPI() {
        FakeAPI.getInstance().start();
    }

    private void stopFakeAPI() {
        FakeAPI.getInstance().shutDown();
    }

    public <T> void addApiResponseToQueue(T response, int statusCode, String reason) {
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        FakeAPI.getInstance().addResponseToQueue(jsonResponse, statusCode, reason);
    }

    public void addApiResponseToQueue(String jsonResponse, int statusCode, String reason) {
        FakeAPI.getInstance().addResponseToQueue(jsonResponse, statusCode, reason);
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        sleepThread(500);
        isActivityFinished = false;
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        isActivityFinished = true;
        if(intentsActive) {
            this.releaseIntents();
        }
    }

    public void initIntentsRecording() {
        Intents.init();
        intentsActive = true;
    }

    public void releaseIntents() {
        Intents.release();
        intentsActive = false;
    }

    public void restartIntents() {
        this.releaseIntents();
        this.initIntentsRecording();
    }

    public boolean isActivityFinishedOrFinishing() {
        return isActivityFinished || getActivity().isFinishing();
    }

    protected void sleepThread(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (Exception ex) {
            // do nothing
        }
    }
}