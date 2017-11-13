package com.mercadopago.tracking.core;

public class Settings {
    public static String servicesVersion = "v1";
    public static String eventsTrackingVersion = "1";

    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}
