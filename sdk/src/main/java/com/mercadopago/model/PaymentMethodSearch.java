package com.mercadopago.model;

import java.util.List;
import java.util.Stack;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch {

    private List<PaymentMethodSearchItem> preferred;

    private List<PaymentMethodSearchItem> groups;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public boolean hasPreferred() {
        return this.preferred != null;
    }

    public boolean hasSearchItems() {
        return this.groups != null;
    }

}
