package com.mercadopago.model;

import com.mercadopago.constants.PaymentTypes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch implements Serializable{

    private List<PaymentMethodSearchItem> groups;

    private List<PaymentMethod> paymentMethods;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
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
                    if(PaymentTypes.getAllPaymentTypes().contains(itemPaymentType)) {
                        //MP API v1 not contemplating different payment types for a payment method. Overriding to give consistent instructions.
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

    //PaymentMethodSearchItem id could be the payment method id or its concatenation with the paymentTypeId
    private PaymentMethodSearchItem searchItemInList(List<PaymentMethodSearchItem> list, String potentialItemId, String paymentMethodId) {
        PaymentMethodSearchItem requiredItem = null;
        for(PaymentMethodSearchItem currentItem : list) {

            if(currentItem.getId().equals(paymentMethodId)
                    || currentItem.getId().equals(potentialItemId)) {
                requiredItem = currentItem;
                break;
            }
            else if(currentItem.hasChildren()) {
                requiredItem = searchItemInList(currentItem.getChildren(), potentialItemId, paymentMethodId);
                if(requiredItem != null) {
                    break;
                }
            }
        }
        return requiredItem;
    }
}
