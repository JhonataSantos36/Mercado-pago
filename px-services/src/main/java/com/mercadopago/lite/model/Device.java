package com.mercadopago.lite.model;
import android.content.Context;

import com.mercadopago.tracking.model.Fingerprint;

public class Device {
    private Fingerprint fingerprint;

    public Device(Context context) {
        this.fingerprint = new Fingerprint(context);
    }
}
