package com.mercadopago.plugins.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.examples.R;
import com.mercadopago.plugins.PaymentPluginProcessorResultAction;
import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.plugins.model.BusinessPayment;
import com.mercadopago.plugins.model.ButtonAction;

public class MainPayment extends PluginComponent<Void> {

    private final Handler handler = new Handler();

    static {
        RendererFactory.register(MainPayment.class, MainPaymentRenderer.class);
    }

    public MainPayment(@NonNull final Props props) {
        super(props);
    }

    @Override
    public void onViewAttachedToWindow() {
        executePayment();
    }

    public void executePayment() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                BusinessPayment result = new BusinessPayment.Builder(BusinessPayment.Status.REJECTED, R.drawable.mpsdk_icon_card, "ASD")
                        .setHelp("HELP!")
                        .setPrimaryButton(new ButtonAction("ASD", 23))
                        .setSecondaryButton(new ButtonAction("ASD", 34))
                        .build();

                getDispatcher().dispatch(new PaymentPluginProcessorResultAction(result));
            }
        }, 2000);
    }
}