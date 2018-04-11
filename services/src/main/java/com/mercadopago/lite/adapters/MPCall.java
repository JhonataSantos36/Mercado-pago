package com.mercadopago.lite.adapters;

import com.mercadopago.lite.callbacks.Callback;

/**
 * Created by mromar on 10/20/17.
 */

public interface MPCall<T> {
    void cancel();

    void enqueue(Callback<T> callback);

    MPCall<T> clone();
}

