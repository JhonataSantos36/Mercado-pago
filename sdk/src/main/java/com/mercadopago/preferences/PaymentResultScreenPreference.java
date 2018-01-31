package com.mercadopago.preferences;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentResultCallback;
import com.mercadopago.constants.ContentLocation;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Reviewable;
import com.mercadopago.paymentresult.model.Badge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 2/13/17.
 */

public class PaymentResultScreenPreference {

    private Integer titleBackgroundColor;
    private String approvedTitle;
    private String approvedSubtitle;
    private Integer approvedIcon;
    private String approvedUrlIcon;
    private String approvedLabelText;
    private @Badge.ApprovedBadges
    String approvedBadge;
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
    private transient Map<ContentLocation, List<Reviewable>> congratsReviewables;
    private transient List<Reviewable> pendingReviewables;

    private Integer secondaryRejectedExitResultCode;
    private Integer secondaryCongratsExitResultCode;
    private Integer secondaryPendingExitResultCode;
    private Boolean rejectionRetryEnabled;

    private Map<String, CustomComponentFactory> approvedCustomComponents;

    private PaymentResultScreenPreference(Builder builder) {
        this.titleBackgroundColor = builder.titleBackgroundColor;
        this.approvedTitle = builder.approvedTitle;
        this.approvedSubtitle = builder.approvedSubtitle;
        this.approvedIcon = builder.approvedIcon;
        this.approvedUrlIcon = builder.approvedUrlIcon;
        this.approvedLabelText = builder.approvedLabelText;
        this.approvedBadge = builder.approvedBadge;
        this.pendingTitle = builder.pendingTitle;
        this.pendingSubtitle = builder.pendingSubtitle;
        this.pendingContentTitle = builder.pendingContentTitle;
        this.pendingContentText = builder.pendingContentText;
        this.pendingIcon = builder.pendingIcon;
        this.pendingUrlIcon = builder.pendingUrlIcon;
        this.exitButtonTitle = builder.exitButtonTitle;
        this.secondaryPendingExitButtonTitle = builder.secondaryPendingExitButtonTitle;
        this.secondaryPendingExitResultCode = builder.secondaryPendingExitResultCode;
        this.secondaryCongratsExitButtonTitle = builder.secondaryCongratsExitButtonTitle;
        this.secondaryCongratsExitResultCode = builder.secondaryCongratsExitResultCode;
        this.secondaryRejectedExitButtonTitle = builder.secondaryRejectedExitButtonTitle;
        this.secondaryRejectedExitResultCode = builder.secondaryRejectedExitResultCode;
        this.rejectedTitle = builder.rejectedTitle;
        this.rejectedSubtitle = builder.rejectedSubtitle;
        this.rejectedIcon = builder.rejectedIcon;
        this.rejectedUrlIcon = builder.rejectedUrlIcon;
        this.rejectedIconSubtext = builder.rejectedIconSubtext;
        this.rejectedContentTitle = builder.rejectedContentTitle;
        this.rejectedContentText = builder.rejectedContentText;
        this.rejectedContentTitle = builder.rejectedContentTitle;
        this.rejectionRetryEnabled = builder.rejectionRetryEnabled;

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
        this.enableRejectedLabelText = builder.enableRejectedLabelText;
        this.pendingReviewables = builder.pendingReviewables;

        this.congratsReviewables = new HashMap<>();
        this.congratsReviewables.put(ContentLocation.BOTTOM, builder.bottomCongratsReviewables);
        this.congratsReviewables.put(ContentLocation.TOP, builder.topCongratsReviewables);

        this.approvedCustomComponents = builder.approvedCustomComponents;
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

    public boolean hasCustomCongratsReviewables() {
        return congratsReviewables != null && !congratsReviewables.isEmpty();
    }

    public boolean hasCustomPendingReviewables() {
        return pendingReviewables != null && !pendingReviewables.isEmpty();
    }

    @Deprecated
    public List<Reviewable> getCongratsReviewables() {
        return congratsReviewables.get(ContentLocation.BOTTOM);
    }

    public List<Reviewable> getCongratsReviewables(ContentLocation location) {
        return congratsReviewables.get(location);
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

    public Integer getApprovedIcon() {
        return approvedIcon;
    }

    public String getApprovedUrlIcon() {
        return approvedUrlIcon;
    }

    public String getApprovedLabelText() {
        return approvedLabelText;
    }

    public @Badge.ApprovedBadges
    String getApprovedBadge() {
        return approvedBadge;
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

    public boolean isRejectedLabelTextEnabled() {
        return this.enableRejectedLabelText;
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
        private @Badge.ApprovedBadges
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
        private List<Reviewable> topCongratsReviewables;
        private List<Reviewable> bottomCongratsReviewables;
        private List<Reviewable> pendingReviewables;

        private Integer secondaryCongratsExitResultCode;
        private Integer secondaryPendingExitResultCode;
        private Integer secondaryRejectedExitResultCode;

        private Map<String, CustomComponentFactory> approvedCustomComponents = new HashMap<>();

        public Builder() {
            this.topCongratsReviewables = new ArrayList<>();
            this.bottomCongratsReviewables = new ArrayList<>();
            this.pendingReviewables = new ArrayList<>();
        }

        //Nuevo customizable

        public Builder setApprovedTitle(String title) {
            this.approvedTitle = title;
            return this;
        }

        public Builder setRejectedTitle(String title) {
            this.rejectedTitle = title;
            return this;
        }

        public Builder setPendingTitle(String title) {
            this.pendingTitle = title;
            return this;
        }

        public Builder setApprovedLabelText(String label) {
            this.approvedLabelText = label;
            return this;
        }

        public Builder disableRejectedLabelText() {
            this.enableRejectedLabelText = false;
            return this;
        }

        public Builder setBadgeApproved(@Badge.ApprovedBadges String approvedBadge) {
            this.approvedBadge = approvedBadge;
            return this;
        }

        public Builder setApprovedHeaderIcon(@DrawableRes int headerIcon) {
            this.approvedIcon = headerIcon;
            return this;
        }

        public Builder setPendingHeaderIcon(@DrawableRes int headerIcon) {
            this.pendingIcon = headerIcon;
            return this;
        }

        public Builder setRejectedHeaderIcon(@DrawableRes int headerIcon) {
            this.rejectedIcon = headerIcon;
            return this;
        }

        public Builder setApprovedHeaderIcon(@NonNull String headerIconUrl) {
            this.approvedUrlIcon = headerIconUrl;
            return this;
        }

        public Builder setPendingHeaderIcon(@NonNull String headerIconUrl) {
            this.pendingUrlIcon = headerIconUrl;
            return this;
        }

        public Builder setRejectedHeaderIcon(@NonNull String headerIconUrl) {
            this.rejectedUrlIcon = headerIconUrl;
            return this;
        }

        //hasta acá

        @Deprecated
        public Builder setApprovedSubtitle(String subtitle) {
            this.approvedSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder setPendingSubtitle(String subtitle) {
            this.pendingSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder disableRejectedIconSubtext() {
            this.enableRejectedIconSubtext = false;
            this.enableRejectedLabelText = false;
            return this;
        }

        @Deprecated
        public Builder setRejectedSubtitle(String subtitle) {
            this.rejectedSubtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder setRejectedIconSubtext(String text) {
            this.rejectedIconSubtext = text;
            return this;
        }

        @Deprecated
        public Builder setTitleBackgroundColor(@ColorInt Integer titleBackgroundColor) {
            this.titleBackgroundColor = titleBackgroundColor;
            return this;
        }

        //

        //body
        @Deprecated
        public Builder addCongratsReviewable(Reviewable customReviewable) {
            addCongratsReviewable(customReviewable, ContentLocation.BOTTOM);
            return this;
        }

        public Builder setApprovedCustomComponentFactory(@NonNull final CustomComponentFactory factory,
                                                         @NonNull final String position) {
            this.approvedCustomComponents.put(position, factory);
            return this;
        }

        //body
        public Builder addCongratsReviewable(Reviewable reviewable, ContentLocation location) {
            if (ContentLocation.BOTTOM.equals(location)) {
                this.bottomCongratsReviewables.add(reviewable);
            } else {
                this.topCongratsReviewables.add(reviewable);
            }
            return this;
        }

        //body
        public Builder setPendingContentTitle(String title) {
            this.pendingContentTitle = title;
            return this;
        }

        //body
        public Builder setPendingContentText(String text) {
            this.pendingContentText = text;
            return this;
        }

        //body
        public Builder addPendingReviewable(Reviewable customReviewable) {
            this.pendingReviewables.add(customReviewable);
            return this;
        }

        //body
        public Builder disableApprovedPaymentMethodInfo() {
            this.enableApprovedPaymentMethodInfo = false;
            return this;
        }

        //body
        public Builder disablePendingContentText() {
            this.enablePendingContentText = false;
            return this;
        }

        //body
        public Builder disablePendingContentTitle() {
            this.enablePendingContentTitle = false;
            return this;
        }

        //body
        public Builder disableRejectedContentText() {
            this.enableRejectedContentText = false;
            return this;
        }

        //body
        public Builder disableRejectedContentTitle() {
            this.enableRejectedContentTitle = false;
            return this;
        }

        //body
        public Builder disableRejectionRetry() {
            this.rejectionRetryEnabled = false;
            return this;
        }

        //body
        public Builder setRejectedContentText(String text) {
            this.rejectedContentText = text;
            return this;
        }

        //body
        public Builder setRejectedContentTitle(String title) {
            this.rejectedContentTitle = title;
            return this;
        }

        //body
        public Builder disableApprovedReceipt() {
            this.enableApprovedReceipt = false;
            return this;
        }

        //body
        public Builder disableApprovedAmount() {
            this.enableApprovedAmount = false;
            return this;
        }

        //footer
        public Builder setRejectedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            this.secondaryRejectedExitButtonTitle = title;
            this.secondaryRejectedExitResultCode = resultCode;
            return this;
        }

        //footer
        public Builder disableApprovedSecondaryExitButton() {
            this.enableCongratsSecondaryExitButton = false;
            return this;
        }

        //footer
        public Builder disablePendingSecondaryExitButton() {
            this.enablePendingSecondaryExitButton = false;
            return this;
        }

        //footer
        public Builder disableRejectedSecondaryExitButton() {
            this.enableRejectedSecondaryExitButton = false;
            return this;
        }

        @Deprecated
        public Builder setPendingSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            this.secondaryPendingExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        //footer
        public Builder setExitButtonTitle(String title) {
            this.exitButtonTitle = title;
            return this;
        }

        //footer
        @Deprecated
        public Builder setApprovedSecondaryExitButton(String title, PaymentResultCallback paymentResultCallback) {
            this.secondaryCongratsExitButtonTitle = title;
            CallbackHolder.getInstance().addPaymentResultCallback(CallbackHolder.CONGRATS_PAYMENT_RESULT_CALLBACK, paymentResultCallback);
            return this;
        }

        //footer
        public Builder setApprovedSecondaryExitButton(String title, @NonNull Integer resultCode) {
            this.secondaryCongratsExitButtonTitle = title;
            this.secondaryCongratsExitResultCode = resultCode;
            return this;
        }

        //footer
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

        public PaymentResultScreenPreference build() {
            final PaymentResultScreenPreference paymentResultScreenPreference = new PaymentResultScreenPreference(this);
            CheckoutStore.getInstance().setPaymentResultScreenPreference(paymentResultScreenPreference);
            return paymentResultScreenPreference;
        }
    }
}
