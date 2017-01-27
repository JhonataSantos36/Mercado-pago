package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentData;

/**
 * Created by vaserber on 1/19/17.
 */

public interface PaymentDataCallback extends ReturnCallback {
    void onSuccess(PaymentData paymentData);
}
