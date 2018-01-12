package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.plugins.components.SamplePayment;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentProcessor extends PaymentProcessor {

    @Override
    public PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props,
                                                  @NonNull final Context context) {

        return new SamplePayment(
                props.toBuilder()
                    .setToolbarVisible(false)
                    .build()
        );
    }
}