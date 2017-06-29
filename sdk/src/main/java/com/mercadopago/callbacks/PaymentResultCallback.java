package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentResult;

/**
 * Created by vaserber on 2/21/17.
 */

public interface PaymentResultCallback {
    void onResult(PaymentResult paymentResult);
}
