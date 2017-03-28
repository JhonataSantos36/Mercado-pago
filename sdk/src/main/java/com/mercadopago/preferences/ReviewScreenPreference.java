package com.mercadopago.preferences;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.model.Reviewable;

import java.math.BigDecimal;
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
    private String summaryCustomDescription;
    private Integer summaryCustomDetailColor;
    private BigDecimal summaryCustomAmount;

    public ReviewScreenPreference(Builder builder) {
        this.title = builder.title;
        this.confirmText = builder.confirmText;
        this.cancelText = builder.cancelText;
        this.productDetail = builder.productDetail;
        this.discountDetail = builder.discountDetail;
        this.itemsReview = builder.itemsReview;
        this.reviewables = builder.reviewables;
        this.reviewOrder = builder.reviewOrder;
        this.summaryCustomDescription = builder.summaryCustomDescription;
        this.summaryCustomDetailColor = builder.summaryCustomDetailColor;
        this.summaryCustomAmount = builder.summaryCustomAmount;
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

    public String getSummaryCustomDescription() {
        return summaryCustomDescription;
    }

    public BigDecimal getSummaryCustomAmount() {
        return summaryCustomAmount;
    }

    public Integer getSummaryCustomTextColor() {
        return summaryCustomDetailColor;
    }

    public boolean hasSummaryCustomInfo() {
        return isSummaryCustomDescriptionValid() && isSummaryCustomAmountValid();
    }

    private boolean isSummaryCustomDescriptionValid() {
        return this.summaryCustomDescription != null && !this.summaryCustomDescription.isEmpty();
    }

    private boolean isSummaryCustomAmountValid() {
        return summaryCustomAmount != null && summaryCustomAmount.compareTo(BigDecimal.ZERO) > 0;
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
        private String summaryCustomDescription;
        private BigDecimal summaryCustomAmount;
        private Reviewable itemsReview;
        private List<String> reviewOrder;
        private Integer summaryCustomDetailColor;

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

        public Builder setSummaryCustomDetail(String customRowDescription) {
            this.summaryCustomDescription = customRowDescription;
            return this;
        }

        public Builder setSummaryCustomDetail(String customRowDescription, String summaryCustomTextColor) {
            this.summaryCustomDescription = customRowDescription;
            this.summaryCustomDetailColor = Color.parseColor(summaryCustomTextColor);
            return this;
        }

        public Builder setSummaryCustomDetail(String customRowDescription, @ColorInt Integer summaryCustomDetailColor) {
            this.summaryCustomDescription = customRowDescription;
            this.summaryCustomDetailColor = summaryCustomDetailColor;
            return this;
        }

        public Builder setSummaryCustomDetail(String customRowDescription, BigDecimal customRowAmount) {
            this.summaryCustomDescription = customRowDescription;
            this.summaryCustomAmount = customRowAmount;
            return this;
        }

        public Builder setSummaryCustomDetail(String summaryCustomDescription, BigDecimal summaryCustomAmount, String summaryCustomTextColor) {
            this.summaryCustomDescription = summaryCustomDescription;
            this.summaryCustomAmount = summaryCustomAmount;
            this.summaryCustomDetailColor = Color.parseColor(summaryCustomTextColor);
            return this;
        }

        public Builder setSummaryCustomDetail(String summaryCustomDescription, BigDecimal summaryCustomAmount, @ColorInt Integer summaryCustomDetailColor) {
            this.summaryCustomDescription = summaryCustomDescription;
            this.summaryCustomAmount = summaryCustomAmount;
            this.summaryCustomDetailColor = summaryCustomDetailColor;
            return this;
        }

        public ReviewScreenPreference build() {
            return new ReviewScreenPreference(this);
        }
    }
}
