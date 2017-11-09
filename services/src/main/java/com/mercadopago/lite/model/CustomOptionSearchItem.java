package com.mercadopago.lite.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mromar on 10/20/17.
 */

public class CustomOptionSearchItem {

    private String id;
    private String description;
    @SerializedName("payment_type_id")
    private String paymentTypeId;
    private String paymentMethodId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
