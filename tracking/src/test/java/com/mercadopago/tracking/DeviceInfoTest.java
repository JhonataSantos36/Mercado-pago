package com.mercadopago.tracking;

import com.mercadopago.tracking.model.DeviceInfo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vaserber on 7/13/17.
 */

public class DeviceInfoTest {

    public static final String MOCKED_OS = "android";
    public static final String MOCKED_MODEL_1 = "Google Nexus 5";
    public static final String MOCKED_UUID_1 = "ABCD";
    public static final String MOCKED_SYSTEM_VERSION_1 = "6.0";
    public static final String MOCKED_SCREEN_SIZE_1 = "1080x1776";
    public static final String MOCKED_RESOLUTION_1 = "3.0";

    public static final String MOCKED_MODEL_2 = "Google Nexus 6";
    public static final String MOCKED_UUID_2 = "CDBA";
    public static final String MOCKED_SYSTEM_VERSION_2 = "7.0";
    public static final String MOCKED_SCREEN_SIZE_2 = "640x880";
    public static final String MOCKED_RESOLUTION_2 = "4.0";

    @Test
    public void testDeviceInfoEquals() {
        DeviceInfo deviceInfo1 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_1)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_1)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_1)
                .setScreenSize(MOCKED_SCREEN_SIZE_1)
                .setResolution(MOCKED_RESOLUTION_1)
                .build();

        DeviceInfo deviceInfo2 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_1)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_1)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_1)
                .setScreenSize(MOCKED_SCREEN_SIZE_1)
                .setResolution(MOCKED_RESOLUTION_1)
                .build();

        assertEquals(deviceInfo1, deviceInfo2);

    }

    @Test
    public void testDeviceInfoNotEquals() {
        DeviceInfo deviceInfo1 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_1)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_1)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_1)
                .setScreenSize(MOCKED_SCREEN_SIZE_1)
                .setResolution(MOCKED_RESOLUTION_1)
                .build();

        DeviceInfo deviceInfo2 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_2)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_2)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_1)
                .setScreenSize(MOCKED_SCREEN_SIZE_1)
                .setResolution(MOCKED_RESOLUTION_1)
                .build();

        assertNotEquals(deviceInfo1, deviceInfo2);

    }

    @Test
    public void testDeviceInfoAgainNotEquals() {
        DeviceInfo deviceInfo1 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_1)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_1)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_1)
                .setScreenSize(MOCKED_SCREEN_SIZE_1)
                .setResolution(MOCKED_RESOLUTION_1)
                .build();

        DeviceInfo deviceInfo2 = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL_2)
                .setOS(MOCKED_OS)
                .setUuid(MOCKED_UUID_2)
                .setSystemVersion(MOCKED_SYSTEM_VERSION_2)
                .setScreenSize(MOCKED_SCREEN_SIZE_2)
                .setResolution(MOCKED_RESOLUTION_2)
                .build();

        assertNotEquals(deviceInfo1, deviceInfo2);

    }
}
