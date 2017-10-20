package com.mercadopago.px_tracking.strategies;

public interface ConnectivityChecker {
    boolean hasConnection();

    boolean hasWifiConnection();
}
