package com.mercadopago.callbacks;

import com.mercadopago.exceptions.MercadoPagoError;

/**
 * Created by vaserber on 1/20/17.
 */

public interface ReturnCallback {
    void onCancel();
    void onFailure(MercadoPagoError error);
}
