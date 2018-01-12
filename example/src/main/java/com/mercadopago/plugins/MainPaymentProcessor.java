package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.plugins.components.MainPayment;

/**
 * Created by nfortuna on 12/13/17.
 */

public class MainPaymentProcessor extends PaymentProcessor {

    @Override
    public PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props,
                                                  @NonNull final Context context) {
        return new MainPayment(
                props.toBuilder()
                    .setToolbarVisible(false)
                    .build()
        );
    }
}