package com.mercadopago.preferences;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.Card;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class PaymentPreference {

    @SerializedName("installments")
    private Integer maxInstallments;
    private Integer defaultInstallments;

    //List<String> excludedPaymentMethodIds
    private List<PaymentMethod> excludedPaymentMethods;

    //List<String> excludedPaymentTypeIds
    private List<PaymentType> excludedPaymentTypes;

    private String defaultPaymentMethodId;

    private String defaultPaymentTypeId;


    public void setMaxAcceptedInstallments(Integer installments) {
        maxInstallments = installments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

    public void setExcludedPaymentMethodIds(List<String> excludedPaymentMethodIds) {
        if (excludedPaymentMethodIds != null) {
            excludedPaymentMethods = new ArrayList<>();
            for (String paymentMethodId : excludedPaymentMethodIds) {
                PaymentMethod excludedPaymentMethod = new PaymentMethod();
                excludedPaymentMethod.setId(paymentMethodId);
                excludedPaymentMethods.add(excludedPaymentMethod);
            }
        }
    }

    public void setExcludedPaymentTypeIds(List<String> excludedPaymentTypeIds) {
        if (excludedPaymentTypeIds != null) {
            excludedPaymentTypes = new ArrayList<>();
            for (String paymentTypeId : excludedPaymentTypeIds) {
                PaymentType excludedPaymentType = new PaymentType();
                excludedPaymentType.setId(paymentTypeId);
                excludedPaymentTypes.add(excludedPaymentType);
            }
        }
    }

    public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }

    public void setDefaultPaymentTypeId(String defaultPaymentTypeId) {
        this.defaultPaymentTypeId = defaultPaymentTypeId;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public Integer getDefaultInstallments() {
        return defaultInstallments;
    }

    public List<String> getExcludedPaymentMethodIds() {
        if (excludedPaymentMethods != null) {
            List<String> excludedPaymentMethodIds = new ArrayList<>();
            for (PaymentMethod paymentMethod : excludedPaymentMethods) {
                excludedPaymentMethodIds.add(paymentMethod.getId());
            }
            return excludedPaymentMethodIds;
        } else
            return null;
    }

    public List<String> getExcludedPaymentTypes() {
        if (excludedPaymentTypes != null) {
            List<String> excludedPaymentTypeIds = new ArrayList<>();
            for (PaymentType paymentType : excludedPaymentTypes) {
                excludedPaymentTypeIds.add(paymentType.getId());
            }
            return excludedPaymentTypeIds;
        } else
            return null;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public String getDefaultPaymentTypeId() {
        return defaultPaymentTypeId;
    }

    public List<PayerCost> getInstallmentsBelowMax(List<PayerCost> payerCosts) {

        List<PayerCost> validPayerCosts = new ArrayList<>();

        if (maxInstallments != null) {
            for (PayerCost currentPayerCost : payerCosts) {
                if (currentPayerCost.getInstallments() <= maxInstallments) {
                    validPayerCosts.add(currentPayerCost);
                }
            }
            return validPayerCosts;
        } else {
            return payerCosts;
        }

    }

    public PayerCost getDefaultInstallments(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = null;

        for (PayerCost currentPayerCost : payerCosts) {
            if (currentPayerCost.getInstallments().equals(defaultInstallments)) {
                defaultPayerCost = currentPayerCost;
                break;
            }
        }

        return defaultPayerCost;
    }

    public List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {
        List<PaymentMethod> supportedPaymentMethods = new ArrayList<>();
        if (paymentMethods != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (isPaymentMethodSupported(paymentMethod)) {
                    supportedPaymentMethods.add(paymentMethod);
                }
            }
        }
        return supportedPaymentMethods;
    }

    public boolean isPaymentMethodSupported(PaymentMethod paymentMethod) {
        boolean isSupported = true;
        if (paymentMethod == null) {
            isSupported = false;
        } else {
            List<String> excludedPaymentMethodIds = getExcludedPaymentMethodIds();
            List<String> excludedPaymentTypes = getExcludedPaymentTypes();

            if ((excludedPaymentMethodIds != null && excludedPaymentMethodIds.contains(paymentMethod.getId()))
                    || (excludedPaymentTypes != null && excludedPaymentTypes.contains(paymentMethod.getPaymentTypeId()))) {
                isSupported = false;
            }
        }
        return isSupported;
    }

    public PaymentMethod getDefaultPaymentMethod(List<PaymentMethod> paymentMethods) {
        PaymentMethod defaultPaymentMethod = null;
        if (defaultPaymentMethodId != null && paymentMethods != null) {
            for (PaymentMethod pm : paymentMethods) {
                if (pm.getId().equals(defaultPaymentMethodId)) {
                    defaultPaymentMethod = pm;
                    break;
                }
            }
        }
        return defaultPaymentMethod;
    }

    public boolean installmentPreferencesValid() {
        return validDefaultInstallments() && validMaxInstallments();
    }

    public boolean excludedPaymentTypesValid() {
        return excludedPaymentTypes == null
                || excludedPaymentTypes.size() < PaymentTypes.getAllPaymentTypes().size();
    }

    public boolean validDefaultInstallments() {
        return defaultInstallments == null || defaultInstallments > 0;
    }

    public boolean validMaxInstallments() {
        return maxInstallments == null || maxInstallments > 0;
    }

    public List<Card> getValidCards(List<Card> cards) {
        List<Card> supportedCards = new ArrayList<>();
        if(cards != null) {
            for(Card card : cards) {
                if(isPaymentMethodSupported(card.getPaymentMethod())) {
                    supportedCards.add(card);
                }
            }
        }
        return supportedCards;
    }
}
