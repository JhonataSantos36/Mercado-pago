package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.plugins.components.MainPayment;
import com.mercadopago.plugins.model.PluginPayment;

public class MainPaymentProcessor extends PaymentProcessor {

    private final PluginPayment pluginPayment;

    public MainPaymentProcessor(final PluginPayment pluginPayment) {
        this.pluginPayment = pluginPayment;
    }

    @Override
    public PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props,
                                                  @NonNull final Context context) {

        PluginComponent.Props newProps = props.toBuilder()
                .setToolbarVisible(false)
                .build();

        return new MainPayment(newProps, pluginPayment);
    }
}