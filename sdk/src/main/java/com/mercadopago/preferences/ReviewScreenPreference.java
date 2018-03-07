package com.mercadopago.preferences;

import android.support.annotation.NonNull;

import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.model.Reviewable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ReviewScreenPreference {

    private String title;
    private String cancelText;
    private String confirmText;

    private transient Reviewable itemsSummary;
    private transient List<Reviewable> reviewables;
    private List<String> reviewOrder;

    private BigDecimal productAmount;
    private BigDecimal discountAmount;
    private BigDecimal chargeAmount;
    private BigDecimal taxesAmount;
    private BigDecimal shippingAmount;
    private BigDecimal arrearsAmount;
    private String productTitle;
    private String disclaimerText;
    private String disclaimerColor;

    private String quantityTitle;
    private String amountTitle;
    private boolean showQuantityRow;
    private boolean showAmountTitle;

    public ReviewScreenPreference(Builder builder) {
        this.title = builder.title;
        this.confirmText = builder.confirmText;
        this.cancelText = builder.cancelText;
        this.itemsSummary = builder.itemsSummary;
        this.reviewables = builder.reviewables;
        this.reviewOrder = builder.reviewOrder;

        this.productAmount = builder.productAmount;
        this.discountAmount = builder.discountAmount;
        this.chargeAmount = builder.chargeAmount;
        this.taxesAmount = builder.taxesAmount;
        this.shippingAmount = builder.shippingAmount;
        this.arrearsAmount = builder.arrearsAmount;
        this.productTitle = builder.productTitle;
        this.disclaimerText = builder.disclaimerText;
        this.disclaimerColor = builder.disclaimerTextColor;

        this.quantityTitle = builder.quantityTitle;
        this.amountTitle = builder.amountTitle;
        this.showQuantityRow = builder.showQuantityRow;
        this.showAmountTitle = builder.showAmountTitle;
    }

    public boolean hasCustomReviewables() {
        return itemsSummary != null
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

    public BigDecimal getProductAmount() {
        return productAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public BigDecimal getTaxesAmount() {
        return taxesAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public BigDecimal getArrearsAmount() {
        return arrearsAmount;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getDisclaimerText() {
        return disclaimerText;
    }

    public String getDisclaimerTextColor() {
        return disclaimerColor;
    }

    public String getQuantityTitle() {
        return quantityTitle;
    }

    public String getAmountTitle() {
        return amountTitle;
    }

    public boolean showQuantityRow() {
        return showQuantityRow;
    }

    public boolean showAmountTitle() {
        return showAmountTitle;
    }

    public Reviewable getItemsReviewable() {
        return itemsSummary;
    }

    public List<String> getReviewOrder() {
        return reviewOrder;
    }

    public void addReviewable(@NonNull Reviewable reviewable) {
        if (this.reviewables == null) {
            this.reviewables = new ArrayList<>();
        }
        this.reviewables.add(reviewable);
    }

    public void setItemsSummary(@NonNull Reviewable reviewable) {
        reviewable.setKey(ReviewKeys.ITEMS);
        this.itemsSummary = reviewable;
    }

    public boolean hasProductAmount() {
        return productAmount != null && productAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);

        totalAmount = (productAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(productAmount);
        totalAmount = (chargeAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(chargeAmount);
        totalAmount = (taxesAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(taxesAmount);
        totalAmount = (shippingAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(shippingAmount);
        totalAmount = (arrearsAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(arrearsAmount);
        totalAmount = (discountAmount == null) ? totalAmount.subtract(new BigDecimal(0)) : totalAmount.subtract(discountAmount);

        return totalAmount;
    }

    public boolean hasReviewOrder() {
        return reviewOrder != null && !reviewOrder.isEmpty();
    }

    @Deprecated
    public static class Builder {
        private String title;
        private String confirmText;
        private String cancelText;
        private Reviewable itemsSummary;
        private List<String> reviewOrder;
        private List<Reviewable> reviewables;
        private BigDecimal productAmount;
        private BigDecimal discountAmount;
        private BigDecimal chargeAmount;
        private BigDecimal taxesAmount;
        private BigDecimal shippingAmount;
        private BigDecimal arrearsAmount;
        private String productTitle;
        private String disclaimerText;
        private String disclaimerTextColor;

        private String quantityTitle;
        private String amountTitle;
        private boolean showQuantityRow = true;
        private boolean showAmountTitle = true;

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

        public Builder setItemsSummary(Reviewable itemsSummary) {
            this.itemsSummary = itemsSummary;
            return this;
        }

        public Builder setReviewOrder(List<String> reviewOrder) {
            this.reviewOrder = reviewOrder;
            return this;
        }

        public Builder addSummaryProductDetail(BigDecimal amount) {
            this.productAmount = amount;
            return this;
        }

        public Builder addSummaryDiscountDetail(BigDecimal amount) {
            this.discountAmount = amount;
            return this;
        }

        public Builder addSummaryChargeDetail(BigDecimal amount) {
            this.chargeAmount = amount;
            return this;
        }

        public Builder addSummaryTaxesDetail(BigDecimal amount) {
            this.taxesAmount = amount;
            return this;
        }

        public Builder addSummaryShippingDetail(BigDecimal amount) {
            this.shippingAmount = amount;
            return this;
        }

        public Builder addSummaryArrearsDetail(BigDecimal amount) {
            this.arrearsAmount = amount;
            return this;
        }

        public Builder setSummaryProductTitle(String productTitle) {
            this.productTitle = productTitle;
            return this;
        }

        public Builder setDisclaimer(String disclaimer) {
            this.disclaimerText = disclaimer;
            return this;
        }

        public Builder setDisclaimerTextColor(String disclaimerTextColor) {
            this.disclaimerTextColor = disclaimerTextColor;
            return this;
        }

        public Builder setQuantityTitle(String quantityTitle) {
            this.quantityTitle = quantityTitle;
            return this;
        }

        public Builder setAmountTitle(String amountTitle) {
            this.amountTitle = amountTitle;
            return this;
        }

        public Builder showQuantityRow() {
            this.showQuantityRow = true;
            return this;
        }

        public Builder hideQuantityRow() {
            this.showQuantityRow = false;
            return this;
        }

        public Builder showAmountTitle() {
            this.showAmountTitle = true;
            return this;
        }

        public Builder hideAmountTitle() {
            this.showAmountTitle = false;
            return this;
        }

        public ReviewScreenPreference build() {
            return new ReviewScreenPreference(this);
        }
    }
}
