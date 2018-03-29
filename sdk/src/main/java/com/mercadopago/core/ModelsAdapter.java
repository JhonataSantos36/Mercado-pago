package com.mercadopago.core;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.lite.constants.ProcessingModes;
import com.mercadopago.lite.controllers.CustomServicesHandler;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.lite.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.Map;

public class ModelsAdapter {

    public static Map<String, Object> adapt(PaymentBody paymentBody) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> payload = JsonUtil.getInstance().getGson().fromJson(JsonUtil.getInstance().toJson(paymentBody), type);
        payload.put("issuer_id", paymentBody.getIssuerId());
        payload.put("installments", paymentBody.getInstallments());
        payload.put("campaign_id", paymentBody.getCampaignId());
        return payload;
    }

    public static CustomServicesHandler adapt(ServicePreference servicePreference) {
        if (servicePreference != null) {
            com.mercadopago.lite.preferences.ServicePreference.Builder builder = new com.mercadopago.lite.preferences.ServicePreference.Builder();
            builder.setDefaultBaseURL(servicePreference.getDefaultBaseURL());
            builder.setGatewayURL(servicePreference.getGatewayBaseURL());
            if (servicePreference.hasGetCustomerURL()) {
                builder.setGetCustomerURL(servicePreference.getGetCustomerURL(), servicePreference.getGetCustomerURI(), servicePreference.getGetCustomerAdditionalInfo());
            }

            if (servicePreference.hasGetDiscountURL()) {
                builder.setDiscountURL(servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo());
            }

            if (servicePreference.hasCreateCheckoutPrefURL()) {
                builder.setCreateCheckoutPreferenceURL(servicePreference.getCreateCheckoutPreferenceURL(), servicePreference.getCreateCheckoutPreferenceURI(), servicePreference.getCreateCheckoutPreferenceAdditionalInfo());
            }

            if (servicePreference.hasCreatePaymentURL()) {
                builder.setCreatePaymentURL(servicePreference.getCreatePaymentURL(), servicePreference.getCreatePaymentURI(), servicePreference.getCreatePaymentAdditionalInfo());
            }

            if (ProcessingModes.HYBRID.equals(servicePreference.getProcessingModeString())) {
                builder.setHybridAsProcessingMode();
            } else if (ProcessingModes.GATEWAY.equals(servicePreference.getProcessingModeString())) {
                builder.setGatewayAsProcessingMode();
            } else {
                builder.setAggregatorAsProcessingMode();
            }
            com.mercadopago.lite.controllers.CustomServicesHandler.getInstance().setServices(builder.build());
        }
        return com.mercadopago.lite.controllers.CustomServicesHandler.getInstance();
    }
}
