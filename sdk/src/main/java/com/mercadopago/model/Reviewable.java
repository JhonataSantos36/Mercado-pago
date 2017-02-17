package com.mercadopago.model;

import com.mercadopago.callbacks.ReviewableCallback;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 2/2/17.
 */
public abstract class Reviewable implements CustomViewController {

    public ReviewSubscriber reviewSubscriber;
    public ReviewableCallback reviewableCallback;
    private String key;

    public abstract void draw();

    public void setReviewableCallback(ReviewableCallback callback) {
        this.reviewableCallback = callback;
    }

    public ReviewableCallback getReviewableCallback() {
        return reviewableCallback;
    }

    public void setReviewSubscriber(ReviewSubscriber subscriber) {
        this.reviewSubscriber = subscriber;
    }

    public String getKey() {
        return ReviewKeys.DEFAULT;
    }

    public void notifyChangeRequired() {
        if(this.reviewSubscriber != null) {
            reviewSubscriber.changeRequired(this);
        }
    }

    public void setKey(String key) {
        this.key = key;
    }
}
