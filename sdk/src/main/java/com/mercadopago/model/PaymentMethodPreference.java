package com.mercadopago.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class PaymentMethodPreference implements Serializable {
    //maxInstallments
    private Integer installments;

    private Integer defaultInstallments;
    private List<PaymentMethod> excludedPaymentMethods;
    private List<PaymentType> excludedPaymentTypes;
    private String defaultPaymentMethodId;

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

    public void setExcludedPaymentMethods(List<String> excludedPaymentMethods) {
        this.excludedPaymentMethods = new ArrayList<>();
        for(String paymentMethodId : excludedPaymentMethods)
        {
            PaymentMethod excludedPaymentMethod = new PaymentMethod();
            excludedPaymentMethod.setId(paymentMethodId);
            this.excludedPaymentMethods.add(excludedPaymentMethod);
        }

    }

    public void setExcludedPaymentTypes(List<String> excludedPaymentTypes) {
        this.excludedPaymentTypes = new ArrayList<>();
        for(String paymentTypeId : excludedPaymentTypes)
        {
            PaymentType excludedPaymentType = new PaymentType();
            excludedPaymentType.setId(paymentTypeId);
            this.excludedPaymentTypes.add(excludedPaymentType);
        }
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public Integer getDefaultInstallments() {
        return defaultInstallments;
    }

    public List<String> getExcludedPaymentMethodIds() {
        if(this.excludedPaymentMethods != null) {
            List<String> excludedPaymentMethodIds = new ArrayList<>();
            for (PaymentMethod paymentMethod : this.excludedPaymentMethods) {
                excludedPaymentMethodIds.add(paymentMethod.getId());
            }
            return excludedPaymentMethodIds;
        }
        else
            return null;
    }

    public List<String> getExcludedPaymentTypes() {
        if(this.excludedPaymentTypes != null) {

            List<String> excludedPaymentTypeIds = new ArrayList<>();
            for (PaymentType paymentType : this.excludedPaymentTypes) {
                excludedPaymentTypeIds.add(paymentType.getId());
            }
            return excludedPaymentTypeIds;
        }
        else
            return null;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public List<PayerCost> getInstallmentsBelowMax(List<PayerCost> payerCosts){
        List<PayerCost> validPayerCosts = new ArrayList<>();

        if(this.installments != null) {
            for (PayerCost currentPayerCost : payerCosts) {
                if (currentPayerCost.getInstallments() <= this.installments) {
                    validPayerCosts.add(currentPayerCost);
                }
            }
            return validPayerCosts;
        }
        else {
            return payerCosts;
        }

    }

    public PayerCost getDefaultInstallments(List<PayerCost> payerCosts){
        PayerCost defaultPayerCost = null;

        for(PayerCost currentPayerCost : payerCosts)
        {
            if(currentPayerCost.getInstallments() == this.defaultInstallments) {
                defaultPayerCost = currentPayerCost;
                break;
            }
        }

        return defaultPayerCost;
    }

    public List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {
        List<PaymentMethod> supportedPaymentMethods =  new ArrayList<>();
        for(PaymentMethod paymentMethod : paymentMethods)
        {
            if(this.isPaymentMethodSupported(paymentMethod))
            {
                supportedPaymentMethods.add(paymentMethod);
            }
        }
        return supportedPaymentMethods;
    }

    public boolean isPaymentMethodSupported(PaymentMethod paymentMethod) {
        boolean isSupported = true;
        List<String> excludedPaymentMethodIds = this.getExcludedPaymentMethodIds();
        List<String> excludedPaymentTypes = this.getExcludedPaymentTypes();

        if((excludedPaymentMethodIds != null && excludedPaymentMethodIds.contains(paymentMethod.getId()))
                || (excludedPaymentTypes != null && excludedPaymentTypes.contains(paymentMethod.getPaymentTypeId())))
        {
            isSupported = false;
        }

        return isSupported;
    }

    public PaymentMethod getDefaultPaymentMethod(List<PaymentMethod> paymentMethods) {
        PaymentMethod defaultPaymentMethod = null;
        if(this.defaultPaymentMethodId != null) {
            for (PaymentMethod pm : paymentMethods) {
                if (pm.getId().equals(this.defaultPaymentMethodId)) {
                    defaultPaymentMethod = pm;
                    break;
                }
            }
        }
        return defaultPaymentMethod;
    }
}
