package com.mercadopago.model;

import java.io.Serializable;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentType implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
