package com.mercadopago.tracking;

import com.mercadopago.tracking.model.AppInformation;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vaserber on 7/13/17.
 */

public class AppInformationTest {

    public static final String MOCKED_FLOW_ID_1 = "12345";
    public static final String MOCKED_CHECKOUT_VERSION_1 = "checkout version 1";
    public static final String MOCKED_FLOW_ID_2 ="54321";
    public static final String MOCKED_CHECKOUT_VERSION_2 = "checkout version 2";
    public static final String MOCKED_PLATFORM = "native/android";
    private static final String MOCKED_ENVIRONMENT = "staging";

    @Test
    public void testAppInformationEquals() {
        AppInformation appInformation1 = new AppInformation.Builder()
                .setFlowId(MOCKED_FLOW_ID_1)
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION_1)
                .setPlatform(MOCKED_PLATFORM)
                .setEnvironment(MOCKED_ENVIRONMENT)
                .build();

        AppInformation appInformation2 = new AppInformation.Builder()
                .setFlowId(MOCKED_FLOW_ID_1)
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION_1)
                .setPlatform(MOCKED_PLATFORM)
                .setEnvironment(MOCKED_ENVIRONMENT)
                .build();

        assertEquals(appInformation1, appInformation2);
    }

    @Test
    public void testAppInformationNotEquals() {
        AppInformation appInformation1 = new AppInformation.Builder()
                .setFlowId(MOCKED_FLOW_ID_1)
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION_1)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        AppInformation appInformation2 = new AppInformation.Builder()
                .setFlowId(MOCKED_FLOW_ID_2)
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION_2)
                .setPlatform(MOCKED_PLATFORM)
                .build();

        assertNotEquals(appInformation1, appInformation2);
    }
}
