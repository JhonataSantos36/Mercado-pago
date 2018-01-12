package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.plugins.model.ProcessorPaymentResult;

/**
 * Created by nfortuna on 12/18/17.
 */

public class PluginPaymentResultAction extends Action {

    private final ProcessorPaymentResult processorPaymentResult;

    public PluginPaymentResultAction(@NonNull final ProcessorPaymentResult processorPaymentResult) {
        this.processorPaymentResult = processorPaymentResult;
    }

    public ProcessorPaymentResult getPluginPaymentResult() {
        return processorPaymentResult;
    }

    @Override
    public String toString() {
        return "Payment result action";
    }
}
