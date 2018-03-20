package com.mercadopago.preferences;

/**
 * Created by mreverter on 1/17/17.
 */
public class FlowPreference {

    public static final int DEFAULT_MAX_SAVED_CARDS_TO_SHOW = 3;
    public static final String SHOW_ALL_SAVED_CARDS_CODE = "all";

    private final boolean paymentSearchScreenEnabled;
    private final boolean reviewAndConfirmScreenEnabled;
    private final boolean paymentResultScreenEnabled;
    private final boolean paymentApprovedScreenEnabled;
    private final boolean paymentRejectedScreenEnabled;
    private final boolean paymentPendingScreenEnabled;
    private final boolean exitOnPaymentMethodChange;
    private final boolean bankDealsEnabled;
    private final boolean installmentsReviewScreenEnabled;
    private boolean discountEnabled;
    private final boolean showAllSavedCardsEnabled;
    private final boolean escEnabled;
    private final int maxSavedCardsToShow;
    private Integer congratsDisplayTime = null;
    private final Integer checkoutTimer;

    private FlowPreference(Builder builder) {
        paymentSearchScreenEnabled = builder.paymentSearchScreenEnabled;
        reviewAndConfirmScreenEnabled = builder.reviewAndConfirmScreenEnabled;
        paymentResultScreenEnabled = builder.paymentResultScreenEnabled;
        paymentApprovedScreenEnabled = builder.paymentApprovedScreenEnabled;
        paymentRejectedScreenEnabled = builder.paymentRejectedScreenEnabled;
        paymentPendingScreenEnabled = builder.paymentPendingScreenEnabled;
        bankDealsEnabled = builder.bankDealsEnabled;
        installmentsReviewScreenEnabled = builder.installmentsReviewScreenEnabled;
        discountEnabled = builder.discountEnabled;
        showAllSavedCardsEnabled = builder.showAllSavedCardsEnabled;
        escEnabled = builder.escEnabled;
        maxSavedCardsToShow = builder.maxSavedCardsToShow;
        congratsDisplayTime = builder.congratsDisplayTime;
        checkoutTimer = builder.checkoutTimer;
        exitOnPaymentMethodChange = builder.exitOnPaymentMethodChange;
    }

    public Integer getCongratsDisplayTime() {
        return congratsDisplayTime;
    }

    public Integer getCheckoutTimerInitialTime() {
        return checkoutTimer;
    }

    public boolean isPaymentSearchScreenEnabled() {
        return paymentSearchScreenEnabled;
    }

    public boolean isReviewAndConfirmScreenEnabled() {
        return reviewAndConfirmScreenEnabled;
    }

    public boolean isPaymentResultScreenEnabled() {
        return paymentResultScreenEnabled;
    }

    public boolean isPaymentApprovedScreenEnabled() {
        return paymentApprovedScreenEnabled;
    }

    public boolean isPaymentRejectedScreenEnabled() {
        return paymentRejectedScreenEnabled;
    }

    public boolean isPaymentPendingScreenEnabled() {
        return paymentPendingScreenEnabled;
    }

    public boolean isBankDealsEnabled() {
        return bankDealsEnabled;
    }

    public boolean isInstallmentsReviewScreenEnabled() {
        return installmentsReviewScreenEnabled && !reviewAndConfirmScreenEnabled;
    }

    public boolean isDiscountEnabled() {
        return discountEnabled;
    }

    public void disableDiscount() {
        discountEnabled = false;
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
            paymentSearchScreenEnabled = true;
            return this;
        }

        public Builder enableESC() {
            escEnabled = true;
            return this;
        }

        public Builder disableESC() {
            escEnabled = false;
            return this;
        }

        public Builder disableReviewAndConfirmScreen() {
            reviewAndConfirmScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentResultScreen() {
            paymentResultScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentApprovedScreen() {
            paymentApprovedScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentRejectedScreen() {
            paymentRejectedScreenEnabled = false;
            return this;
        }

        public Builder disablePaymentPendingScreen() {
            paymentPendingScreenEnabled = false;
            return this;
        }

        public Builder disableBankDeals() {
            bankDealsEnabled = false;
            return this;
        }

        public Builder disableInstallmentsReviewScreen() {
            installmentsReviewScreenEnabled = false;
            return this;
        }

        public Builder disableDiscount() {
            discountEnabled = false;
            return this;
        }

        public Builder setCongratsDisplayTime(int seconds) {
            congratsDisplayTime = seconds;
            return this;
        }

        public Builder setMaxSavedCardsToShow(int count) {
            if (count > 0) {
                maxSavedCardsToShow = count;
            } else {
                maxSavedCardsToShow = DEFAULT_MAX_SAVED_CARDS_TO_SHOW;
            }
            return this;
        }

        public Builder setMaxSavedCardsToShow(String count) {
            if (SHOW_ALL_SAVED_CARDS_CODE.equals(count)) {
                showAllSavedCardsEnabled = true;
            } else {
                showAllSavedCardsEnabled = false;
                maxSavedCardsToShow = DEFAULT_MAX_SAVED_CARDS_TO_SHOW;
            }
            return this;
        }

        public Builder setCheckoutTimer(Integer seconds) {
            checkoutTimer = seconds;
            return this;
        }

        public Builder exitOnPaymentMethodChange() {
            exitOnPaymentMethodChange = true;
            return this;
        }

        public FlowPreference build() {
            return new FlowPreference(this);
        }
    }
}
