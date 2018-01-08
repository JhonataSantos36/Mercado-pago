package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.core.CheckoutSessionStore;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;
import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;
import com.mercadopago.preferences.PaymentResultScreenPreference;

/**
 * Created by vaserber on 10/20/17.
 */

public class PaymentResultProps {

    public final PaymentResult paymentResult;
    public final Instruction instruction;
    public final String headerMode;
    public final HeaderTitleFormatter headerAmountFormatter;
    public final BodyAmountFormatter bodyAmountFormatter;
    public final boolean loading;
    public final String processingMode;

    public PaymentResultProps(@NonNull final Builder builder) {
        this.paymentResult = builder.paymentResult;
        this.headerMode = builder.headerMode;
        this.instruction = builder.instruction;
        this.headerAmountFormatter = builder.headerAmountFormatter;
        this.bodyAmountFormatter = builder.bodyAmountFormatter;
        this.loading = builder.loading;
        this.processingMode = builder.processingMode;
    }

    public Builder toBuilder() {
        return new Builder()
                .setPaymentResult(this.paymentResult)
                .setHeaderMode(this.headerMode)
                .setInstruction(this.instruction)
                .setHeaderAmountFormatter(this.headerAmountFormatter)
                .setBodyAmountFormatter(this.bodyAmountFormatter)
                .setLoading(this.loading)
                .setProcessingMode(this.processingMode);
    }

    public boolean hasCustomizedTitle() {
        final PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance()
                .getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedTitleValidState()) {
                return preferences.getApprovedTitle() != null && !preferences.getApprovedTitle().isEmpty();
            } else if (isPendingTitleValidState()) {
                return preferences.getPendingTitle() != null && !preferences.getPendingTitle().isEmpty();
            } else if (isRejectedTitleValidState()) {
                return preferences.getRejectedTitle() != null && !preferences.getRejectedTitle().isEmpty();
            }
        }
        return false;
    }

    public boolean isPluginPaymentResult(@Nullable final PaymentResult paymentResult) {
        return paymentResult != null &&
                (Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(paymentResult.getPaymentStatusDetail())
                        || Payment.StatusCodes.STATUS_DETAIL_APPROVED_PLUGIN_PM.equals(paymentResult.getPaymentStatusDetail()));
    }

    public String getPreferenceTitle() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedTitleValidState()) {
                return preferences.getApprovedTitle();
            } else if (isPendingTitleValidState()) {
                return preferences.getPendingTitle();
            } else if (isRejectedTitleValidState()) {
                return preferences.getRejectedTitle();
            }
        }
        return "";
    }

    private boolean isApprovedTitleValidState() {
        return isStatusApproved();
    }

    private boolean isStatusApproved() {
        return paymentResult != null && paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED);
    }

    private boolean isStatusRejected() {
        return paymentResult != null && paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED);
    }

    private boolean isPendingTitleValidState() {
        return paymentResult != null && ((paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)
                && !paymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) ||
                paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS));
    }

    private boolean isRejectedTitleValidState() {
        return isStatusRejected();
    }

    public boolean hasCustomizedLabel() {
        final PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedLabelValidState()) {
                return preferences.getApprovedLabelText() != null && !preferences.getApprovedLabelText().isEmpty();
            } else if (isRejectedLabelValidState()) {
                return !preferences.isRejectedLabelTextEnabled();
            }
        }
        return false;
    }

    public String getPreferenceLabel() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedLabelValidState()) {
                return preferences.getApprovedLabelText();
            } else if (isRejectedLabelValidState() && !preferences.isRejectedLabelTextEnabled()) {
                return "";
            }
        }
        return "";
    }

    private boolean isApprovedLabelValidState() {
        return isStatusApproved();
    }

    private boolean isRejectedLabelValidState() {
        return isStatusRejected();
    }

    public boolean hasCustomizedBadge() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null && isApprovedBadgeValidState()) {
            return preferences.getApprovedBadge() != null && !preferences.getApprovedBadge().isEmpty();
        }
        return false;
    }

    public String getPreferenceBadge() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null && isApprovedBadgeValidState()) {
            return preferences.getApprovedBadge();
        }
        return "";
    }

    private boolean isApprovedBadgeValidState() {
        return isStatusApproved();
    }

    public boolean hasCustomizedIcon() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedIconValidState()) {
                return preferences.getApprovedIcon() != null;
            } else if (isPendingIconValidState()) {
                return preferences.getPendingIcon() != null;
            } else if (isRejectedIconValidState()) {
                return preferences.getRejectedIcon() != null;
            }
        }
        return false;
    }

    public int getPreferenceIcon() {
        PaymentResultScreenPreference preferences = CheckoutSessionStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedIconValidState()) {
                return preferences.getApprovedIcon();
            } else if (isPendingIconValidState()) {
                return preferences.getPendingIcon();
            } else if (isRejectedIconValidState()) {
                return preferences.getRejectedIcon();
            }
        }
        return 0;
    }

    private boolean isApprovedIconValidState() {
        return isStatusApproved();
    }

    private boolean isPendingIconValidState() {
        return paymentResult != null && (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)
                && paymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
    }

    private boolean isRejectedIconValidState() {
        return isStatusRejected();
    }

    public boolean hasInstructions() {
        return instruction != null;
    }

    public String getInstructionsTitle() {
        if (hasInstructions()) {
            return instruction.getTitle();
        } else {
            return "";
        }
    }

    public static class Builder {

        public PaymentResult paymentResult;
        public Instruction instruction;
        public String headerMode = HeaderProps.HEADER_MODE_WRAP;
        public HeaderTitleFormatter headerAmountFormatter;
        public BodyAmountFormatter bodyAmountFormatter;
        public boolean loading = true;
        public String processingMode;

        public Builder setPaymentResult(@NonNull final PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
            return this;
        }

        public Builder setHeaderMode(@NonNull final String headerMode) {
            this.headerMode = headerMode;
            return this;
        }

        public Builder setInstruction(@NonNull final Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setHeaderAmountFormatter(@NonNull final HeaderTitleFormatter headerAmountFormatter) {
            this.headerAmountFormatter = headerAmountFormatter;
            return this;
        }

        public Builder setBodyAmountFormatter(@NonNull final BodyAmountFormatter bodyAmountFormatter) {
            this.bodyAmountFormatter = bodyAmountFormatter;
            return this;
        }

        public Builder setLoading(final boolean loading) {
            this.loading = loading;
            return this;
        }

        public Builder setProcessingMode(String processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public PaymentResultProps build() {
            return new PaymentResultProps(this);
        }
    }
}
