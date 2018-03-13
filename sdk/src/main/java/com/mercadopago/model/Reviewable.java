package com.mercadopago.model;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mercadopago.callbacks.PaymentResultReviewableCallback;
import com.mercadopago.callbacks.ReviewableCallback;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 2/2/17.
 */
@Deprecated
public abstract class Reviewable implements CustomViewController {

    public ReviewSubscriber reviewSubscriber;
    public ReviewableCallback reviewableCallback;
    public PaymentResultReviewableCallback paymentResultReviewableCallback;

    private String key;
    private Integer resultCode;

    public abstract void draw();

    @Deprecated
    public void setReviewableCallback(ReviewableCallback callback) {
        this.reviewableCallback = callback;
    }

    public void setResultCode(@NonNull Integer resultCode) {
        this.resultCode = resultCode;
    }

    public void setReviewableCallback(PaymentResultReviewableCallback callback) {
        this.paymentResultReviewableCallback = callback;
    }

    public ReviewableCallback getReviewableCallback() {
        return reviewableCallback;
    }

    public PaymentResultReviewableCallback getPaymentResultReviewableCallback() {
        return paymentResultReviewableCallback;
    }

    public void setReviewSubscriber(ReviewSubscriber subscriber) {
        this.reviewSubscriber = subscriber;
    }

    public String getKey() {
        return ReviewKeys.DEFAULT;
    }

    public void notifyChangeRequired(@NonNull Integer resultCode) {
        if (this.reviewSubscriber != null) {
            reviewSubscriber.changeRequired(resultCode, null);
        }
    }

    public void notifyChangeRequired(@NonNull Integer resultCode, Bundle resultData) {
        if (this.reviewSubscriber != null) {
            reviewSubscriber.changeRequired(resultCode, resultData);
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getResultCode() {
        return resultCode;
    }
}