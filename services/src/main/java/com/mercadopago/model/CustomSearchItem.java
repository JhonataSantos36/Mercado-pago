package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;

public class CustomSearchItem {
    private String description;
    private String id;

    @SerializedName("payment_type_id")
    private String type;
    private String paymentMethodId;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
