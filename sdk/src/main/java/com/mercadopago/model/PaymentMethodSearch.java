package com.mercadopago.model;

import java.io.Serializable;
import java.util.ArrayList;
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
        return this.groups != null && !this.groups.isEmpty();
    }

    public PaymentMethod getPaymentMethodBySearchItem(PaymentMethodSearchItem item) {
        PaymentMethod requiredPaymentMethod = null;
        if(paymentMethods != null && item != null && item.getId() != null) {
            for (PaymentMethod currentPaymentMethod : paymentMethods) {
                if (item.getId().contains(currentPaymentMethod.getId())) {
                    requiredPaymentMethod = currentPaymentMethod;

                    String itemPaymentType = item.getId().replace(currentPaymentMethod.getId() + "_", "");
                    if(PaymentType.getAllPaymentTypes().contains(itemPaymentType)) {
                        requiredPaymentMethod.setPaymentTypeId(itemPaymentType);
                    }
                    break;
                }
            }
        }
        return requiredPaymentMethod;
    }

    public PaymentMethodSearchItem getSearchItemByPaymentMethod(PaymentMethod selectedPaymentMethod) {
        PaymentMethodSearchItem requiredItem = null;
        if(selectedPaymentMethod != null) {

            requiredItem = searchItemMatchingPaymentMethod(selectedPaymentMethod);

        }
        return requiredItem;
    }

    private PaymentMethodSearchItem searchItemMatchingPaymentMethod(PaymentMethod paymentMethod) {
        String potentialItemId = paymentMethod.getId() + "_" + paymentMethod.getPaymentTypeId();
        String paymentMethodId = paymentMethod.getId();
        return searchItemInList(groups, potentialItemId, paymentMethodId);
    }

    private PaymentMethodSearchItem searchItemInList(List<PaymentMethodSearchItem> list, String potentialItemId, String paymentMethodId) {
        PaymentMethodSearchItem requiredItem = null;
        for(PaymentMethodSearchItem currentItem : list) {
            if(currentItem.getId().equals(paymentMethodId)
                    || currentItem.getId().equals(potentialItemId)) {
                requiredItem = currentItem;
                break;
            }
        }
        if(requiredItem == null) {
            for(PaymentMethodSearchItem currentItem : list) {
                if(currentItem.hasChildren()) {
                    requiredItem = searchItemInList(currentItem.getChildren(), potentialItemId, paymentMethodId);
                    if(requiredItem != null) {
                        break;
                    }
                }
            }
        }
        return requiredItem;
    }
}
