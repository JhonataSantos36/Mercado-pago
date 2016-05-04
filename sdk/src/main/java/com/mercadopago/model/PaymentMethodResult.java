package com.mercadopago.model;

import java.io.Serializable;

/**
 * Created by mreverter on 25/4/16.
 */
public class PaymentMethodResult implements Serializable {

    private PaymentMethod paymentMethod;
    private String recommendedDescription;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRecommendedDescription() {
        return recommendedDescription;
    }

    public void setRecommendedDescription(String recommendedDescription) {
        this.recommendedDescription = recommendedDescription;
    }
}
