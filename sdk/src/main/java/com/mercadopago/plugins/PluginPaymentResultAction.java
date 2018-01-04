package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.plugins.model.PluginPaymentResult;

/**
 * Created by nfortuna on 12/18/17.
 */

public class PluginPaymentResultAction extends Action {

    private final PluginPaymentResult pluginPaymentResult;

    public PluginPaymentResultAction(@NonNull final PluginPaymentResult pluginPaymentResult) {
        this.pluginPaymentResult = pluginPaymentResult;
    }

    public PluginPaymentResult getPluginPaymentResult() {
        return pluginPaymentResult;
    }

    @Override
    public String toString() {
        return "Payment result action";
    }
}
