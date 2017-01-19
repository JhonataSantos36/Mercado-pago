package com.mercadopago.callbacks;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Payment;

/**
 * Created by mreverter on 1/17/17.
 */
public interface PaymentCallback {
    void onSuccess(Payment payment);
    void onCancel();
    void onFailure(MercadoPagoError exception);
}
