package com.mercadopago.lite.core;

import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {
    public static HttpLoggingInterceptor.Level OKHTTP_LOGGING = HttpLoggingInterceptor.Level.NONE;
    public static String servicesVersion = "v1";

    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}
