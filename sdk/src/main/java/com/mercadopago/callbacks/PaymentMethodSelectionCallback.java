package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by mreverter on 28/12/15.
 */
public interface PaymentMethodSelectionCallback {
    void onPaymentMethodSet(PaymentMethod paymentMethod);
    void onPaymentMethodCleared();
}
