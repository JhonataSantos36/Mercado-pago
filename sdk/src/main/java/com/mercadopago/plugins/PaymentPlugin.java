package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by nfortuna on 12/11/17.
 */

public abstract class PaymentPlugin {

    public boolean support(final String paymentMethodId, final Map data) {
        return true;
    }

    public abstract PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props);
}
