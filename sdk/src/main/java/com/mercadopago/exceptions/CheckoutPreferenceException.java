package com.mercadopago.exceptions;

/**
 * Created by mromar on 3/2/16.
 */
public class CheckoutPreferenceException extends Exception {

    public static final int INVALID_ITEM = 0;
    public static final int EXPIRED_PREFERENCE = 1;
    public static final int INACTIVE_PREFERENCE = 2;
    public static final int INVALID_INSTALLMENTS = 3;
    public static final int EXCLUDED_ALL_PAYMENTTYPES = 4;

    private int errorCode;

    public CheckoutPreferenceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
