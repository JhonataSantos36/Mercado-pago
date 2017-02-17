package com.mercadopago.preferences;

import android.support.annotation.NonNull;

import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.model.Reviewable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 2/13/17.
 */

public class ReviewScreenPreference {

    private String title;
    private String cancelText;
    private String confirmText;
    private String productDetail;
    private transient Reviewable itemsReview;
    private transient List<Reviewable> reviewables;
    private List<String> reviewOrder;
    private String discountDetail;

    public ReviewScreenPreference(Builder builder) {
        this.title = builder.title;
        this.confirmText = builder.confirmText;
        this.cancelText = builder.cancelText;
        this.productDetail = builder.productDetail;
        this.discountDetail = builder.discountDetail;
        this.itemsReview = builder.itemsReview;
        this.reviewables = builder.reviewables;
        this.reviewOrder = builder.reviewOrder;
    }

    public boolean hasCustomReviewables() {
        return itemsReview != null
                || (reviewables != null && !reviewables.isEmpty());
    }

    public List<Reviewable> getCustomReviewables() {
        return reviewables;
    }

    public String getReviewTitle() {
        return title;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public String getCancelText() {
        return cancelText;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public Reviewable getItemsReviewable() {
        return itemsReview;
    }

    public List<String> getReviewOrder() {
        return reviewOrder;
    }

    public String getDiscountDetail() {
        return discountDetail;
    }

    public void addReviewable(@NonNull Reviewable reviewable) {
        if(this.reviewables == null) {
            this.reviewables = new ArrayList<>();
        }
        this.reviewables.add(reviewable);
    }

    public void setItemsReview(@NonNull Reviewable reviewable) {
        reviewable.setKey(ReviewKeys.ITEMS);
        this.itemsReview = reviewable;
    }

    public boolean hasReviewOrder() {
        return reviewOrder != null && !reviewOrder.isEmpty();
    }

    public static class Builder {
        private String title;
        private List<Reviewable> reviewables;
        private String confirmText;
        private String cancelText;
        private String productDetail;
        private String discountDetail;
        private Reviewable itemsReview;
        private List<String> reviewOrder;

        public Builder() {
            this.reviewables = new ArrayList<>();
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setConfirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        public Builder addReviewable(Reviewable customReviewable) {
            this.reviewables.add(customReviewable);
            return this;
        }

        public Builder setCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder setProductDetail(String productDetail) {
            this.productDetail = productDetail;
            return this;
        }

        public Builder setDiscountDetail(String discountDetail) {
            this.discountDetail = discountDetail;
            return this;
        }

        public Builder setItemsReview(Reviewable itemsReview) {
            this.itemsReview = itemsReview;
            return this;
        }

        public Builder setReviewOrder(List<String> reviewOrder) {
            this.reviewOrder = reviewOrder;
            return this;
        }

        public ReviewScreenPreference build() {
            return new ReviewScreenPreference(this);
        }
    }
}
