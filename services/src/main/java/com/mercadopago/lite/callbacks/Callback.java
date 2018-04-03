package com.mercadopago.lite.callbacks;

import com.mercadopago.lite.exceptions.ApiException;

/**
 * Created by mromar on 10/20/17.
 */

public abstract class Callback<T> {
    /**
     * Called for [200, 300) responses.
     */
    public abstract void success(T t);

    /**
     * Called for all errors.
     */
    public abstract void failure(ApiException apiException);

    public int attempts = 0;
}
