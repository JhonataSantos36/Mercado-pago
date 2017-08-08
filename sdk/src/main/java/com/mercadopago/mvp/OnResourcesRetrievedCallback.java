package com.mercadopago.mvp;

import com.mercadopago.exceptions.MercadoPagoError;

/**
 * All ResourcesProvider implementations' methods containing api calls
 * MUST receive as @param an <code>OnResourcesRetrievedCallback</code>.
 *
 * See also {@link ResourcesProvider}
 */

public interface OnResourcesRetrievedCallback<T> {
    void onSuccess(T t);
    void onFailure(MercadoPagoError error);
}

