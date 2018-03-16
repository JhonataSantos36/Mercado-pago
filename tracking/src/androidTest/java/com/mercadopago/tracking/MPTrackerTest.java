package com.mercadopago.tracking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.tracking.listeners.TracksListener;
import com.mercadopago.tracking.mocks.MPMockedTrackingService;
import com.mercadopago.tracking.model.ActionEvent;
import com.mercadopago.tracking.model.AppInformation;
import com.mercadopago.tracking.model.DeviceInfo;
import com.mercadopago.tracking.model.ErrorEvent;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.model.StackTraceInfo;
import com.mercadopago.tracking.model.TrackingIntent;
import com.mercadopago.tracking.strategies.TrackingStrategy;
import com.mercadopago.tracking.tracker.MPTracker;
import com.mercadopago.tracking.utils.TrackingUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MPTrackerTest {

    private static final String MOCKED_FLOW_ID = "12345";
    private static final String MOCKED_CHECKOUT_VERSION = "3.0.0";
    private static final String MOCKED_PLATFORM = "native/android";
    private static final String MOCKED_PUBLIC_KEY = "public_key";
    private static final String MOCKED_MODEL = "model";
    private static final String MOCKED_OS = "android";
    private static final String MOCKED_RESOLUTION = "resolution";
    private static final String MOCKED_SCREEN_SIZE = "size";
    private static final String MOCKED_SYSTEM_VERSION = "system_version";
    private static final String MOCKED_ACTION = "action";
    private static final String MOCKED_CATEGORY = "category";
    private static final String MOCKED_LABEL = "label";
    private static final String MOCKED_VALUE = "value";

    private static final String MOCKED_SCREEN_ID_1 = "id_1";
    private static final String MOCKED_SCREEN_NAME_1 = "name_1";
    private static final String MOCKED_ERROR_CLASS_1 = "class_1";
    private static final String MOCKED_ERROR_MESSAGE_1 = "message_1";

    private static final String MOCKED_PROPERTY_KEY_1 = "property_key_1";
    private static final String MOCKED_PROPERTY_VALUE_1 = "property_value_1";

    private static final String ACTION_EVENT_KEY_ACTION = "action";
    private static final String ACTION_EVENT_KEY_CATEGORY = "category";
    private static final String ACTION_EVENT_KEY_LABEL = "label";
    private static final String ACTION_EVENT_KEY_VALUE = "value";
    private static final String ACTION_EVENT_KEY_SCREEN_ID = "screen_id";
    private static final String ACTION_EVENT_KEY_SCREEN_NAME = "screen_name";

    private static final Long MOCKED_PAYMENT_ID = 123L;
    private static final String MOCKED_PAYMENT_TYPE_ID = "pm_type_id";

    private static final String MOCKED_SITE_ID = "site_id";
    private static final String MOCKED_PAYMENT_PLATFORM = "Android";
    private static final String MOCKED_SDK_TYPE = "native";

    private static final String MOCKED_TOKEN_ID = "1234";

    private static final String MOCKED_STACK_TRACE_FILE = "file_name";
    private static final Integer MOCKED_STACK_TRACE_LINE = 123;
    private static final Integer MOCKED_STACK_TRACE_COLUMN = 3;
    private static final String MOCKED_STACK_TRACE_METHOD = "method_name";
    private static final String MOCKED_SCREEN_REVIEW_AND_CONFIRM = "Review and confirm";


    @Test
    public void sendScreenViewEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        AppInformation appInformation = new AppInformation.Builder()
                .setVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        final Event screenViewEvent = new ScreenViewEvent.Builder()
                .setFlowId(MOCKED_FLOW_ID)
                .setScreenId(MOCKED_SCREEN_ID_1)
                .setScreenName(MOCKED_SCREEN_REVIEW_AND_CONFIRM)
                .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {
                assertEquals(((ScreenViewEvent) screenViewEvent).getScreenName(), screenName);
            }

            @Override
            public void onEvent(@NonNull Object event) {

            }
        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, screenViewEvent, appContext, TrackingUtil.BATCH_STRATEGY);

        TrackingStrategy strategy = MPTracker.getInstance().getTrackingStrategy();

        assertEquals(strategy.getAppInformation(), appInformation);
        assertEquals(strategy.getDeviceInfo(), deviceInfo);
    }

    @Test
    public void sendActionEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        AppInformation appInformation = new AppInformation.Builder()
                .setVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        Event actionEvent = new ActionEvent.Builder()
                .setAction(MOCKED_ACTION)
                .setCategory(MOCKED_CATEGORY)
                .setLabel(MOCKED_LABEL)
                .setValue(MOCKED_VALUE)
                .setScreenId(MOCKED_SCREEN_ID_1)
                .setScreenName(MOCKED_SCREEN_NAME_1)
                .addProperty(MOCKED_PROPERTY_KEY_1, MOCKED_PROPERTY_VALUE_1)
                .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener<HashMap>() {

            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEvent(@NonNull HashMap eventMap) {
                assertEquals(eventMap.get(ACTION_EVENT_KEY_ACTION), MOCKED_ACTION);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_CATEGORY), MOCKED_CATEGORY);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_LABEL), MOCKED_LABEL);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_VALUE), MOCKED_VALUE);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_SCREEN_ID), MOCKED_SCREEN_ID_1);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_SCREEN_NAME), MOCKED_SCREEN_NAME_1);
            }

        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, actionEvent, appContext, TrackingUtil.BATCH_STRATEGY);
    }

    @Test
    public void sendErrorEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        AppInformation appInformation = new AppInformation.Builder()
                .setVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        Event errorEvent = new ErrorEvent.Builder()
                .setErrorClass(MOCKED_ERROR_CLASS_1)
                .setErrorMessage(MOCKED_ERROR_MESSAGE_1)
                .setStackTraceList(new ArrayList<StackTraceInfo>())
                .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEvent(@NonNull Object event) {

            }
        });

    }

    @Test
    public void sendPaymentTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEvent(@NonNull Object event) {

            }
        });

        //Initialize tracker before creating a payment
        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        PaymentIntent paymentIntent = MPTracker.getInstance().trackPayment(MOCKED_PAYMENT_ID, MOCKED_PAYMENT_TYPE_ID);

        assertEquals(paymentIntent.mPaymentId, String.valueOf(MOCKED_PAYMENT_ID));
        assertEquals(paymentIntent.mPlatform, MOCKED_PAYMENT_PLATFORM);
        assertEquals(paymentIntent.mPublicKey, MOCKED_PUBLIC_KEY);
        assertEquals(paymentIntent.mSdkVersion, MOCKED_CHECKOUT_VERSION);
        assertEquals(paymentIntent.mSite, MOCKED_SITE_ID);
        assertEquals(paymentIntent.mType, MOCKED_SDK_TYPE);
    }

    @Test
    public void sendTokenTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEvent(@NonNull Object event) {

            }
        });

        //Initialize tracker before creating a token
        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        TrackingIntent trackingIntent = MPTracker.getInstance().trackToken(MOCKED_TOKEN_ID);

        assertEquals(trackingIntent.mPlatform, MOCKED_PAYMENT_PLATFORM);
        assertEquals(trackingIntent.mPublicKey, MOCKED_PUBLIC_KEY);
        assertEquals(trackingIntent.mSdkVersion, MOCKED_CHECKOUT_VERSION);
        assertEquals(trackingIntent.mSite, MOCKED_SITE_ID);
        assertEquals(trackingIntent.mType, MOCKED_SDK_TYPE);
        assertEquals(trackingIntent.mCardToken, MOCKED_TOKEN_ID);
    }

    @Test
    public void sendErrorEventWithStackTraceInfo() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        AppInformation appInformation = new AppInformation.Builder()
                .setVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        List<StackTraceInfo> stackTraceInfoList = new ArrayList<>();
        StackTraceInfo stackTraceInfo = new StackTraceInfo(MOCKED_STACK_TRACE_FILE, MOCKED_STACK_TRACE_LINE,
                MOCKED_STACK_TRACE_COLUMN, MOCKED_STACK_TRACE_METHOD);
        stackTraceInfoList.add(stackTraceInfo);

        ErrorEvent errorEvent = new ErrorEvent.Builder()
                .setErrorClass(MOCKED_ERROR_CLASS_1)
                .setErrorMessage(MOCKED_ERROR_MESSAGE_1)
                .setStackTraceList(stackTraceInfoList)
                .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, appContext);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEvent(@NonNull Object event) {

            }
        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, errorEvent, appContext, null);

        ErrorEvent sentEvent = (ErrorEvent) MPTracker.getInstance().getEvent();
        List<StackTraceInfo> sentStackTraceList = sentEvent.getStackTraceList();
        assertTrue(sentStackTraceList.size() == 1);
        StackTraceInfo sentStackTrace = sentStackTraceList.get(0);
        assertEquals(sentStackTrace.getFile(), MOCKED_STACK_TRACE_FILE);
        assertEquals(sentStackTrace.getLineNumber(), MOCKED_STACK_TRACE_LINE);
        assertEquals(sentStackTrace.getColumnNumber(), MOCKED_STACK_TRACE_COLUMN);
        assertEquals(sentStackTrace.getMethod(), MOCKED_STACK_TRACE_METHOD);
    }

}
