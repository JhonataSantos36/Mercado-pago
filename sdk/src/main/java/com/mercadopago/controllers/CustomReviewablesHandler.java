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
    private List<Reviewable> congratsReviewables;
    private List<Reviewable> pendingReviewables;
    private Reviewable itemsReview;

    private CustomReviewablesHandler(){
        reviewables = new ArrayList<>();
        congratsReviewables = new ArrayList<>();
        pendingReviewables = new ArrayList<>();
    }

    public static CustomReviewablesHandler getInstance() {
        if(mInstance == null) {
            mInstance = new CustomReviewablesHandler();
        }
        return mInstance;
    }

    public void add(List<Reviewable> reviewables) {
        this.reviewables.addAll(reviewables);
    }

    public void addCongratsReviewables(List<Reviewable> reviewables) {
        this.congratsReviewables.addAll(reviewables);
    }

    public void addPendingReviewables(List<Reviewable> reviewables) {
        this.pendingReviewables.addAll(reviewables);
    }

    public boolean hasReviewables() {
        return !reviewables.isEmpty();
    }

    public boolean hasCongratsReviewables() {
        return !congratsReviewables.isEmpty();
    }

    public boolean hasPendingReviewables() {
        return !pendingReviewables.isEmpty();
    }

    public List<Reviewable> getReviewables() {
        return reviewables;
    }

    public List<Reviewable> getCongratsReviewables() {
        return congratsReviewables;
    }

    public List<Reviewable> getPendingReviewables() {
        return pendingReviewables;
    }

    public void clear() {
        itemsReview = null;
        reviewables = new ArrayList<>();
        congratsReviewables = new ArrayList<>();
        pendingReviewables = new ArrayList<>();
    }



    public void setItemsReview(Reviewable itemsReview) {
        this.itemsReview = itemsReview;
    }

    public boolean hasCustomItemsReviewable() {
        return itemsReview != null;
    }

    public Reviewable getItemsReviewable() {
        return itemsReview;
    }
}
