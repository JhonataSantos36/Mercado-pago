package com.mercadopago.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class CheckoutPreference implements Serializable {

    private String id;
    private List<Item> items;
    private Payer payer;

    public BigDecimal getAmount() {

        BigDecimal totalAmount = BigDecimal.ZERO;
        for(Iterator<Item> i = items.iterator(); i.hasNext(); ) {
            Item item = i.next();
            if ((item != null) && (item.getUnitPrice() != null) && (item.getQuantity() != null)) {
                totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            } else {
                return null;
            }
        }
        return totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }
}
