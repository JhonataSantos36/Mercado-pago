package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.model.PaymentResult;

/**
 * Created by nfortuna on 12/18/17.
 */

public class PaymentResultAction extends Action {

    private final PaymentResult paymentResult;

    public PaymentResultAction(@NonNull final PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    @Override
    public String toString() {
        return "Payment result action";
    }
}
