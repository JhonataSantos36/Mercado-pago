package com.mercadopago.plugins.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.plugins.PaymentPluginProcessorResultAction;
import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.plugins.model.PluginPayment;

public class MainPayment extends PluginComponent<Void> {

    private final Handler handler = new Handler();

    static {
        RendererFactory.register(MainPayment.class, MainPaymentRenderer.class);
    }

    private final PluginPayment pluginPayment;


    public MainPayment(@NonNull final Props props, PluginPayment pluginPayment) {
        super(props);
        this.pluginPayment = pluginPayment;
    }

    @Override
    public void onViewAttachedToWindow() {
        executePayment();
    }

    public void executePayment() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDispatcher().dispatch(new PaymentPluginProcessorResultAction(pluginPayment));
            }
        }, 2000);
    }
}