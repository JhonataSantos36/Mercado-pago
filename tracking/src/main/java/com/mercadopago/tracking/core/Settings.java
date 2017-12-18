package com.mercadopago.tracking.core;

import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {
    public static String servicesVersion = "v1";
    public static String eventsTrackingVersion = "2";
    public static HttpLoggingInterceptor.Level OKHTTP_LOGGING = HttpLoggingInterceptor.Level.NONE;
    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}
