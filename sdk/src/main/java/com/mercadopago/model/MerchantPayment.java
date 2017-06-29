package com.mercadopago.model;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

public class MerchantPayment {

    private Payer payer;
    private Long cardIssuerId;
    private String cardToken;
    private Long campaignId;
    private Integer installments;
    private String paymentMethodId;
    private BigDecimal transactionAmount;

    public MerchantPayment(BigDecimal transactionAmount, Integer installments, Long cardIssuerId, String cardToken,
                           String paymentMethodId, Long campaignId) {
        this.transactionAmount = transactionAmount;
        this.installments = installments;
        this.cardIssuerId = cardIssuerId;
        this.cardToken = cardToken;
        this.paymentMethodId = paymentMethodId;
        this.campaignId = campaignId;
    }

    public MerchantPayment(@NonNull PaymentData paymentData) {
        if (paymentData.getPayerCost() != null) {
            this.installments = paymentData.getPayerCost().getInstallments();
        }

        if (paymentData.getToken() != null) {
            this.cardToken = paymentData.getToken().getId();
        }

        if (paymentData.getPaymentMethod() != null) {
            this.paymentMethodId = paymentData.getPaymentMethod().getId();
        }

        if (paymentData.getDiscount() != null) {
            this.campaignId = paymentData.getDiscount().getId();
        }

        if (paymentData.getIssuer() != null) {
            this.cardIssuerId = paymentData.getIssuer().getId();
        }

        this.payer = paymentData.getPayer();
        this.transactionAmount = paymentData.getTransactionAmount();
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
        this.cardToken = card;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
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
