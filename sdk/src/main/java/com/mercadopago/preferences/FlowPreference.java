package com.mercadopago.preferences;

/**
 * Created by mreverter on 1/17/17.
 */
public class FlowPreference {

    public static final int DEFAULT_MAX_SAVED_CARDS_TO_SHOW = 3;
    public static final String SHOW_ALL_SAVED_CARDS_CODE = "all";

    private boolean paymentSearchScreenEnabled;
    private boolean reviewAndConfirmScreenEnabled;
    private boolean paymentResultScreenEnabled;
    private boolean paymentApprovedScreenEnabled;
    private boolean paymentRejectedScreenEnabled;
    private boolean paymentPendingScreenEnabled;
    private boolean exitOnPaymentMethodChange;
    private boolean bankDealsEnabled;
    private boolean installmentsReviewScreenEnabled;
    private boolean discountEnabled;
    private boolean showAllSavedCardsEnabled;
    private boolean escEnabled;
    private int maxSavedCardsToShow;
    private Integer congratsDisplayTime = null;
    private Integer checkoutTimer;

    private FlowPreference(Builder builder) {
        this.paymentSearchScreenEnabled = builder.paymentSearchScreenEnabled;
        this.reviewAndConfirmScreenEnabled = builder.reviewAndConfirmScreenEnabled;
        this.paymentResultScreenEnabled = builder.paymentResultScreenEnabled;
        this.paymentApprovedScreenEnabled = builder.paymentApprovedScreenEnabled;
        this.paymentRejectedScreenEnabled = builder.paymentRejectedScreenEnabled;
        this.paymentPendingScreenEnabled = builder.paymentPendingScreenEnabled;
        this.bankDealsEnabled = builder.bankDealsEnabled;
        this.installmentsReviewScreenEnabled = builder.installmentsReviewScreenEnabled;
        this.discountEnabled = builder.discountEnabled;
        this.showAllSavedCardsEnabled = builder.showAllSavedCardsEnabled;
        this.escEnabled = builder.escEnabled;
        this.maxSavedCardsToShow = builder.maxSavedCardsToShow;
        this.congratsDisplayTime = builder.congratsDisplayTime;
        this.checkoutTimer = builder.checkoutTimer;
        this.exitOnPaymentMethodChange = builder.exitOnPaymentMethodChange;
    }

    public Integer getCongratsDisplayTime() {
        return this.congratsDisplayTime;
    }

    public Integer getCheckoutTimerInitialTime() {
        return this.checkoutTimer;
    }

    public boolean isPaymentSearchScreenEnabled() {
        return this.paymentSearchScreenEnabled;
    }

    public boolean isReviewAndConfirmScreenEnabled() {
        return this.reviewAndConfirmScreenEnabled;
    }

    public boolean isPaymentResultScreenEnabled() {
        return this.paymentResultScreenEnabled;
    }

    public boolean isPaymentApprovedScreenEnabled() {
        return this.paymentApprovedScreenEnabled;
    }

    public boolean isPaymentRejectedScreenEnabled() {
        return this.paymentRejectedScreenEnabled;
    }

    public boolean isPaymentPendingScreenEnabled() {
        return this.paymentPendingScreenEnabled;
    }

    public boolean isBankDealsEnabled() {
        return this.bankDealsEnabled;
    }

    public boolean isInstallmentsReviewScreenEnabled() {
        return this.installmentsReviewScreenEnabled && !this.reviewAndConfirmScreenEnabled;
    }

    public boolean isDiscountEnabled() {
        return this.discountEnabled;
    }

    public void disableDiscount() {
        this.discountEnabled = false;
    }

    public boolean isCheckoutTimerEnabled() {
        return checkoutTimer != null;
    }

    public int getMaxSavedCardsToShow() {
        return maxSavedCardsToShow;
    }

    public boolean isShowAllSavedCardsEnabled() {
        return showAllSavedCardsEnabled;
    }

    public boolean isESCEnabled() {
        return escEnabled;
    }

    public boolean shouldExitOnPaymentMethodChange() {
        return exitOnPaymentMethodChange;
    }

    public static class Builder {

        private boolean bankDealsEnabled = true;
        private boolean paymentSearchScreenEnabled = false;
        private boolean reviewAndConfirmScreenEnabled = true;
        private boolean paymentResultScreenEnabled = true;
        private boolean paymentApprovedScreenEnabled = true;
        private boolean paymentRejectedScreenEnabled = true;
        private boolean paymentPendingScreenEnabled = true;
        private boolean installmentsReviewScreenEnabled = true;
        private boolean discountEnabled = true;
        private boolean showAllSavedCardsEnabled = false;
        private boolean escEnabled = false;
        private int maxSavedCardsToShow = DEFAULT_MAX_SAVED_CARDS_TO_SHOW;
        private Integer congratsDisplayTime = null;
        private Integer checkoutTimer;
        private boolean exitOnPaymentMethodChange = false;

        public Builder enablePaymentSearchScreen() {
            this.paymentSearchScreenEnabled = true;
            return this;
        }

        public Builder enableESC() {
            this.escEnabled = true;
            return this;
        }

        public Builder disableESC() {
            this.escEnabled = false;
            return this;
        }

        public Builder disableReviewAndConfirmScreen() {
            this.reviewAndConfirmScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentResultScreen() {
            this.paymentResultScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentApprovedScreen() {
            this.paymentApprovedScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentRejectedScreen() {
            this.paymentRejectedScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentPendingScreen() {
            this.paymentPendingScreenEnabled = false;
            return this;
        }

        public Builder disableBankDeals() {
            this.bankDealsEnabled = false;
            return this;
        }

        public Builder disableInstallmentsReviewScreen() {
            this.installmentsReviewScreenEnabled = false;
            return this;
        }

        public Builder disableDiscount() {
            this.discountEnabled = false;
            return this;
        }

        public Builder setCongratsDisplayTime(int seconds) {
            this.congratsDisplayTime = seconds;
            return this;
        }

        public Builder setMaxSavedCardsToShow(int count) {
            if (count > 0) {
                this.maxSavedCardsToShow = count;
            } else {
                this.maxSavedCardsToShow = DEFAULT_MAX_SAVED_CARDS_TO_SHOW;
            }
            return this;
        }

        public Builder setMaxSavedCardsToShow(String count) {
            if (count != null && count.equals(SHOW_ALL_SAVED_CARDS_CODE)) {
                this.showAllSavedCardsEnabled = true;
            } else {
                this.showAllSavedCardsEnabled = false;
                this.maxSavedCardsToShow = DEFAULT_MAX_SAVED_CARDS_TO_SHOW;
            }
            return this;
        }

        public Builder setCheckoutTimer(Integer seconds) {
            this.checkoutTimer = seconds;
            return this;
        }

        public Builder exitOnPaymentMethodChange() {
            this.exitOnPaymentMethodChange = true;
            return this;
        }

        public FlowPreference build() {
            return new FlowPreference(this);
        }
    }
}
