package com.mercadopago.review_and_confirm.models;

import android.support.annotation.DrawableRes;

import com.mercadopago.components.CustomComponent;

import java.math.BigDecimal;

public final class ReviewAndConfirmPreferences {

    private final CustomComponent topComponent;

    private final CustomComponent bottomComponent;

    private final boolean itemsEnabled;

    private final @DrawableRes
    Integer collectorIcon;

    private final String quantityLabel;

    private final String unitPriceLabel;

    public final BigDecimal productAmount;
    public final BigDecimal shippingAmount;
    public final BigDecimal arrearsAmount;
    public final BigDecimal taxesAmount;
    public final String disclaimerText;
    public final BigDecimal chargeAmount;
    public final BigDecimal discountAmount;
    public final BigDecimal totalAmount;

    public final String productTitle;
    public final String disclaimerTextColor;

    private ReviewAndConfirmPreferences(Builder builder) {
        this.topComponent = builder.topView;
        this.bottomComponent = builder.bottomView;
        this.itemsEnabled = builder.itemsEnabled;
        this.collectorIcon = builder.collectorIcon;
        this.quantityLabel = builder.quantityLabel;
        this.unitPriceLabel = builder.unitPriceLabel;
        this.productAmount = builder.productAmount;
        this.shippingAmount = builder.shippingAmount;
        this.arrearsAmount = builder.arrearsAmount;
        this.taxesAmount = builder.taxesAmount;
        this.disclaimerText = builder.disclaimerText;
        this.chargeAmount = builder.chargeAmount;
        this.discountAmount = builder.discountAmount;
        this.productTitle = builder.productTitle;
        this.disclaimerTextColor = builder.disclaimerTextColor;

        this.totalAmount = calculateTotalAmount();
    }

