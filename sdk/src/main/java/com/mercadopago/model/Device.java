package com.mercadopago.model;

import android.content.Context;

public class Device {

    public Fingerprint fingerprint;

    public Device(Context context) {
        this.fingerprint = new Fingerprint(context);
    }
}
