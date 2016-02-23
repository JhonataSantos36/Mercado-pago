package com.mercadopago.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class PaymentMethodPreference implements Serializable {
    private Integer maxInstallments;
    private Integer defaultInstallments;
    private List<String> excludedPaymentMethodIds;
    private List<String> supportedPaymentTypes;
    private List<String> excludedPaymentTypes;
    private String defaultPaymentMethodId;

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

<<<<<<< HEAD
    public void setExcludedPaymentMethods(List<String> excludedPaymentMethods) {
        if(excludedPaymentMethods != null) {
            this.excludedPaymentMethods = new ArrayList<>();
            for (String paymentMethodId : excludedPaymentMethods) {
                PaymentMethod excludedPaymentMethod = new PaymentMethod();
                excludedPaymentMethod.setId(paymentMethodId);
                this.excludedPaymentMethods.add(excludedPaymentMethod);
            }
        }
    }

    public void setExcludedPaymentTypes(List<String> excludedPaymentTypes) {
        if(excludedPaymentTypes != null) {
            this.excludedPaymentTypes = new ArrayList<>();
            for (String paymentTypeId : excludedPaymentTypes) {
                PaymentType excludedPaymentType = new PaymentType();
                excludedPaymentType.setId(paymentTypeId);
                this.excludedPaymentTypes.add(excludedPaymentType);
            }
        }
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }
=======
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes

    public void setExcludedPaymentMethodIds(List<String> excludedPaymentMethodIds) {
        this.excludedPaymentMethodIds = excludedPaymentMethodIds;
    }

    public List<String> getSupportedPaymentTypes() {
        return supportedPaymentTypes;
    }

    public void setSupportedPaymentTypes(List<String> supportedPaymentTypes) {
        this.supportedPaymentTypes = supportedPaymentTypes;
    }

    public void setExcludedPaymentTypes(List<String> excludedPaymentTypes) {
        this.excludedPaymentTypes = excludedPaymentTypes;
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }

    public List<PayerCost> getInstallmentsBelowMax(List<PayerCost> payerCosts){

        List<PayerCost> validPayerCosts = new ArrayList<>();

        if(this.maxInstallments != null) {
            for (PayerCost currentPayerCost : payerCosts) {
                if (currentPayerCost.getInstallments() <= this.maxInstallments) {
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
<<<<<<< HEAD
        List<PaymentMethod> supportedPaymentMethods =  new ArrayList<>();
        if(paymentMethods != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (this.isPaymentMethodSupported(paymentMethod)) {
                    supportedPaymentMethods.add(paymentMethod);
                }
=======
        if(this.excludedPaymentTypes != null || this.supportedPaymentTypes != null) {
            paymentMethods = getPaymentMethodsWithSupportedType(paymentMethods);
        }
        if(this.excludedPaymentMethodIds != null)
        {
            paymentMethods = getNotExcludedPaymentMethods(paymentMethods);
        }
        return paymentMethods;
    }

    private List<PaymentMethod> getNotExcludedPaymentMethods(List<PaymentMethod> paymentMethods) {
        List<PaymentMethod> supportedPaymentMethods = new ArrayList<>();
        for(PaymentMethod currentPaymentMethod : paymentMethods)
        {
            if(!this.excludedPaymentMethodIds.contains(currentPaymentMethod.getId()))
            {
                supportedPaymentMethods.add(currentPaymentMethod);
            }
        }
        return supportedPaymentMethods;
    }

    private List<PaymentMethod> getPaymentMethodsWithSupportedType(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods = new ArrayList<>();
        for(PaymentMethod currentPaymentMethod : paymentMethods)
        {
            if(isPaymentMethodSupported(currentPaymentMethod))
            {
                supportedPaymentMethods.add(currentPaymentMethod);
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
            }
        }
        return supportedPaymentMethods;
    }

    public boolean isPaymentMethodSupported(PaymentMethod paymentMethod) {
        boolean isSupported = true;
<<<<<<< HEAD
        if(paymentMethod == null) {
=======

        if(this.excludedPaymentMethodIds != null && this.excludedPaymentMethodIds.contains(paymentMethod.getId()))
        {
>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
            isSupported = false;
        }
        else {
            List<String> excludedPaymentMethodIds = this.getExcludedPaymentMethodIds();
            List<String> excludedPaymentTypes = this.getExcludedPaymentTypes();

<<<<<<< HEAD
            if ((excludedPaymentMethodIds != null && excludedPaymentMethodIds.contains(paymentMethod.getId()))
                    || (excludedPaymentTypes != null && excludedPaymentTypes.contains(paymentMethod.getPaymentTypeId()))) {
                isSupported = false;
            }
        }
=======
        if(isSupported) {
            if (this.excludedPaymentTypes == null && this.supportedPaymentTypes != null) {

                if (!this.supportedPaymentTypes.contains(paymentMethod.getPaymentTypeId())) {
                    isSupported = false;
                }

            } else if (this.excludedPaymentTypes != null && this.supportedPaymentTypes == null) {

                if (this.excludedPaymentTypes.contains(paymentMethod.getPaymentTypeId())){
                    isSupported = false;
                }

            } else if (this.excludedPaymentTypes != null) {

                if (!this.supportedPaymentTypes.contains(paymentMethod.getPaymentTypeId())
                        || this.excludedPaymentTypes.contains(paymentMethod.getPaymentTypeId())) {
                    isSupported = false;
                }
            }
        }

>>>>>>> parent of df039a5... matched preference to vault settings, added payment methods search classes
        return isSupported;
    }

    public PaymentMethod getDefaultPaymentMethod(List<PaymentMethod> paymentMethods) {
        PaymentMethod defaultPaymentMethod = null;
        if(this.defaultPaymentMethodId != null && paymentMethods != null) {
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
