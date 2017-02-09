package com.mercadopago.model;

import com.mercadopago.callbacks.ReviewableCallback;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 2/2/17.
 */
public abstract class Reviewable implements CustomViewController {

    public ReviewSubscriber reviewSubscriber;
    public ReviewableCallback reviewableCallback;

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

    public void notifyChangeRequired() {
        if(this.reviewSubscriber != null) {
            reviewSubscriber.changeRequired(this);
        }
    }
}
