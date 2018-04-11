package com.mercadopago.model;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;

/**
 * Created by mreverter on 1/17/17.
 */
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
        //TODO refactor
        if (paymentMethod.isEntityTypeRequired() && payer.getType() == null) {
            return false;
        }

        if (paymentMethod.isPayerInfoRequired() && payer.getIdentification() == null) {
            return false;
        }


        //TODO
        /*
        if (!Array.isNullOrEmpty(paymentMethod.financialInstitutions) && transactionDetails?.financialInstitution == null) {
            return false;
        }
        */

        if (paymentMethod.getId().equals(PaymentTypes.ACCOUNT_MONEY) || !paymentMethod.isOnline()) {
            return true;
        }

        if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId()) && (token == null || payerCost == null)) {
            return paymentMethod.getPaymentTypeId().equals(PaymentTypes.DEBIT_CARD) || paymentMethod.getPaymentTypeId().equals(PaymentTypes.PREPAID_CARD) && token != null;
        }

        return true;
    }
}
