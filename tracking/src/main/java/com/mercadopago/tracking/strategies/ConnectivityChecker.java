package com.mercadopago.tracking.strategies;

public interface ConnectivityChecker {
    boolean hasConnection();

    boolean hasWifiConnection();
}
