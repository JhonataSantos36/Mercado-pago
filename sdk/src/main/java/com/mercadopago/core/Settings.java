package com.mercadopago.core;

import com.mercadopago.tracking.constants.TrackingEnvironments;

import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {
    public static final HttpLoggingInterceptor.Level OKHTTP_LOGGING = HttpLoggingInterceptor.Level.NONE;
    public static String servicesVersion = "v1";
    public static String trackingEnvironment = TrackingEnvironments.PRODUCTION;

    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}
