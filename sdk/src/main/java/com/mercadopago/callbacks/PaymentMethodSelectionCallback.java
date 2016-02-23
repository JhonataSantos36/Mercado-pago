package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by mreverter on 28/12/15.
 */
public interface PaymentMethodSelectionCallback {
    public void onPaymentMethodSet(PaymentMethod paymentMethod);
    public void onPaymentMethodCleared();
}
