package com.mercadopago.callbacks;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.PaymentData;

/**
 * Created by mreverter on 1/17/17.
 */
public interface PaymentDataCallback {
    void onSuccess(PaymentData payment);
    void onCancel();
    void onFailure(MercadoPagoError exception);
}
