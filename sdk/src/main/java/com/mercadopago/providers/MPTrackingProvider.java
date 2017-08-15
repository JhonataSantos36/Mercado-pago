package com.mercadopago.providers;

import android.content.Context;
import android.os.Build;

import com.mercadopago.model.Fingerprint;
import com.mercadopago.px_tracking.MPTracker;
import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 6/5/17.
 */

public class MPTrackingProvider {

    private Context context;
    private List<Event> eventList;
    private String clientId;
    private AppInformation appInformation;
    private DeviceInfo deviceInfo;

    private MPTrackingProvider(Builder builder) {
        this.context = builder.context;
        this.eventList = new ArrayList<>();

        if (builder.eventList != null && !builder.eventList.isEmpty()) {
            this.eventList = builder.eventList;
        }
        if (this.context != null) {
            this.clientId = initializeClientId();
            this.deviceInfo = initializeDeviceInfo();
        }
        if (builder.publicKey != null && builder.checkoutVersion != null) {
            this.appInformation = initializeAppInformation(builder.publicKey, builder.checkoutVersion);
        }

    }

    private String initializeClientId() {
        return Fingerprint.getAndroidId(this.context);
    }

    private AppInformation initializeAppInformation(String publicKey, String checkoutVersion) {
        return new AppInformation.Builder()
                .setPublicKey(publicKey)
                .setCheckoutVersion(checkoutVersion)
                .setPlatform("native/android")
                .build();
    }

    private DeviceInfo initializeDeviceInfo() {
        return new DeviceInfo.Builder()
                .setModel(Build.MODEL)
                .setOS("android")
                .setSystemVersion(Fingerprint.getDeviceSystemVersion())
                .setScreenSize(Fingerprint.getDeviceResolution(this.context))
                .setResolution(String.valueOf(Fingerprint.getDeviceScreenDensity(this.context)))
                .build();
    }


    public void addTrackEvent(Event event) {
        if (eventList == null) {
            this.eventList = new ArrayList<>();
        }
        this.eventList.add(event);

        MPTracker.getInstance().trackEvents(clientId, appInformation, deviceInfo, eventList, context);
    }


    public static class Builder {
        private Context context;
        private String publicKey;
        private String checkoutVersion;
        private List<Event> eventList;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setCheckoutVersion(String checkoutVersion) {
            this.checkoutVersion = checkoutVersion;
            return this;
        }

        public Builder setEventList(List<Event> eventList) {
            this.eventList = eventList;
            return this;
        }

        public MPTrackingProvider build() {
            return new MPTrackingProvider(this);
        }
    }
}
