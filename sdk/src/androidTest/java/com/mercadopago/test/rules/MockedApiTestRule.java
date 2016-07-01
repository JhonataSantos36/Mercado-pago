package com.mercadopago.test.rules;

import android.app.Activity;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.FakeInterceptor;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Field;

/**
 * Created by mreverter on 29/2/16.
 */
public class MockedApiTestRule<A extends Activity> extends ActivityTestRule<A> {

    private Boolean intentsActive;
    private FakeInterceptor fakeInterceptor;
    private Boolean isActivityFinished;

    public MockedApiTestRule(Class<A> activityClass) {
        super(activityClass);
        intentsActive = false;
        setUpFakeInterceptor();
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
        intentsActive = false;
        setUpFakeInterceptor();
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        intentsActive = false;
        setUpFakeInterceptor();
    }

    private void setUpFakeInterceptor() {
        fakeInterceptor = new FakeInterceptor();
        HttpClientUtil.bindInterceptor(fakeInterceptor);
    }

    public <T> void addApiResponseToQueue(T response, int statusCode, String reason) {
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        FakeAPI.addResponseToQueue(jsonResponse, statusCode, reason);
    }

    public void addApiResponseToQueue(String jsonResponse, int statusCode, String reason) {
        FakeAPI.addResponseToQueue(jsonResponse, statusCode, reason);
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
        FakeAPI.cleanQueue();
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

    public boolean isFinishingOrFinishedWithResult(int resultCode) {
        boolean finishingWithResult = false;
        if(isActivityFinishedOrFinishing()) {
            try {
                Field field = Activity.class.getDeclaredField("mResultCode");
                field.setAccessible(true);
                int actualResultCode = (Integer) field.get(getActivity());
                finishingWithResult = (actualResultCode == resultCode);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData.Time to update the reflection code.", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return finishingWithResult;
    }

    protected void sleepThread(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (Exception ex) {
            // do nothing
        }
    }
}