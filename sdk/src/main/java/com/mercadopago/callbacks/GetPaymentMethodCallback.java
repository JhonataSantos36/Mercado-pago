package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by mreverter on 4/3/16.
 */
public interface GetPaymentMethodCallback {
    void onSuccess(PaymentMethod paymentMethod);
    void onFailure();
}
