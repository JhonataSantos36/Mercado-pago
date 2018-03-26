package com.mercadopago.preferences;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentResultCallback;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.paymentresult.model.Badge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaserber on 2/13/17.
 */

public class PaymentResultScreenPreference {

    private final Integer titleBackgroundColor;
    private final String approvedTitle;
    private final String approvedSubtitle;
    private final Integer approvedIcon;
    private final String approvedUrlIcon;
    private final String approvedLabelText;
    @Badge.ApprovedBadges private final
    String approvedBadge;
    private final String pendingTitle;
    private final String pendingSubtitle;
    private final String pendingContentTitle;
    private final String pendingContentText;
    private final Integer pendingIcon;
    private final String pendingUrlIcon;
    private final String exitButtonTitle;
    private final String secondaryPendingExitButtonTitle;
    private final String secondaryCongratsExitButtonTitle;
    private final String secondaryRejectedExitButtonTitle;
    private final String rejectedTitle;
    private final String rejectedSubtitle;
    private final Integer rejectedIcon;
    private final String rejectedUrlIcon;
    private final String rejectedIconSubtext;
    private String rejectedContentTitle;
    private final String rejectedContentText;
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
    private boolean enableRejectedLabelText = true;

    private final Integer secondaryRejectedExitResultCode;
    private final Integer secondaryCongratsExitResultCode;
    private final Integer secondaryPendingExitResultCode;
    private final Boolean rejectionRetryEnabled;

    private final Map<String, CustomComponentFactory> approvedCustomComponents;

    private PaymentResultScreenPreference(Builder builder) {
        titleBackgroundColor = builder.titleBackgroundColor;
        approvedTitle = builder.approvedTitle;
        approvedSubtitle = builder.approvedSubtitle;
        approvedIcon = builder.approvedIcon;
        approvedUrlIcon = builder.approvedUrlIcon;
        approvedLabelText = builder.approvedLabelText;
        approvedBadge = builder.approvedBadge;
        pendingTitle = builder.pendingTitle;
        pendingSubtitle = builder.pendingSubtitle;
        pendingContentTitle = builder.pendingContentTitle;
        pendingContentText = builder.pendingContentText;
        pendingIcon = builder.pendingIcon;
        pendingUrlIcon = builder.pendingUrlIcon;
        exitButtonTitle = builder.exitButtonTitle;
        secondaryPendingExitButtonTitle = builder.secondaryPendingExitButtonTitle;
        secondaryPendingExitResultCode = builder.secondaryPendingExitResultCode;
        secondaryCongratsExitButtonTitle = builder.secondaryCongratsExitButtonTitle;
        secondaryCongratsExitResultCode = builder.secondaryCongratsExitResultCode;
        secondaryRejectedExitButtonTitle = builder.secondaryRejectedExitButtonTitle;
        secondaryRejectedExitResultCode = builder.secondaryRejectedExitResultCode;
        rejectedTitle = builder.rejectedTitle;
        rejectedSubtitle = builder.rejectedSubtitle;
        rejectedIcon = builder.rejectedIcon;
        rejectedUrlIcon = builder.rejectedUrlIcon;
        rejectedIconSubtext = builder.rejectedIconSubtext;
        rejectedContentTitle = builder.rejectedContentTitle;
        rejectedContentText = builder.rejectedContentText;
        rejectedContentTitle = builder.rejectedContentTitle;
        rejectionRetryEnabled = builder.rejectionRetryEnabled;

        enableCongratsSecondaryExitButton = builder.enableCongratsSecondaryExitButton;
        enablePendingSecondaryExitButton = builder.enablePendingSecondaryExitButton;
        enableRejectedSecondaryExitButton = builder.enableRejectedSecondaryExitButton;
        enablePendingContentText = builder.enablePendingContentText;
        enablePendingContentTitle = builder.enablePendingContentTitle;
        enableRejectedContentText = builder.enableRejectedContentText;
        enableRejectedContentTitle = builder.enableRejectedContentTitle;
        enableRejectedIconSubtext = builder.enableRejectedIconSubtext;
        enableApprovedReceipt = builder.enableApprovedReceipt;
        enableApprovedAmount = builder.enableApprovedAmount;
        enableApprovedPaymentMethodInfo = builder.enableApprovedPaymentMethodInfo;
        enableRejectedLabelText = builder.enableRejectedLabelText;
        approvedCustomComponents = builder.approvedCustomComponents;
    }

