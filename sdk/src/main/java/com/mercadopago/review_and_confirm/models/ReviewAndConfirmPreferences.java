package com.mercadopago.review_and_confirm.models;

import android.support.annotation.DrawableRes;

import com.mercadopago.components.CustomComponent;

public final class ReviewAndConfirmPreferences {

    private final CustomComponent topComponent;

    private final CustomComponent bottomComponent;

    private final boolean itemsEnabled;

    private final @DrawableRes
    Integer collectorIcon;

    private final String quantityLabel;

    private final String unitPriceLabel;

    private ReviewAndConfirmPreferences(Builder builder) {
        this.topComponent = builder.topView;
        this.bottomComponent = builder.bottomView;
        this.itemsEnabled = builder.itemsEnabled;
        this.collectorIcon = builder.collectorIcon;
        this.quantityLabel = builder.quantityLabel;
        this.unitPriceLabel = builder.unitPriceLabel;
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

    public static class Builder {
        CustomComponent topView;
        CustomComponent bottomView;
        boolean itemsEnabled = true;
        Integer collectorIcon;
        String quantityLabel;
        String unitPriceLabel;

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
