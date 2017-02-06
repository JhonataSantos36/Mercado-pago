package com.mercadopago.controllers;

import com.mercadopago.model.Reviewable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 2/3/17.
 */
public class CustomReviewablesHandler {
    private static CustomReviewablesHandler mInstance;
    private List<Reviewable> reviewables;

    private CustomReviewablesHandler(){
        reviewables = new ArrayList<>();
    }

    public static CustomReviewablesHandler getInstance() {
        if(mInstance == null) {
            mInstance = new CustomReviewablesHandler();
        }
        return mInstance;
    }

    public void add(Reviewable customReviewable) {
        if(!this.reviewables.contains(customReviewable)) {
            this.reviewables.add(customReviewable);
        }
    }

    public boolean hasReviewables() {
        return !reviewables.isEmpty();
    }

    public List<Reviewable> getReviewables() {
        return reviewables;
    }

    public void clear() {
        reviewables = new ArrayList<>();
    }
}
