package com.mercadopago.model;

import com.mercadopago.constants.PaymentTypes;

import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch {

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
                if (itemMatchesPaymentMethod(item, currentPaymentMethod)) {
                    requiredPaymentMethod = currentPaymentMethod;
                    requiredPaymentMethod.setPaymentTypeId(getPaymentTypeIdFromItem(item, currentPaymentMethod));
                }
            }
        }
        return requiredPaymentMethod;
    }

    private String getPaymentTypeIdFromItem(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {

        String paymentType = "";

        //Remove payment method id from item id
        String potentialPaymentType = item.getId().replace(paymentMethod.getId(), "");
        for(String currentPaymentType : PaymentTypes.getAllPaymentTypes()) {
            if(potentialPaymentType.endsWith(currentPaymentType)){
                paymentType = currentPaymentType;
                break;
            }
        }
        if(paymentType.isEmpty()) {
            paymentType = paymentMethod.getPaymentTypeId();
        }
        return paymentType;
    }

    private boolean itemMatchesPaymentMethod(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {
        return item.getId().startsWith(paymentMethod.getId());
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
