package com.mercadopago.tracking.strategies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityCheckerImpl implements ConnectivityChecker {
    private Context mContext;

    public ConnectivityCheckerImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean hasConnection() {

        NetworkInfo networkInfo = getAvailableNetworkInfo();

        if (networkInfo != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasWifiConnection() {

        NetworkInfo networkInfo = getAvailableNetworkInfo();

        if (networkInfo != null && hasWifiNetwork(networkInfo)) {
            return true;
        }

        return false;
    }

    private boolean hasWifiNetwork(NetworkInfo networkInfo) {
        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private NetworkInfo getAvailableNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        return null;
    }

}
