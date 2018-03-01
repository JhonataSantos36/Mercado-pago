package com.mercadopago.callbacks;

import com.mercadopago.model.ApiException;

/**
 * Created by mreverter on 6/6/16.
 */
public abstract class Callback<T> {

    public int attempts = 0;

    /**
     * Called for [200, 300) responses.
     */
    public abstract void success(T t);

    /**
     * Called for all errors.
     */
    public abstract void failure(ApiException apiException);
}
