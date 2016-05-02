package com.mercadopago.model;

import java.io.Serializable;

public class PaymentIntent implements Serializable {

    private Integer installments;
    private Long issuerId;
    private String paymentMethodId;
    private String prefId;
    private String token;
    private String publicKey;
    private String email;

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPrefId() {
        return prefId;
    }

    public void setPrefId(String prefId) {
        this.prefId = prefId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
