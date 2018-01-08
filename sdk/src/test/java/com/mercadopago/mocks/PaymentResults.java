package com.mercadopago.mocks;

import com.mercadopago.model.PaymentResult;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

/**
 * Created by vaserber on 11/2/17.
 */

public class PaymentResults {

    private PaymentResults() {

    }

    public static PaymentResult getStatusApprovedPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_approved.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusInProcessContingencyPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_in_process_contingency.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusInProcessReviewManualPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_in_process_review_manual.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusCallForAuthPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_call_for_auth.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedInsufficientAmountPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_insufficient_amount.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedBadFilledSecuPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_bad_filled_secu.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedBadFilledDatePaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_bad_filled_date.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedBadFilledFormPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_bad_filled_form.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedOtherPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_other.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getStatusRejectedDuplicatedPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_duplicated.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getPaymentMethodOffPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rapipago.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getBoletoApprovedPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_boleto.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }

    public static PaymentResult getBoletoRejectedPaymentResult() {
        String json = ResourcesUtil.getStringResource("payment_result_rejected_boleto.json");
        return JsonUtil.getInstance().fromJson(json, PaymentResult.class);
    }
}
