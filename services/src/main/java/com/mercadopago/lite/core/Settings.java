package com.mercadopago.lite.core;

import com.mercadopago.tracking.constants.TrackingEnvironments;

import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {
    public static final HttpLoggingInterceptor.Level OKHTTP_LOGGING = HttpLoggingInterceptor.Level.NONE;
    public static String servicesVersion = "v1";
    private static String trackingEnvironment = TrackingEnvironments.PRODUCTION;

    public static void setTrackingEnvironment(final String mode) {
        trackingEnvironment = mode;
    }

    public static String getTrackingEnvironment() {
        return trackingEnvironment;
    }

    @Deprecated
    public static void enableBetaServices() {
        servicesVersion = "beta";
    }


}
