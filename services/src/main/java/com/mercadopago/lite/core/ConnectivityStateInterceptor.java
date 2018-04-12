package com.mercadopago.lite.core;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;

import com.mercadopago.lite.util.NoConnectivityException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ConnectivityStateInterceptor implements Interceptor {

    private final ConnectivityManager connectivityManager;

    public ConnectivityStateInterceptor(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (connectivityManager.getActiveNetworkInfo() == null
                || !connectivityManager.getActiveNetworkInfo().isAvailable()
                || !connectivityManager.getActiveNetworkInfo().isConnected()) {
            throw new NoConnectivityException();
        } else {
            return chain.proceed(chain.request());
        }
    }
}
