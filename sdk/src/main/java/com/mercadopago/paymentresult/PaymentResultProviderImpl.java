package com.mercadopago.paymentresult;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.lite.model.Instructions;
import com.mercadopago.mvp.TaggedCallback;


public class PaymentResultProviderImpl implements PaymentResultProvider {
    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public PaymentResultProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
    }

    @Override
    public void getInstructionsAsync(Long paymentId, String paymentTypeId, final TaggedCallback<Instructions> taggedCallback) {
        mercadoPago.getInstructions(paymentId, paymentTypeId, taggedCallback);
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }

    @Override
    public String getApprovedTitle() {
        return context.getString(R.string.mpsdk_title_approved_payment);
    }

    @Override
    public String getPendingTitle() {
        return context.getString(R.string.mpsdk_title_pending_payment);
    }

    @Override
    public String getRejectedOtherReasonTitle(String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_title_other_reason_rejection), paymentMethodName);
    }

    @Override
    public String getRejectedInsufficientAmountTitle(String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_text_insufficient_amount), paymentMethodName);
    }

    @Override
    public String getRejectedDuplicatedPaymentTitle(String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_title_other_reason_rejection), paymentMethodName);
    }

    @Override
    public String getRejectedCardDisabledTitle(String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_text_active_card), paymentMethodName);
    }

    @Override
    public String getRejectedBadFilledCardTitle(String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_text_some_card_data_is_incorrect), paymentMethodName);
    }

    @Override
    public String getRejectedBadFilledCardTitle() {
        return context.getString(R.string.mpsdk_text_card_data_invalid);
    }

    @Override
    public String getRejectedHighRiskTitle() {
        return context.getString(R.string.mpsdk_title_rejection_high_risk);
    }

    @Override
    public String getRejectedMaxAttemptsTitle() {
        return context.getString(R.string.mpsdk_title_rejection_max_attempts);
    }

    @Override
    public String getRejectedInsufficientDataTitle() {
        return context.getString(R.string.mpsdk_bolbradesco_rejection);
    }

    @Override
    public String getRejectedBadFilledOther() {
        return context.getString(R.string.mpsdk_title_bad_filled_other);
    }

    @Override
    public String getRejectedCallForAuthorizeTitle() {
        return context.getString(R.string.mpsdk_title_activity_call_for_authorize);
    }

    @Override
    public String getEmptyText() {
        return context.getString(R.string.mpsdk_empty_string);
    }

    @Override
    public String getPendingLabel() {
        return context.getString(R.string.mpsdk_pending_label);
    }

    @Override
    public String getRejectionLabel() {
        return context.getString(R.string.mpsdk_rejection_label);
    }

    @Override
    public String getCancelPayment() {
        return context.getString(R.string.mpsdk_cancel_payment);
    }

    @Override
    public String getContinueShopping() {
        return context.getString(R.string.mpsdk_continue_shopping);
    }

    @Override
    public String getExitButtonDefaultText() {
        return context.getString(R.string.mpsdk_cancel_payment);
    }

    @Override
    public String getChangePaymentMethodLabel() {
        return context.getString(R.string.mpsdk_text_pay_with_other_method);
    }

    @Override
    public String getRecoverPayment() {
        return context.getString(R.string.mpsdk_text_enter_again);
    }

    @Override
    public String getCardEnabled() {
        return context.getString(R.string.mpsdk_text_card_enabled);
    }

    @Override
    public String getErrorTitle() {
        return context.getString(R.string.mpsdk_what_can_do);
    }

    @Override
    public String getPendingContingencyBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_contingency);
    }

    @Override
    public String getPendingReviewManualBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_review_manual);
    }

    @Override
    public String getRejectedCallForAuthBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_call);
    }

    @Override
    public String getRejectedCardDisabledBodyErrorDescription(String paymentMethodName) {
        return context.getString(R.string.mpsdk_error_description_card_disabled, paymentMethodName);
    }

    @Override
    public String getRejectedInsufficientAmountBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_insufficient_amount);
    }

    @Override
    public String getRejectedInsufficientAmountBodyErrorSecondDescription() {
        return context.getString(R.string.mpsdk_error_description_second_insufficient_amount);
    }

    @Override
    public String getRejectedOtherReasonBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_other_reason);
    }

    @Override
    public String getRejectedByBankBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_by_bank);
    }

    @Override
    public String getRejectedInsufficientDataBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_insufficient_data);
    }

    @Override
    public String getRejectedDuplicatedPaymentBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_duplicated_payment);
    }

    @Override
    public String getRejectedMaxAttemptsBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_max_attempts);
    }

    @Override
    public String getRejectedHighRiskBodyErrorDescription() {
        return context.getString(R.string.mpsdk_error_description_high_risk);
    }

    @Override
    public String getRejectedCallForAuthBodyActionText(final String paymentMethodName) {
        return String.format(context.getString(R.string.mpsdk_text_authorized_call_for_authorize), paymentMethodName);
    }

    @Override
    public String getRejectedCallForAuthBodySecondaryTitle() {
        return context.getString(R.string.mpsdk_error_secondary_title_call);
    }

    @Override
    public String getReceiptDescription(final Long receiptId) {
        String description = "";
        if (receiptId != null) {
            description = context.getResources().getString(R.string.mpsdk_receipt, String.valueOf(receiptId));
        }
        return description;
    }
}
