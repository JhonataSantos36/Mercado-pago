package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.util.TextUtils;

public class PaymentResultProps {

    public final PaymentResult paymentResult;
    public final Instruction instruction;
    public final String headerMode;
    public final boolean loading;
    public final String processingMode;
    public final HeaderTitleFormatter headerFormatter;

    public PaymentResultProps(@NonNull final Builder builder) {
        paymentResult = builder.paymentResult;
        headerMode = builder.headerMode;
        instruction = builder.instruction;
        loading = builder.loading;
        processingMode = builder.processingMode;
        headerFormatter = builder.headerFormatter;
    }

    public Builder toBuilder() {
        return new Builder()
                .setPaymentResult(paymentResult)
                .setHeaderMode(headerMode)
                .setInstruction(instruction)
                .setLoading(loading)
                .setHeaderFormatter(headerFormatter)
                .setProcessingMode(processingMode);
    }

    public boolean hasCustomizedTitle() {
        final PaymentResultScreenPreference preferences = CheckoutStore.getInstance()
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
                (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(paymentResult.getPaymentStatusDetail())
                        || Payment.StatusDetail.STATUS_DETAIL_APPROVED_PLUGIN_PM.equals(paymentResult.getPaymentStatusDetail()));
    }

    public String getPreferenceTitle() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
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
                && !paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) ||
                paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS));
    }

    private boolean isRejectedTitleValidState() {
        return isStatusRejected();
    }

    public boolean hasCustomizedLabel() {
        final PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
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
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
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
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null && isApprovedBadgeValidState()) {
            return preferences.getApprovedBadge() != null && !preferences.getApprovedBadge().isEmpty();
        }
        return false;
    }

    public String getPreferenceBadge() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null && isApprovedBadgeValidState()) {
            return preferences.getApprovedBadge();
        }
        return "";
    }

    private boolean isApprovedBadgeValidState() {
        return isStatusApproved();
    }

    public boolean hasCustomizedImageIcon() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
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

    public boolean hasCustomizedUrlIcon() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedIconValidState()) {
                return !TextUtils.isEmpty(preferences.getApprovedUrlIcon());
            } else if (isPendingIconValidState()) {
                return !TextUtils.isEmpty(preferences.getPendingUrlIcon());
            } else if (isRejectedIconValidState()) {
                return !TextUtils.isEmpty(preferences.getRejectedUrlIcon());
            }
        }
        return false;
    }

    public int getPreferenceIcon() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
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

    public String getPreferenceUrlIcon() {
        PaymentResultScreenPreference preferences = CheckoutStore.getInstance().getPaymentResultScreenPreference();
        if (preferences != null) {
            if (isApprovedIconValidState()) {
                return preferences.getApprovedUrlIcon();
            } else if (isPendingIconValidState()) {
                return preferences.getPendingUrlIcon();
            } else if (isRejectedIconValidState()) {
                return preferences.getRejectedUrlIcon();
            }
        }
        return null;
    }

    private boolean isApprovedIconValidState() {
        return isStatusApproved();
    }

    private boolean isPendingIconValidState() {
        return paymentResult != null && (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)
                && paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
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
        public boolean loading = true;
        public String processingMode;
        private HeaderTitleFormatter headerFormatter;

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

        public Builder setHeaderFormatter(final HeaderTitleFormatter headerFormatter) {
            this.headerFormatter = headerFormatter;
            return this;
        }
    }
}
