package com.mercadopago.lite.util;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    private static final String NO_CONNECTION_AVAILABLE = "No connection available";

    public NoConnectivityException() {
        super(NO_CONNECTION_AVAILABLE);
    }
}
