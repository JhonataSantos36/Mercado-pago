package com.mercadopago.model;

import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch {

    private List<PaymentMethodSearchItem> groups;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public void setGroups(List<PaymentMethodSearchItem> groups) {
        this.groups = groups;
    }
}
