package com.mercadopago.lite.exceptions;

public class CheckoutPreferenceException extends Exception {

    public static final int INVALID_ITEM = 0;
    public static final int EXPIRED_PREFERENCE = 1;
    public static final int INACTIVE_PREFERENCE = 2;
    public static final int INVALID_INSTALLMENTS = 3;
    public static final int EXCLUDED_ALL_PAYMENT_TYPES = 4;
    public static final int PREF_ID_NOT_MATCHING_REQUESTED = 5;
    public static final int NO_EMAIL_FOUND = 6;

    private final int errorCode;

    public CheckoutPreferenceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
