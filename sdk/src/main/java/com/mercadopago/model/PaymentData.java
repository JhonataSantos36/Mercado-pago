package com.mercadopago.model;

/**
 * Created by mreverter on 1/17/17.
 */
public class PaymentData {
    private PaymentMethod paymentMethod;
    private Issuer issuer;
    private PayerCost payerCost;
    private Token token;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public PayerCost getPayerCost() {
        return payerCost;
    }

    public void setPayerCost(PayerCost payerCost) {
        this.payerCost = payerCost;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
