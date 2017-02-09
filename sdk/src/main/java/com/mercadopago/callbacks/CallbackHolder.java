package com.mercadopago.callbacks;

/**
 * Created by vaserber on 1/19/17.
 */

public class CallbackHolder {

    protected static CallbackHolder callbackHolder = null;

    private PaymentCallback paymentCallback;
    private PaymentDataCallback paymentDataCallback;

    protected CallbackHolder() {

    }

    public static CallbackHolder getInstance() {
        if (callbackHolder == null) {
            callbackHolder = new CallbackHolder();
        }
        return callbackHolder;
    }

    public void setPaymentCallback(PaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
    }

    public void setPaymentDataCallback(PaymentDataCallback paymentDataCallback) {
        this.paymentDataCallback = paymentDataCallback;
    }

    public PaymentCallback getPaymentCallback() {
        return paymentCallback;
    }

    public PaymentDataCallback getPaymentDataCallback() {
        return paymentDataCallback;
    }

    public boolean hasPaymentCallback() {
        return paymentCallback != null;
    }

    public boolean hasPaymentDataCallback() {
        return paymentDataCallback != null;
    }

}
