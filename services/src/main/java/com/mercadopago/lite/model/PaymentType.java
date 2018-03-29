package com.mercadopago.lite.model;

public class PaymentType {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentType() {
    }

    public PaymentType(String paymentTypeId) {
        id = paymentTypeId;
    }
}
