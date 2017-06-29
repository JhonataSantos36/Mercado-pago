package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentData;

/**
 * Created by vaserber on 2/7/17.
 */

public interface ReviewableCallback {
    void onChangeRequired(PaymentData paymentData);
}
