package com.mercadopago.px_tracking.mocks;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.utils.JsonConverter;

import java.io.InputStream;

/**
 * Created by vaserber on 6/8/17.
 */

public class TrackingStaticMock {

    public static EventTrackIntent getScreenViewEventTrackIntent() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/screenviewevent_list.json");
            return JsonConverter.getInstance().fromJson(json, EventTrackIntent.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getScreenViewTrackJsonIntent() {
        return getFile(InstrumentationRegistry.getContext(), "mocks/screenviewevent_list.json");
    }

    public static AppInformation getApplicationInformation() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/app_information.json");
            return JsonConverter.getInstance().fromJson(json, AppInformation.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static DeviceInfo getDeviceInformation() {
        try {
            String json = getFile(InstrumentationRegistry.getContext(), "mocks/device_information.json");
            return JsonConverter.getInstance().fromJson(json, DeviceInfo.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getFile(Context context, String fileName) {

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);

        } catch (Exception e) {

            return "";
        }
    }
}
