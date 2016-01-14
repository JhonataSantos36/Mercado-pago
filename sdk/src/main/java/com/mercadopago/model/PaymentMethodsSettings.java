package com.mercadopago.model;

import java.io.Serializable;
import java.util.List;

public class PaymentMethodsSettings implements Serializable {

    private Integer defaultInstallments;
    private String defaultPaymentMethodId;
    private List<String> excludedPaymentMethods;
    private List<String> excludedPaymentTypes;
    private Integer installments;

    public Integer getDefaultInstallments() {
        return defaultInstallments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }
}
