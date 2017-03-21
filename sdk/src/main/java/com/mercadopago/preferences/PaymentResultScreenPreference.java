package com.mercadopago.preferences;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentResultCallback;
import com.mercadopago.model.Reviewable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 2/13/17.
 */

public class PaymentResultScreenPreference {

    private Integer titleBackgroundColor;
    private String approvedTitle;
    private String approvedSubtitle;
    private String pendingTitle;
    private String pendingSubtitle;
    private String pendingContentTitle;
    private String pendingContentText;
    private Integer pendingIconName;
    private String exitButtonTitle;
    private String secondaryPendingExitButtonTitle;
    private String secondaryCongratsExitButtonTitle;
    private String secondaryRejectedExitButtonTitle;
    private String rejectedTitle;
    private String rejectedSubtitle;
    private Integer rejectedIconName;
    private String rejectedIconSubtext;
    private String rejectedContentTitle;
    private String rejectedContentText;
    private boolean enableCongratsSecondaryExitButton = true;
    private boolean enablePendingSecondaryExitButton = true;
    private boolean enableRejectedSecondaryExitButton = true;
    private boolean enablePendingContentText = true;
    private boolean enablePendingContentTitle = true;
    private boolean enableRejectedContentText = true;
    private boolean enableRejectedContentTitle = true;
    private boolean enableRejectedIconSubtext = true;
    private boolean enableApprovedReceipt = true;
    private boolean enableApprovedAmount = true;
    private boolean enableApprovedPaymentMethodInfo = true;
    private transient List<Reviewable> congratsReviewables;
    private transient List<Reviewable> pendingReviewables;

    private Integer secondaryRejectedExitResultCode;
    private Integer secondaryCongratsExitResultCode;
    private Integer secondaryPendingExitResultCode;

    private PaymentResultScreenPreference(Builder builder) {
        this.titleBackgroundColor = builder.titleBackgroundColor;
        this.approvedTitle = builder.approvedTitle;
        this.approvedSubtitle = builder.approvedSubtitle;
        this.pendingTitle = builder.pendingTitle;
        this.pendingSubtitle = builder.pendingSubtitle;
        this.pendingContentTitle = builder.pendingContentTitle;
        this.pendingContentText = builder.pendingContentText;
        this.pendingIconName = builder.pendingIcon;
        this.exitButtonTitle = builder.exitButtonTitle;
        this.secondaryPendingExitButtonTitle = builder.secondaryPendingExitButtonTitle;
        this.secondaryPendingExitResultCode = builder.secondaryPendingExitResultCode;
        this.secondaryCongratsExitButtonTitle = builder.secondaryCongratsExitButtonTitle;
        this.secondaryCongratsExitResultCode = builder.secondaryCongratsExitResultCode;
        this.secondaryRejectedExitButtonTitle = builder.secondaryRejectedExitButtonTitle;
        this.secondaryRejectedExitResultCode = builder.secondaryRejectedExitResultCode;
        this.rejectedTitle = builder.rejectedTitle;
        this.rejectedSubtitle = builder.rejectedSubtitle;
        this.rejectedIconName = builder.rejectedIcon;
        this.rejectedIconSubtext = builder.rejectedIconSubtext;
        this.rejectedContentTitle = builder.rejectedContentTitle;
        this.rejectedContentText = builder.rejectedContentText;
        this.rejectedContentTitle = builder.rejectedContentTitle;
        this.enableCongratsSecondaryExitButton = builder.enableCongratsSecondaryExitButton;
        this.enablePendingSecondaryExitButton = builder.enablePendingSecondaryExitButton;
        this.enableRejectedSecondaryExitButton = builder.enableRejectedSecondaryExitButton;
        this.enablePendingContentText = builder.enablePendingContentText;
        this.enablePendingContentTitle = builder.enablePendingContentTitle;
        this.enableRejectedContentText = builder.enableRejectedContentText;
        this.enableRejectedContentTitle = builder.enableRejectedContentTitle;
        this.enableRejectedIconSubtext = builder.enableRejectedIconSubtext;
        this.enableApprovedReceipt = builder.enableApprovedReceipt;
        this.enableApprovedAmount = builder.enableApprovedAmount;
        this.enableApprovedPaymentMethodInfo = builder.enableApprovedPaymentMethodInfo;
        this.congratsReviewables = builder.congratsReviewables;
        this.pendingReviewables = builder.pendingReviewables;
    }

    public boolean hasCustomCongratsReviewables() {
        return congratsReviewables != null && !congratsReviewables.isEmpty();
    }

    public boolean hasCustomPendingReviewables() {
        return pendingReviewables != null && !pendingReviewables.isEmpty();
    }

    public List<Reviewable> getCongratsReviewables() {
        return congratsReviewables;
    }

