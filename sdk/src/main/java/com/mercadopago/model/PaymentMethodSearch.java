package com.mercadopago.model;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch implements Serializable{

    private List<PaymentMethodSearchItem> groups;

    private List<PaymentMethod> paymentMethods;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public boolean hasSearchItems() {
        return this.groups != null;
    }

    public PaymentMethod getPaymentMethodBySearchItem(PaymentMethodSearchItem item) {
        return getPaymentMethodById(item.getId());
    }

    public PaymentMethod getPaymentMethodById(String paymentMethodId) {
        PaymentMethod requiredPaymentMethod = null;
        if(paymentMethods != null && paymentMethodId != null) {
            for (PaymentMethod currentPaymentMethod : paymentMethods) {
                if (paymentMethodId.contains(currentPaymentMethod.getId())) {
                    //TODO cuando de payment methods cambien los ids y agreguen bank_transfer, ticket, etc borrar el set.
                    currentPaymentMethod.setId(paymentMethodId);
                    requiredPaymentMethod = currentPaymentMethod;
                    break;
                }
            }
        }
        return requiredPaymentMethod;
    }

}
