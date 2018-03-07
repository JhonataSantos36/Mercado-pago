package com.mercadopago.review_and_confirm.models;

import com.mercadopago.components.CustomComponent;

public final class ReviewAndConfirmPreferences {

    private final CustomComponent topComponent;

    private final CustomComponent bottomComponent;

    private ReviewAndConfirmPreferences() {
        this.topComponent = null;
        this.bottomComponent = null;
    }

    private ReviewAndConfirmPreferences(Builder builder) {
        this.topComponent = builder.topView;
        this.bottomComponent = builder.bottomView;
    }

    public boolean hasCustomTopView() {
        return topComponent != null;
    }

    public boolean hasCustomBottomView() {
        return bottomComponent != null;
    }

    public CustomComponent getTopComponent() {
        return topComponent;
    }

    public CustomComponent getBottomComponent() {
        return bottomComponent;
    }

    public static class Builder {
        CustomComponent topView;
        CustomComponent bottomView;

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

        public ReviewAndConfirmPreferences build() {
            return new ReviewAndConfirmPreferences(this);
        }
    }
}
