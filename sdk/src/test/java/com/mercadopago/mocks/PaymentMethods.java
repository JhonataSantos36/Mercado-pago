package com.mercadopago.mocks;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

public class PaymentMethods {
    private PaymentMethods() {
    }

    public static PaymentMethod getPaymentMethodOnVisa() {
        String json = ResourcesUtil.getStringResource("payment_method_visa.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnMaster() {
        String json = ResourcesUtil.getStringResource("payment_method_on_master.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnMasterWithoutSecurityCodeSettings() {
        String json = ResourcesUtil.getStringResource("payment_method_on_master_without_sec_code_settings.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOff() {
        String json = ResourcesUtil.getStringResource("payment_method_pagofacil.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }
}
