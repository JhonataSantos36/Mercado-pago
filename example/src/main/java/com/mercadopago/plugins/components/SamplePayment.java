package com.mercadopago.plugins.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.model.Payment;
import com.mercadopago.plugins.PaymentPluginProcessorResultAction;
import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.plugins.model.GenericPayment;

public class SamplePayment extends PluginComponent<Void> {

    static {
        RendererFactory.register(SamplePayment.class, SamplePaymentRenderer.class);
    }

    private final Handler handler = new Handler();

    public SamplePayment(@NonNull final Props props) {
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


                final GenericPayment result = new GenericPayment(
                        123456l,
                        Payment.StatusCodes.STATUS_APPROVED,
                        Payment.StatusDetail.STATUS_DETAIL_APPROVED_PLUGIN_PM,
                        props.paymentData);
                getDispatcher().dispatch(new PaymentPluginProcessorResultAction(result));


            }
        }, 2000);
    }
}