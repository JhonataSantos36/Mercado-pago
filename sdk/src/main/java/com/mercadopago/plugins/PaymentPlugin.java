package com.mercadopago.plugins;

import android.support.annotation.NonNull;

/**
 * Created by nfortuna on 12/11/17.
 */

public abstract class PaymentPlugin {
    public abstract PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props);
}
