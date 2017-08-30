package com.mercadopago.preferences;

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by mromar on 8/22/17.
 */

public class ShoppingReviewPreference {

    private String oneWordDescription;
    private String quantityTitle;
    private String amountTitle;

    private boolean showQuantityRow;
    private boolean showAmountTitle;

    public ShoppingReviewPreference(Builder builder) {
        this.oneWordDescription = builder.oneWordDescription;
        this.quantityTitle = builder.quantityTitle;
        this.amountTitle = builder.amountTitle;
        this.showQuantityRow = builder.showQuantityRow;
        this.showAmountTitle = builder.showAmountTitle;
    }

    public String getOneWordDescription() {
        String oneWordDescription = null;

        if (!isEmpty(this.oneWordDescription)) {
            String[] splittedString = this.oneWordDescription.split(" ");
            oneWordDescription = splittedString[0];
        }

        return oneWordDescription;
    }

    public String getQuantityTitle() {
        return quantityTitle;
    }

    public String getAmountTitle() {
        return amountTitle;
    }

    public boolean shouldShowQuantityRow() {
        return showQuantityRow;
    }

    public boolean shouldShowAmountTitle() {
        return showAmountTitle;
    }

    public static class Builder {
        private String oneWordDescription;
        private String quantityTitle;
        private String amountTitle;

        private boolean showQuantityRow = true;
        private boolean showAmountTitle = true;

        public Builder setOneWordDescription(String oneWordDescription) {
            this.oneWordDescription = oneWordDescription;
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
            showAmountTitle = true;
            return this;
        }

        public Builder hideAmountTitle() {
            this.showAmountTitle = false;
            return this;
        }

        public ShoppingReviewPreference build() {
            return new ShoppingReviewPreference(this);
        }
    }
}
