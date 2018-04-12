package com.mercadopago.model;

import com.mercadopago.constants.PaymentTypes;

import java.math.BigDecimal;

import static com.mercadopago.util.TextUtil.isEmpty;

public class PaymentData {
    private BigDecimal transactionAmount;
    private PaymentMethod paymentMethod;
    private Issuer issuer;
    private PayerCost payerCost;
    private Token token;
    private Discount discount;
    private Payer payer;

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

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public boolean isComplete() {
        return !hasEntityTypeAvailable() || !hasPayerInfoAvailable() || isAccountMoney() || !paymentMethod.isOnline() || isOneInstallment() || !isCreditCardInfoAvailable();
    }

    private boolean hasEntityTypeAvailable() {
        return paymentMethod.isEntityTypeRequired() && payer.getType() == null;
    }

    private boolean hasPayerInfoAvailable() {
        return paymentMethod.isPayerInfoRequired() && payer.getIdentification() == null;
    }

    private boolean isAccountMoney() {
        return paymentMethod.getId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    private boolean isOneInstallment() {
        return !isEmpty(paymentMethod.getPaymentTypeId()) && token != null && payerCost == null && (paymentMethod.getPaymentTypeId().equals(PaymentTypes.DEBIT_CARD)
                || paymentMethod.getPaymentTypeId().equals(PaymentTypes.PREPAID_CARD));
    }

    private boolean isCreditCardInfoAvailable() {
        return !isEmpty(paymentMethod.getPaymentTypeId()) && (token == null || payerCost == null) && paymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD);
    }
}
