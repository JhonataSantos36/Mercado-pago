package com.mercadopago.model;

import java.io.Serializable;
import java.util.List;

public class PaymentSettings implements Serializable {

    private Integer defaultInstallments;
    private Integer maxAcceptedInstallments;
    private String defaultPaymentTypeId;
    private String defaultPaymentMethodId;
    private List<String> excludedPaymentMethodsIds;
    private List<String> excludedPaymentTypesIds;

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
