package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentResult;

/**
 * Created by vaserber on 2/20/17.
 */

public interface PaymentResultReviewableCallback {
    void onChangeRequired(PaymentResult paymentResult);
}
