package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.plugins.model.PluginPayment;

public class PaymentPluginProcessorResultAction extends Action {

    private final PluginPayment pluginPaymentResult;

    public PaymentPluginProcessorResultAction(@NonNull final PluginPayment pluginPaymentResult) {
        this.pluginPaymentResult = pluginPaymentResult;
    }

    public PluginPayment getPluginPaymentResult() {
        return pluginPaymentResult;
    }

    @Override
    public String toString() {
        return "Payment result action";
    }
}
