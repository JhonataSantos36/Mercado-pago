package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

public class PaymentMethods {

    private static String doNotFindPaymentMethodsException = "{\"message\":\"doesn't find payment methods\",\"error\":\"payment methods not found error\",\"cause\":[]}";

    private PaymentMethods() {

    }

    public static PaymentMethod getPaymentMethodWithWrongSecurityCodeSettings() {
        String json = ResourcesUtil.getStringResource("payment_method_security_code_length_0.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static ApiException getDoNotFindPaymentMethodsException() {
        return JsonUtil.getInstance().fromJson(doNotFindPaymentMethodsException, ApiException.class);
    }

    public static PaymentMethod getPaymentMethodWithIdNotRequired() {
        String json = ResourcesUtil.getStringResource("payment_method_id_not_required.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnDebit() {
        String json = ResourcesUtil.getStringResource("payment_method_on_debit.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
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

    public static List<PaymentMethod> getPaymentMethodListMLA() {
        List<PaymentMethod> paymentMethodList;
        String json = ResourcesUtil.getStringResource("payment_methods.json");

        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }

    public static List<PaymentMethod> getPaymentMethodListWithTwoOptions() {
        List<PaymentMethod> paymentMethodList;
        String json = ResourcesUtil.getStringResource("payment_methods_two_options.json");

        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }

    public static List<PaymentMethod> getPaymentMethodListMLM() {
        List<PaymentMethod> paymentMethodList;
        String json = ResourcesUtil.getStringResource("payment_methods_mlm.json");

        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }
}
