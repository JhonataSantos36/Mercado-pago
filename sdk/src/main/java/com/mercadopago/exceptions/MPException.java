package com.mercadopago.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 3/2/16.
 */
public class MPException extends Exception {

    private int errorCode;

    public MPException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