    public boolean hasApprovedTopCustomComponent() {
        return approvedCustomComponents.containsKey(CustomComponentFactory.POSIION_TOP);
    }

    public boolean hasApprovedBottomCustomComponent() {
        return approvedCustomComponents.containsKey(CustomComponentFactory.POSIION_BOTTOM);
    }

    public CustomComponentFactory getApprovedTopCustomComponentFactory() {
        return approvedCustomComponents.get(CustomComponentFactory.POSIION_TOP);
    }

    public CustomComponentFactory getApprovedBottomCustomComponentFactory() {
        return approvedCustomComponents.get(CustomComponentFactory.POSIION_BOTTOM);
    }

    public String getApprovedTitle() {
        return approvedTitle;
    }

    public String getApprovedSubtitle() {
        return approvedSubtitle;
    }

    public Integer getApprovedIcon() {
        return approvedIcon;
    }

    public String getApprovedUrlIcon() {
        return approvedUrlIcon;
    }

    public String getApprovedLabelText() {
        return approvedLabelText;
    }

    @Badge.ApprovedBadges
    public
    String getApprovedBadge() {
        return approvedBadge;
    }

    public String getPendingTitle() {
        return pendingTitle;
    }

    public String getPendingSubtitle() {
        return pendingSubtitle;
    }

    public String getExitButtonTitle() {
        return exitButtonTitle;
    }

    public String getPendingContentTitle() {
        return pendingContentTitle;
    }

    public String getPendingContentText() {
        return pendingContentText;
    }

    public String getSecondaryPendingExitButtonTitle() {
        return secondaryPendingExitButtonTitle;
    }

    public String getSecondaryCongratsExitButtonTitle() {
        return secondaryCongratsExitButtonTitle;
    }

    public Integer getSecondaryCongratsExitResultCode() {
        return secondaryCongratsExitResultCode;
    }

    public String getSecondaryRejectedExitButtonTitle() {
        return secondaryRejectedExitButtonTitle;
    }

    public String getRejectedTitle() {
        return rejectedTitle;
    }

    public String getRejectedSubtitle() {
        return rejectedSubtitle;
    }

    public String getRejectedContentTitle() {
        return rejectedContentTitle;
    }

    public String getRejectedContentText() {
        return rejectedContentText;
    }

    public Integer getRejectedIcon() {
        return rejectedIcon;
    }

    public String getRejectedUrlIcon() {
        return rejectedUrlIcon;
    }

    public String getRejectedIconSubtext() {
        return rejectedIconSubtext;
    }

    public Integer getPendingIcon() {
        return pendingIcon;
    }

    public String getPendingUrlIcon() {
        return pendingUrlIcon;
    }

    public boolean isApprovedReceiptEnabled() {
        return enableApprovedReceipt;
    }

    public boolean isApprovedAmountEnabled() {
        return enableApprovedAmount;
    }

    public boolean isApprovedPaymentMethodInfoEnabled() {
        return enableApprovedPaymentMethodInfo;
    }

    public boolean isCongratsSecondaryExitButtonEnabled() {
        return enableCongratsSecondaryExitButton;
    }

    public boolean isPendingSecondaryExitButtonEnabled() {
        return enablePendingSecondaryExitButton;
    }

    public boolean isRejectedSecondaryExitButtonEnabled() {
        return enableRejectedSecondaryExitButton;
    }

    public boolean isPendingContentTextEnabled() {
        return enablePendingContentText;
    }

    public boolean isPendingContentTitleEnabled() {
        return enablePendingContentTitle;
    }

