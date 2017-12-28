package com.mercadopago.tracker;

import android.content.Context;
import android.os.Build;

import com.mercadopago.core.Settings;
import com.mercadopago.model.Fingerprint;
import com.mercadopago.tracking.model.AppInformation;
import com.mercadopago.tracking.model.DeviceInfo;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.tracker.MPTracker;
import com.mercadopago.tracking.utils.TrackingUtil;

/**
 * Created by vaserber on 6/5/17.
 */

public class MPTrackingContext {

    private String publicKey;
    private Context context;
    private AppInformation appInformation;
    private DeviceInfo deviceInfo;
    private String trackingStrategy;

    private MPTrackingContext(Builder builder) {
        this.context = builder.context;
        this.deviceInfo = initializeDeviceInfo();
        this.trackingStrategy = builder.trackingStrategy;
        this.publicKey = builder.publicKey;

        if (!builder.publicKey.isEmpty() && builder.checkoutVersion != null) {
            this.appInformation = initializeAppInformation(builder.checkoutVersion);
        }
    }

    private AppInformation initializeAppInformation(final String checkoutVersion) {
        return new AppInformation.Builder()
                .setCheckoutVersion(checkoutVersion)
                .setPlatform("/native/android")
                .setEnvironment(Settings.trackingEnvironment)
                .build();
    }

    private DeviceInfo initializeDeviceInfo() {
        return new DeviceInfo.Builder()
                .setModel(Build.MODEL)
                .setOS("android")
                .setUuid(Fingerprint.getAndroidId(this.context))
                .setSystemVersion(Fingerprint.getDeviceSystemVersion())
                .setScreenSize(Fingerprint.getDeviceResolution(this.context))
                .setResolution(String.valueOf(Fingerprint.getDeviceScreenDensity(this.context)))
                .build();
    }

    public void trackEvent(Event event) {
        MPTracker.getInstance().trackEvent(publicKey, appInformation, deviceInfo, event, context, trackingStrategy);
    }

    public void clearExpiredTracks() {
        MPTracker.getInstance().clearExpiredTracks();
    }

    public static class Builder {
        private Context context;
        private String publicKey;
        private String checkoutVersion;
        private String trackingStrategy;

        public Builder(final Context context, final String publicKey) {
            this.context = context;
            this.publicKey = publicKey;
        }

        public Builder setPublicKey(final String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setCheckoutVersion(final String checkoutVersion) {
            this.checkoutVersion = checkoutVersion;
            return this;
        }

        public Builder setTrackingStrategy(final String trackingStrategy) {
            this.trackingStrategy = trackingStrategy;
            return this;
        }

        public MPTrackingContext build() {
            return new MPTrackingContext(this);
        }
    }
}
