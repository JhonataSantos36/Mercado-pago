package com.mercadopago.callbacks;

import com.mercadopago.model.Payment;

/**
 * Created by vaserber on 1/19/17.
 */

public interface PaymentCallback extends ReturnCallback {
    void onSuccess(Payment payment);
}
