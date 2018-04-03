package com.mercadopago.callbacks;

import java.util.HashMap;

/**
 * Created by vaserber on 1/19/17.
 */

public class CallbackHolder {

    public static final String CONGRATS_PAYMENT_RESULT_CALLBACK = "congratsPaymentResult";
    public static final String PENDING_PAYMENT_RESULT_CALLBACK = "pendingPaymentResult";
    public static final String REJECTED_PAYMENT_RESULT_CALLBACK = "rejectedPaymentResult";

    protected static CallbackHolder callbackHolder = null;

    private PaymentCallback paymentCallback;
    private PaymentDataCallback paymentDataCallback;
    private final HashMap<String, PaymentResultCallback> paymentResultCallbacks = new HashMap<>();

    protected CallbackHolder() {

    }

    public static CallbackHolder getInstance() {
        if (callbackHolder == null) {
            callbackHolder = new CallbackHolder();
        }
        return callbackHolder;
    }

    public void clean() {
        paymentCallback = null;
        paymentDataCallback = null;
    }

    public void setPaymentCallback(PaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
    }

    public void setPaymentDataCallback(PaymentDataCallback paymentDataCallback) {
        this.paymentDataCallback = paymentDataCallback;
    }

    public void addPaymentResultCallback(String type, PaymentResultCallback paymentResultCallback) {
        paymentResultCallbacks.put(type, paymentResultCallback);
    }

    public PaymentCallback getPaymentCallback() {
        return paymentCallback;
    }

    public PaymentDataCallback getPaymentDataCallback() {
        return paymentDataCallback;
    }

    public PaymentResultCallback getPaymentResultCallback(String type) {
        return paymentResultCallbacks.get(type);
    }

    public boolean hasPaymentCallback() {
        return paymentCallback != null;
    }

    public boolean hasPaymentDataCallback() {
        return paymentDataCallback != null;
    }

    public boolean hasPaymentResultCallback(String type) {
        return paymentResultCallbacks.containsKey(type);
    }
}
