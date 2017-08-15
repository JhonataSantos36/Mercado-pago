package com.mercadopago.tracker;

import android.content.Context;
import android.os.Build;

import com.mercadopago.model.Fingerprint;
import com.mercadopago.px_tracking.MPTracker;
import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.Event;

/**
 * Created by vaserber on 6/5/17.
 */


public class MPTrackingContext {

    private static final String TRACKING_STRATEGY_NOT_SET_MESSAGE = "trackingStrategy not set";
    private static final String CONTEXT_NOT_SET_MESSAGE = "context not set";
    private static final String PUBLIC_KEY_NOT_SET_MESSAGE = "publicKey not set";

    private Context context;
    private String clientId;
    private AppInformation appInformation;
    private DeviceInfo deviceInfo;
    private String trackingStrategy;

    private MPTrackingContext(Builder builder) {
        this.context = builder.context;

        if (builder.trackingStrategy == null) {
            throw new IllegalStateException(TRACKING_STRATEGY_NOT_SET_MESSAGE);
        }

        if (builder.context == null) {
            throw new IllegalStateException(CONTEXT_NOT_SET_MESSAGE);
        }

        if (builder.publicKey == null) {
            throw new IllegalStateException(PUBLIC_KEY_NOT_SET_MESSAGE);
        }

        if (this.context != null) {
            this.clientId = initializeClientId();
            this.deviceInfo = initializeDeviceInfo();
            this.trackingStrategy = builder.trackingStrategy;
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

    public void trackEvent(Event event) {
        MPTracker.getInstance().trackEvent(clientId, appInformation, deviceInfo, event, context, trackingStrategy);
    }

    public void clearExpiredTracks() {
        MPTracker.getInstance().clearExpiredTracks();
    }

    public static class Builder {
        private Context context;
        private String publicKey;
        private String checkoutVersion;
        private String trackingStrategy;

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

        public Builder setTrackingStrategy(String trackingStrategy) {
            this.trackingStrategy = trackingStrategy;
            return this;
        }

        public MPTrackingContext build() {
            return new MPTrackingContext(this);
        }
    }
}
