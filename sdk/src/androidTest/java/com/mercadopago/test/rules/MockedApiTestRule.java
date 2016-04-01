package com.mercadopago.test.rules;

import android.app.Activity;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.mercadopago.test.MockedHttpClient;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

/**
 * Created by mreverter on 29/2/16.
 */
public class MockedApiTestRule<A extends Activity> extends ActivityTestRule<A> {

    private Boolean intentsActive;
    private MockedHttpClient mockedHttpClient;
    private Boolean isActivityFinished;

    public MockedApiTestRule(Class<A> activityClass) {
        super(activityClass);
        intentsActive = false;

        setUpMockedClient();
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
        intentsActive = false;
        setUpMockedClient();
    }

    public MockedApiTestRule(Class<A> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        intentsActive = false;
        setUpMockedClient();
    }

    private void setUpMockedClient() {
        mockedHttpClient = new MockedHttpClient();
        HttpClientUtil.bindClient(mockedHttpClient);
    }

    public <T> void addApiResponseToQueue(T response, int statusCode, String reason) {
        String jsonResponse = JsonUtil.getInstance().toJson(response);
        mockedHttpClient.addResponseToQueue(jsonResponse, statusCode, reason);
    }

    public void addApiResponseToQueue(String jsonResponse, int statusCode, String reason) {
        mockedHttpClient.addResponseToQueue(jsonResponse, statusCode, reason);
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        isActivityFinished = false;
        if(!intentsActive) {
            this.initIntents();
        }
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        isActivityFinished = true;
        if(intentsActive) {
            this.releaseIntents();
        }
        HttpClientUtil.unbindClient();
    }

    public void initIntents() {
        Intents.init();
        intentsActive = true;
    }

    public void releaseIntents() {
        Intents.release();
        intentsActive = false;
    }

    public void restartIntents() {
        this.releaseIntents();
        this.initIntents();
    }

    public boolean isActivityFinishedOrFinishing() {
        return isActivityFinished || getActivity().isFinishing();
    }
}