    public boolean isRejectedContentTextEnabled() {
        return enableRejectedContentText;
    }

    public boolean isRejectedContentTitleEnabled() {
        return enableRejectedContentTitle;
    }

    public boolean isRejectedIconSubtextEnabled() {
        return enableRejectedIconSubtext;
    }

    public boolean isRejectedLabelTextEnabled() {
        return enableRejectedLabelText;
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

    public boolean isRejectionRetryEnabled() {
        return rejectionRetryEnabled;
    }

    public static class Builder {

        private String approvedTitle;
        private String approvedLabelText;
        @Badge.ApprovedBadges private
        String approvedBadge;

        private Integer titleBackgroundColor;
        private String approvedSubtitle;
        private Integer approvedIcon;
        private String approvedUrlIcon;
        private String pendingTitle;
        private String pendingSubtitle;
        private String pendingContentTitle;
        private String pendingContentText;
        private Integer pendingIcon;
        private String pendingUrlIcon;
        private String exitButtonTitle;
        private String secondaryPendingExitButtonTitle;
        private String secondaryCongratsExitButtonTitle;
        private String secondaryRejectedExitButtonTitle;
        private String rejectedTitle;
        private String rejectedSubtitle;
        private Integer rejectedIcon;
        private String rejectedUrlIcon;
        private String rejectedIconSubtext;
        private String rejectedContentTitle;
        private String rejectedContentText;
        private boolean rejectionRetryEnabled = true;
        private boolean enablePendingContentText = true;
        private boolean enablePendingContentTitle = true;
        private boolean enableRejectedContentText = true;
        private boolean enableRejectedContentTitle = true;
        private boolean enableRejectedIconSubtext = true;
        private boolean enableRejectedLabelText = true;
        private boolean enableCongratsSecondaryExitButton = true;
        private boolean enablePendingSecondaryExitButton = true;
        private boolean enableRejectedSecondaryExitButton = true;
        private boolean enableApprovedReceipt = true;
        private boolean enableApprovedAmount = true;
        private boolean enableApprovedPaymentMethodInfo = true;

        private Integer secondaryCongratsExitResultCode;
        private Integer secondaryPendingExitResultCode;
        private Integer secondaryRejectedExitResultCode;

        private final Map<String, CustomComponentFactory> approvedCustomComponents = new HashMap<>();

        public Builder() {
        }

        //Nuevo customizable

        public Builder setApprovedTitle(String title) {
            approvedTitle = title;
            return this;
        }

        public Builder setRejectedTitle(String title) {
            rejectedTitle = title;
            return this;
        }

        public Builder setPendingTitle(String title) {
            pendingTitle = title;
            return this;
        }

        public Builder setApprovedLabelText(String label) {
            approvedLabelText = label;
            return this;
        }

        public Builder disableRejectedLabelText() {
            enableRejectedLabelText = false;
            return this;
        }

        public Builder setBadgeApproved(@Badge.ApprovedBadges String approvedBadge) {
            this.approvedBadge = approvedBadge;
            return this;
        }

        public Builder setApprovedHeaderIcon(@DrawableRes int headerIcon) {
            approvedIcon = headerIcon;
            return this;
        }

        public Builder setPendingHeaderIcon(@DrawableRes int headerIcon) {
            pendingIcon = headerIcon;
            return this;
        }

        public Builder setRejectedHeaderIcon(@DrawableRes int headerIcon) {
            rejectedIcon = headerIcon;
            return this;
        }

        public Builder setApprovedHeaderIcon(@NonNull String headerIconUrl) {
            approvedUrlIcon = headerIconUrl;
            return this;
        }

        public Builder setPendingHeaderIcon(@NonNull String headerIconUrl) {
            pendingUrlIcon = headerIconUrl;
            return this;
        }

        public Builder setRejectedHeaderIcon(@NonNull String headerIconUrl) {
            rejectedUrlIcon = headerIconUrl;
            return this;
        }

        //hasta acá

        @Deprecated
        public Builder setApprovedSubtitle(String subtitle) {
            approvedSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder setPendingSubtitle(String subtitle) {
            pendingSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder disableRejectedIconSubtext() {
            enableRejectedIconSubtext = false;
            enableRejectedLabelText = false;
            return this;
        }

        @Deprecated
        public Builder setRejectedSubtitle(String subtitle) {
            rejectedSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder setRejectedIconSubtext(String text) {
            rejectedIconSubtext = text;
            return this;
        }

        @Deprecated
        public Builder setTitleBackgroundColor(@ColorInt Integer titleBackgroundColor) {
            this.titleBackgroundColor = titleBackgroundColor;
            return this;
        }

        //

        //body
        public Builder setApprovedCustomComponentFactory(@NonNull final CustomComponentFactory factory,
                                                         @NonNull final String position) {
            approvedCustomComponents.put(position, factory);
            return this;
        }

        //body
        public Builder setPendingContentTitle(String title) {
            pendingContentTitle = title;
            return this;
        }

        //body
        public Builder setPendingContentText(String text) {
            pendingContentText = text;
            return this;
        }

        //body
        public Builder disableApprovedPaymentMethodInfo() {
            enableApprovedPaymentMethodInfo = false;
            return this;
        }

        //body
        public Builder disablePendingContentText() {
            enablePendingContentText = false;
            return this;
        }

        //body
        public Builder disablePendingContentTitle() {
            enablePendingContentTitle = false;
            return this;
        }

        //body
        public Builder disableRejectedContentText() {
            enableRejectedContentText = false;
            return this;
        }

        //body
        public Builder disableRejectedContentTitle() {
            enableRejectedContentTitle = false;
            return this;
        }

        //body
        public Builder disableRejectionRetry() {
            rejectionRetryEnabled = false;
            return this;
        }

        //body
        public Builder setRejectedContentText(String text) {
            rejectedContentText = text;
            return this;
        }

        //body
        public Builder setRejectedContentTitle(String title) {
            rejectedContentTitle = title;
            return this;
        }

        //body
        public Builder disableApprovedReceipt() {
            enableApprovedReceipt = false;
            return this;
        }

        //body
        public Builder disableApprovedAmount() {
            enableApprovedAmount = false;
            return this;
        }

        //footer
        public Builder setRejectedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            secondaryRejectedExitButtonTitle = title;
            secondaryRejectedExitResultCode = resultCode;
            return this;
        }

        //footer
        public Builder disableApprovedSecondaryExitButton() {
            enableCongratsSecondaryExitButton = false;
            return this;
        }

        //footer
        public Builder disablePendingSecondaryExitButton() {
            enablePendingSecondaryExitButton = false;
            return this;
        }

        //footer
        public Builder disableRejectedSecondaryExitButton() {
            enableRejectedSecondaryExitButton = false;
            return this;
        }

        @Deprecated
        public Builder setPendingSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            secondaryPendingExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        //footer
        public Builder setExitButtonTitle(String title) {
            exitButtonTitle = title;
            return this;
        }

        //footer
        @Deprecated
        public Builder setApprovedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            secondaryCongratsExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.CONGRATS_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        //footer
        public Builder setApprovedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            secondaryCongratsExitButtonTitle = title;
            secondaryCongratsExitResultCode = resultCode;
            return this;
        }

        //footer
        public Builder setPendingSecondaryExitButton(String title, @NonNull Integer resultCode) {
            secondaryPendingExitButtonTitle = title;
            secondaryPendingExitResultCode = resultCode;
            return this;
        }

        @Deprecated
        public Builder setRejectedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            secondaryRejectedExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        public PaymentResultScreenPreference build() {
            final PaymentResultScreenPreference paymentResultScreenPreference = new PaymentResultScreenPreference(this);
            CheckoutStore.getInstance().setPaymentResultScreenPreference(paymentResultScreenPreference);
            return paymentResultScreenPreference;
        }
    }
}
