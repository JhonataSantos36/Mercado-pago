package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

public abstract class PaymentProcessor {

    public boolean support(@NonNull final String paymentMethodId,
                           @NonNull final Map<String, Object> data) {
        return true;
    }

    public abstract @NonNull
    PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props,
                                           @NonNull final Context context);
}