    public List<Reviewable> getPendingReviewables() {
        return pendingReviewables;
    }

    public String getApprovedTitle() {
        return this.approvedTitle;
    }

    public String getApprovedSubtitle() {
        return this.approvedSubtitle;
    }

    public String getPendingTitle() {
        return this.pendingTitle;
    }

    public String getPendingSubtitle() {
        return this.pendingSubtitle;
    }

    public String getExitButtonTitle() {
        return this.exitButtonTitle;
    }

    public String getPendingContentTitle() {
        return this.pendingContentTitle;
    }

    public String getPendingContentText() {
        return this.pendingContentText;
    }

    public String getSecondaryPendingExitButtonTitle() {
        return this.secondaryPendingExitButtonTitle;
    }

    public String getSecondaryCongratsExitButtonTitle() {
        return this.secondaryCongratsExitButtonTitle;
    }

    public Integer getSecondaryCongratsExitResultCode() {
        return secondaryCongratsExitResultCode;
    }

    public String getSecondaryRejectedExitButtonTitle() {
        return this.secondaryRejectedExitButtonTitle;
    }

    public String getRejectedTitle() {
        return this.rejectedTitle;
    }

    public String getRejectedSubtitle() {
        return this.rejectedSubtitle;
    }

    public String getRejectedContentTitle() {
        return this.rejectedContentTitle;
    }

    public String getRejectedContentText() {
        return rejectedContentText;
    }

    public Integer getRejectedIconName() {
        return rejectedIconName;
    }

    public String getRejectedIconSubtext() {
        return rejectedIconSubtext;
    }

    public Integer getPendingIconName() {
        return pendingIconName;
    }

    public boolean isApprovedReceiptEnabled() {
        return this.enableApprovedReceipt;
    }

    public boolean isApprovedAmountEnabled() {
        return this.enableApprovedAmount;
    }

    public boolean isApprovedPaymentMethodInfoEnabled() {
        return this.enableApprovedPaymentMethodInfo;
    }

    public boolean isCongratsSecondaryExitButtonEnabled() {
        return this.enableCongratsSecondaryExitButton;
    }

    public boolean isPendingSecondaryExitButtonEnabled() {
        return this.enablePendingSecondaryExitButton;
    }

    public boolean isRejectedSecondaryExitButtonEnabled() {
        return this.enableRejectedSecondaryExitButton;
    }

    public boolean isPendingContentTextEnabled() {
        return this.enablePendingContentText;
    }

    public boolean isPendingContentTitleEnabled() {
        return this.enablePendingContentTitle;
    }

    public boolean isRejectedContentTextEnabled() {
        return this.enableRejectedContentText;
    }

    public boolean isRejectedContentTitleEnabled() {
        return this.enableRejectedContentTitle;
    }

    public boolean isRejectedIconSubtextEnabled() {
        return this.enableRejectedIconSubtext;
    }

    public void addCongratsReviewable(@NonNull Reviewable reviewable) {
        if (this.congratsReviewables == null) {
            this.congratsReviewables = new ArrayList<>();
        }
        this.congratsReviewables.add(reviewable);
    }

    public void addPendingReviewable(@NonNull Reviewable reviewable) {
        if (this.pendingReviewables == null) {
            this.pendingReviewables = new ArrayList<>();
        }
        this.pendingReviewables.add(reviewable);
    }

    public Integer getSecondaryRejectedExitResultCode() {
        return secondaryRejectedExitResultCode;
    }

    public Integer getSecondaryPendingExitResultCode() {
        return secondaryPendingExitResultCode;
    }

    public boolean hasTitleBackgroundColor() {
        return titleBackgroundColor != null;
    }

