package com.mercadopago.plugins.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Payment;
import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.plugins.PluginPaymentResultAction;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.plugins.model.PluginPaymentResult;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePayment extends PluginComponent {

    private final Handler handler = new Handler();

    static {
        RendererFactory.register(SamplePayment.class, SamplePaymentRenderer.class);
    }

    public SamplePayment(@NonNull final Props props) {
        super(props);
        executePayment();
    }

    public void executePayment() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final PaymentMethodInfo paymentMethodInfo =
                        CheckoutStore.getInstance().getSelectedPaymentMethod();

                final PluginPaymentResult result = new PluginPaymentResult(
                        123456l,
                        Payment.StatusCodes.STATUS_REJECTED,
                        Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM,
                        paymentMethodInfo);

                getDispatcher().dispatch(new PluginPaymentResultAction(result));
            }
        }, 2500);
    }

    public String getDocument() {
        return (String) props.data.get("docu");
    }
}