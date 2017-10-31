package com.mercadopago.lite.preferences;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.PaymentType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 10/23/17.
 */

public class PaymentPreference {

    @SerializedName("installments")
    private Integer maxAcceptedInstallments;
    private Integer defaultInstallments;
    private List<PaymentMethod> excludedPaymentMethods;
    private List<PaymentType> excludedPaymentTypes;
    private String defaultPaymentMethodId;
    private String defaultPaymentTypeId;

    public Integer getMaxAcceptedInstallments() {
        return maxAcceptedInstallments;
    }

    public void setMaxAcceptedInstallments(Integer maxAcceptedInstallments) {
        this.maxAcceptedInstallments = maxAcceptedInstallments;
    }
    public Integer getMaxInstallments() {
        return maxAcceptedInstallments;
    }

    public Integer getDefaultInstallments() {
        return defaultInstallments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

    public List<String> getExcludedPaymentMethodIds() {
        if (this.excludedPaymentMethods != null) {
            List<String> excludedPaymentMethodIds = new ArrayList<>();
            for (PaymentMethod paymentMethod : this.excludedPaymentMethods) {
                excludedPaymentMethodIds.add(paymentMethod.getId());
            }
            return excludedPaymentMethodIds;
        } else
            return null;
    }

    public List<String> getExcludedPaymentTypeIds() {
        if (this.excludedPaymentTypes != null) {
            List<String> excludedPaymentTypeIds = new ArrayList<>();
            for (PaymentType paymentType : this.excludedPaymentTypes) {
                excludedPaymentTypeIds.add(paymentType.getId());
            }
            return excludedPaymentTypeIds;
        } else
            return null;
    }

    public void setExcludedPaymentMethodIds(List<String> excludedPaymentMethodIds) {
        if (excludedPaymentMethodIds != null) {
            this.excludedPaymentMethods = new ArrayList<>();
            for (String paymentMethodId : excludedPaymentMethodIds) {
                PaymentMethod excludedPaymentMethod = new PaymentMethod();
                excludedPaymentMethod.setId(paymentMethodId);
                this.excludedPaymentMethods.add(excludedPaymentMethod);
            }
        }
    }

    public void setExcludedPaymentTypeIds(List<String> excludedPaymentTypeIds) {
        if (excludedPaymentTypeIds != null) {
            this.excludedPaymentTypes = new ArrayList<>();
            for (String paymentTypeId : excludedPaymentTypeIds) {
                PaymentType excludedPaymentType = new PaymentType();
                excludedPaymentType.setId(paymentTypeId);
                this.excludedPaymentTypes.add(excludedPaymentType);
            }
        }
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }

    public String getDefaultPaymentTypeId() {
        return defaultPaymentTypeId;
    }

    public void setDefaultPaymentTypeId(String defaultPaymentTypeId) {
        this.defaultPaymentTypeId = defaultPaymentTypeId;
    }

}
