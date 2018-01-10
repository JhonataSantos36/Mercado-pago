package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.plugins.components.SamplePayment;

import java.util.Map;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentPlugin extends PaymentPlugin {

    @Override
    public PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props) {
        return new SamplePayment(
                props.toBuilder()
                    .setToolbarVisible(false)
                    .build()
        );
    }
}