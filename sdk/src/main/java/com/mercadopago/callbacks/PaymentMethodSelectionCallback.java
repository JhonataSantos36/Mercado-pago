package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by mreverter on 28/12/15.
 */
public abstract class PaymentMethodSelectionCallback {
    public abstract void onPaymentMethodSet(PaymentMethod paymentMethod);
    public abstract void onPaymentMethodCleared();
}
