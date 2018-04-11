package com.mercadopago.mocks;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.Payment;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

public class Payments {
    public static Payment getApprovedPayment() {
        String json = ResourcesUtil.getStringResource("approved_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getRejectedPayment() {
        String json = ResourcesUtil.getStringResource("rejected_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getPendingPayment() {
        String json = ResourcesUtil.getStringResource("pending_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getCallForAuthPayment() {
        String json = ResourcesUtil.getStringResource("call_for_auth_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static ApiException getInvalidESCPayment() {
        String json = ResourcesUtil.getStringResource("invalid_esc_payment.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }

    public static ApiException getInvalidIdentificationPayment() {
        String json = ResourcesUtil.getStringResource("invalid_identification_payment.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }
}
