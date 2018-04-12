package com.mercadopago.model;

import android.support.annotation.NonNull;

public class MerchantPayment {

    private Long cardIssuerId;
    private String cardToken;
    private String campaignId;
    private Integer installments;
    private String paymentMethodId;


    public MerchantPayment(@NonNull PaymentData paymentData) {
        if (paymentData.getPayerCost() != null) {
            installments = paymentData.getPayerCost().getInstallments();
        }

        if (paymentData.getToken() != null) {
            cardToken = paymentData.getToken().getId();
        }

        if (paymentData.getPaymentMethod() != null) {
            paymentMethodId = paymentData.getPaymentMethod().getId();
        }

        if (paymentData.getDiscount() != null) {
            campaignId = paymentData.getDiscount().getId();
        }

        if (paymentData.getIssuer() != null) {
            cardIssuerId = paymentData.getIssuer().getId();
        }
    }

    public Long getCardIssuerId() {
        return cardIssuerId;
    }

    public void setCardIssuerId(Long cardIssuerId) {
        this.cardIssuerId = cardIssuerId;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String card) {
        cardToken = card;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
