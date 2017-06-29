package com.mercadopago.core;

import com.mercadopago.callbacks.CallbackHolder;

/**
 * Created by vaserber on 1/27/17.
 */

public class CallbackHolderClearable extends CallbackHolder {

    private CallbackHolderClearable() {
        super();
    }

    public static void clear() {
        callbackHolder = null;
    }
}
