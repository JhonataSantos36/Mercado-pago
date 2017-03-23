package com.mercadopago.controllers;

import com.mercadopago.constants.ContentLocation;
import com.mercadopago.model.Reviewable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mreverter on 2/3/17.
 */
public class CustomReviewablesHandler {
    private static CustomReviewablesHandler mInstance;
    private List<Reviewable> reviewables;
    private Map<ContentLocation, List<Reviewable>> congratsReviewables;
    private List<Reviewable> pendingReviewables;
    private Reviewable itemsReview;

    private CustomReviewablesHandler(){
        reviewables = new ArrayList<>();
        congratsReviewables = new HashMap<>();
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

    @Deprecated
    public void addCongratsReviewables(List<Reviewable> reviewables) {
        this.congratsReviewables.put(ContentLocation.BOTTOM, reviewables);
    }

    public void addCongratsReviewables(List<Reviewable> reviewables, ContentLocation location) {
        this.congratsReviewables.put(location, reviewables);
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

    public boolean hasCongratsReviewables(ContentLocation location) {
        return congratsReviewables.containsKey(location);
    }

    public boolean hasPendingReviewables() {
        return !pendingReviewables.isEmpty();
    }

    public List<Reviewable> getReviewables() {
        return reviewables;
    }

    @Deprecated
    public List<Reviewable> getCongratsReviewables() {
        return congratsReviewables.get(ContentLocation.BOTTOM);
    }

    public List<Reviewable> getCongratsReviewables(ContentLocation location) {
        return congratsReviewables.get(location);
    }

    public List<Reviewable> getPendingReviewables() {
        return pendingReviewables;
    }

    public void clear() {
        itemsReview = null;
        reviewables = new ArrayList<>();
        congratsReviewables = new HashMap<>();
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
