package com.mercadopago.model;

import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 2/2/17.
 */
public abstract class Reviewable implements CustomViewController {

    public Reviewer reviewer;

    public abstract void draw();

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public void notifyChangeRequired() {
        if(this.reviewer != null) {
            this.reviewer.changeRequired();
        }
    }
}
