package com.mercadopago.model;

import java.util.List;

public class PaymentSettings {

    private final Integer defaultInstallments;
    private final Integer maxAcceptedInstallments;
    private final String defaultPaymentTypeId;
    private final String defaultPaymentMethodId;
    private final List<String> excludedPaymentMethodsIds;
    private final List<String> excludedPaymentTypesIds;

    public PaymentSettings(Integer defaultInstallments, Integer maxAcceptedInstallments,
                           String defaultPaymentTypeId, String defaultPaymentMethodId,
                           List<String> excludedPaymentMethodsIds, List<String> excludedPaymentTypesIds) {
        this.defaultInstallments = defaultInstallments;
        this.maxAcceptedInstallments = maxAcceptedInstallments;
        this.defaultPaymentTypeId = defaultPaymentTypeId;
        this.defaultPaymentMethodId = defaultPaymentMethodId;
        this.excludedPaymentMethodsIds = excludedPaymentMethodsIds;
        this.excludedPaymentTypesIds = excludedPaymentTypesIds;
    }

    public Integer getDefaultInstallments() {
        return defaultInstallments;
    }

    public Integer getMaxAcceptedInstallments() {
        return maxAcceptedInstallments;
    }

    public String getDefaultPaymentTypeId() {
        return defaultPaymentTypeId;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public List<String> getExcludedPaymentMethodsIds() {
        return excludedPaymentMethodsIds;
    }

    public List<String> getExcludedPaymentTypesIds() {
        return excludedPaymentTypesIds;
    }
}
