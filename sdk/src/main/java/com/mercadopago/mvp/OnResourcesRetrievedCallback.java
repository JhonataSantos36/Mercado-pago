package com.mercadopago.mvp;

import com.mercadopago.exceptions.MPException;

/**
 * All ResourcesProvider implementations' methods containing api calls
 * MUST receive as @param an <code>OnResourcesRetrievedCallback</code>.
 *
 * See also {@link ResourcesProvider}
 */

public interface OnResourcesRetrievedCallback<T> {
    void  onSuccess(T t);
    void onFailure(MPException exception);
}