    public boolean hasProductAmount() {
        return productAmount != null && productAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasCustomTopView() {
        return topComponent != null;
    }

    public boolean hasCustomBottomView() {
        return bottomComponent != null;
    }

    public boolean hasItemsEnabled() {
        return itemsEnabled;
    }

    public CustomComponent getTopComponent() {
        return topComponent;
    }

    public CustomComponent getBottomComponent() {
        return bottomComponent;
    }

    public Integer getCollectorIcon() {
        return collectorIcon;
    }

    public String getQuantityLabel() {
        return quantityLabel;
    }

    public String getUnitPriceLabel() {
        return unitPriceLabel;
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);

        totalAmount = (productAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(productAmount);
        totalAmount = (chargeAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(chargeAmount);
        totalAmount = (taxesAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(taxesAmount);
        totalAmount = (shippingAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(shippingAmount);
        totalAmount = (arrearsAmount == null) ? totalAmount.add(new BigDecimal(0)) : totalAmount.add(arrearsAmount);
        totalAmount = (discountAmount == null) ? totalAmount.subtract(new BigDecimal(0)) : totalAmount.subtract(discountAmount);

        return totalAmount;
    }

    public static class Builder {
        CustomComponent topView;
        CustomComponent bottomView;
        boolean itemsEnabled = true;
        Integer collectorIcon;
        String quantityLabel;
        String unitPriceLabel;

        BigDecimal productAmount;
        BigDecimal shippingAmount;
        BigDecimal arrearsAmount;
        BigDecimal taxesAmount;
        String disclaimerText;
        BigDecimal chargeAmount;
        BigDecimal discountAmount;
        BigDecimal totalAmount;
        String productTitle;
        String disclaimerTextColor;

        /**
         * Set product title that will appear in the top of review and confirm summary.
         *
         * @param productTitle the product title
         * @return builder
         */
        public Builder setProductTitle(String productTitle) {
            this.productTitle = productTitle;
            return this;
        }

        /**
         * Set product amount that will appear in the top of review and confirm summary.
         *
         * @param productAmount the product amount
         * @return builder
         */
        public Builder setProductAmount(BigDecimal productAmount) {
            this.productAmount = productAmount;
            return this;
        }

        /**
         * Set shipping amount that will appear in the top of review and confirm summary.
         *
         * @param shippingAmount the shipping amount
         * @return builder
         */
        public Builder setShippingAmount(BigDecimal shippingAmount) {
            this.shippingAmount = shippingAmount;
            return this;
        }

        /**
         * Set arrears amount that will appear in the top of review and confirm summary.
         *
         * @param arrearsAmount the arrears amount
         * @return builder
         */
        public Builder setArrearsAmount(BigDecimal arrearsAmount) {
            this.arrearsAmount = arrearsAmount;
            return this;
        }

        /**
         * Set taxes amount that will appear in the top of review and confirm summary.
         *
         * @param taxesAmount the taxes amount
         * @return builder
         */
        public Builder setTaxesAmount(BigDecimal taxesAmount) {
            this.taxesAmount = taxesAmount;
            return this;
        }

        /**
         * Set charges amount that will appear in the top of review and confirm summary.
         *
         * @param chargeAmount the charges amount
         * @return builder
         */
        public Builder setChargeAmount(BigDecimal chargeAmount) {
            this.chargeAmount = chargeAmount;
            return this;
        }

        /**
         * Set discount amount that will appear in the top of review and confirm summary.
         *
         * @param discountAmount the discount amount
         * @return builder
         */
        public Builder setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        /**
         * Set disclaimer text that will appear in the bottom of review and confirm summary.
         *
         * @param disclaimerText the disclaimer text
         * @return builder
         */
        public Builder setDisclaimerText(String disclaimerText) {
            this.disclaimerText = disclaimerText;
            return this;
        }

        /**
         * Set disclaimer text color.
         *
         * @param disclaimerTextColor the disclaimer text color in hex with hashtag
         * @return builder
         */
        public Builder setDisclaimerTextColor(String disclaimerTextColor) {
            this.disclaimerTextColor = disclaimerTextColor;
            return this;
        }

        /**
         * Custom view that will appear before payment method description
         * inside Review and confirm screen
         *
         * @param topComponent the top component that renders the top view
         * @return builder
         */
        public Builder setTopComponent(final CustomComponent topComponent) {
            this.topView = topComponent;
            return this;
        }

        /**
         * Custom view that will appear after payment method description
         * inside Review and confirm screen
         *
         * @param bottomComponent the top bottom component that renders bottom view
         * @return builder
         */
        public Builder setBottomComponent(final CustomComponent bottomComponent) {
            this.bottomView = bottomComponent;
            return this;
        }

        /**
         * Hide items view in Review and confirm screen
         *
         * @return builder
         */
        public Builder disableItems() {
            this.itemsEnabled = false;
            return this;
        }

        /**
         * Set custom icon that will appear in items view.
         * It appears only if the item doesn't have a picture url in it
         *
         * @param collectorIcon drawable that will be shown in items view
         * @return builder
         */
        public Builder setCollectorIcon(@DrawableRes int collectorIcon) {
            this.collectorIcon = collectorIcon;
            return this;
        }

        /**
         * Set a custom text that displays in the quantity label of the items view.
         * It appears only if the item's quantity is greater than 1
         *
         * @param quantityLabel the text that will be shown in the quantity label
         * @return
         */
        public Builder setQuantityLabel(final String quantityLabel) {
            this.quantityLabel = quantityLabel;
            return this;
        }

        /**
         * Set a custom text that displays in the unit price label of the items view.
         * It appears only if there are multiple items to show, or
         * if the item's quantity is greater than 1
         *
         * @param unitPriceLabel the text that will be shown in the unit price label
         * @return
         */
        public Builder setUnitPriceLabel(final String unitPriceLabel) {
            this.unitPriceLabel = unitPriceLabel;
            return this;
        }

        public ReviewAndConfirmPreferences build() {
            return new ReviewAndConfirmPreferences(this);
        }
    }
}