    public Integer getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public static class Builder {

        private Integer titleBackgroundColor;
        private String approvedTitle;
        private String approvedSubtitle;
        private String pendingTitle;
        private String pendingSubtitle;
        private String pendingContentTitle;
        private String pendingContentText;
        private Integer pendingIcon;
        private String exitButtonTitle;
        private String secondaryPendingExitButtonTitle;
        private String secondaryCongratsExitButtonTitle;
        private String secondaryRejectedExitButtonTitle;
        private String rejectedTitle;
        private String rejectedSubtitle;
        private Integer rejectedIcon;
        private String rejectedIconSubtext;
        private String rejectedContentTitle;
        private String rejectedContentText;
        private boolean enablePendingContentText = true;
        private boolean enablePendingContentTitle = true;
        private boolean enableRejectedContentText = true;
        private boolean enableRejectedContentTitle = true;
        private boolean enableRejectedIconSubtext = true;
        private boolean enableCongratsSecondaryExitButton = true;
        private boolean enablePendingSecondaryExitButton = true;
        private boolean enableRejectedSecondaryExitButton = true;
        private boolean enableApprovedReceipt = true;
        private boolean enableApprovedAmount = true;
        private boolean enableApprovedPaymentMethodInfo = true;
        private List<Reviewable> congratsReviewables;
        private List<Reviewable> pendingReviewables;

        private Integer secondaryCongratsExitResultCode;
        private Integer secondaryPendingExitResultCode;
        private Integer secondaryRejectedExitResultCode;

        public Builder() {
            this.congratsReviewables = new ArrayList<>();
            this.pendingReviewables = new ArrayList<>();
        }

        public Builder setApprovedTitle(String title) {
            this.approvedTitle = title;
            return this;
        }

        public Builder setApprovedSubtitle(String subtitle) {
            this.approvedSubtitle = subtitle;
            return this;
        }

        public Builder setPendingTitle(String title) {
            this.pendingTitle = title;
            return this;
        }

        public Builder setPendingSubtitle(String subtitle) {
            this.pendingSubtitle = subtitle;
            return this;
        }

        public Builder setPendingContentTitle(String title) {
            this.pendingContentTitle = title;
            return this;
        }

        public Builder setPendingContentText(String text) {
            this.pendingContentText = text;
            return this;
        }

        public Builder setExitButtonTitle(String title) {
            this.exitButtonTitle = title;
            return this;
        }

        @Deprecated
        public Builder setApprovedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            this.secondaryCongratsExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.CONGRATS_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        public Builder setApprovedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            this.secondaryCongratsExitButtonTitle = title;
            this.secondaryCongratsExitResultCode = resultCode;
            return this;
        }

        @Deprecated
        public Builder setPendingSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            this.secondaryPendingExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        public Builder setPendingSecondaryExitButton(String title, @NonNull Integer resultCode) {
            this.secondaryPendingExitButtonTitle = title;
            this.secondaryPendingExitResultCode = resultCode;
            return this;
        }

        @Deprecated
        public Builder setRejectedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            this.secondaryRejectedExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        public Builder setRejectedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            this.secondaryRejectedExitButtonTitle = title;
            this.secondaryRejectedExitResultCode = resultCode;
            return this;
        }

        public Builder disableApprovedSecondaryExitButton() {
            this.enableCongratsSecondaryExitButton = false;
            return this;
        }

        public Builder disablePendingSecondaryExitButton() {
            this.enablePendingSecondaryExitButton = false;
            return this;
        }

        public Builder disableRejectedSecondaryExitButton() {
            this.enableRejectedSecondaryExitButton = false;
            return this;
        }

        public Builder disablePendingContentText() {
            this.enablePendingContentText = false;
            return this;
        }

        public Builder disablePendingContentTitle() {
            this.enablePendingContentTitle = false;
            return this;
        }

        public Builder disableRejectedContentText() {
            this.enableRejectedContentText = false;
            return this;
        }

        public Builder disableRejectedContentTitle() {
            this.enableRejectedContentTitle = false;
            return this;
        }

        public Builder disableRejectedIconSubtext() {
            this.enableRejectedIconSubtext = false;
            return this;
        }

        public Builder setPendingHeaderIcon(@DrawableRes Integer headerIcon) {
            this.pendingIcon = headerIcon;
            return this;
        }

        public Builder addCongratsReviewable(Reviewable customReviewable) {
            this.congratsReviewables.add(customReviewable);
            return this;
        }

        public Builder addPendingReviewable(Reviewable customReviewable) {
            this.pendingReviewables.add(customReviewable);
            return this;
        }

        public Builder disableApprovedReceipt() {
            this.enableApprovedReceipt = false;
            return this;
        }

        public Builder disableApprovedAmount() {
            this.enableApprovedAmount = false;
            return this;
        }

        public Builder disableApprovedPaymentMethodInfo() {
            this.enableApprovedPaymentMethodInfo = false;
            return this;
        }

        public Builder setRejectedTitle(String title) {
            this.rejectedTitle = title;
            return this;
        }

        public Builder setRejectedSubtitle(String subtitle) {
            this.rejectedSubtitle = subtitle;
            return this;
        }

        public Builder setRejectedHeaderIcon(@DrawableRes Integer headerIcon) {
            this.rejectedIcon = headerIcon;
            return this;
        }

        public Builder setRejectedContentTitle(String title) {
            this.rejectedContentTitle = title;
            return this;
        }

        public Builder setRejectedContentText(String text) {
            this.rejectedContentText = text;
            return this;
        }

        public Builder setRejectedIconSubtext(String text) {
            this.rejectedIconSubtext = text;
            return this;
        }

        public Builder setTitleBackgroundColor(@ColorInt Integer titleBackgroundColor) {
            this.titleBackgroundColor = titleBackgroundColor;
            return this;
        }

        public PaymentResultScreenPreference build() {
            return new PaymentResultScreenPreference(this);
        }
    }
}
