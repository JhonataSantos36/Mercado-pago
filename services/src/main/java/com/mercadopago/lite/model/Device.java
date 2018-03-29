package com.mercadopago.lite.model;

import android.content.Context;

public class Device {

    public Fingerprint fingerprint;

    public Device(Context context) {
        fingerprint = new Fingerprint(context);
    }
}
