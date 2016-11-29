package com.mercadopago.model;

/**
 * Created by mromar on 8/19/16.
 */
public class PaymentRecovery {
    private Token mToken;
    private Payment mPayment;
    private PaymentMethod mPaymentMethod;
    private PayerCost mPayerCost;
    private Issuer mIssuer;
    private Boolean mIsTokenRecoverable = false;

    public PaymentRecovery(Token token, Payment payment, PaymentMethod paymentMethod, PayerCost payerCost, Issuer issuer){
        if (areNullParameters(token, payment, paymentMethod, issuer)){
            throw new IllegalStateException("parameter can not be null");
        }

        if (!areValidParameters(payment, paymentMethod)){
            throw new IllegalStateException("paymentMethodId of payment is not equal to paymentMethodId of paymentMethod");
        }

        if (!isRecoverablePayment(payment)){
            throw new IllegalStateException("this payment is not recoverable");
        }
        setToken(token);
        setPayment(payment);
        setPaymentMethod(paymentMethod);
        setPayerCost(payerCost);
        setIssuer(issuer);
    }

    private boolean areNullParameters(Token token, Payment payment, PaymentMethod paymentMethod, Issuer issuer){
        return token == null || payment == null || paymentMethod == null || issuer == null;
    }

    private boolean areValidParameters(Payment payment, PaymentMethod paymentMethod){
        return paymentMethod.getId().equals(payment.getPaymentMethodId());
    }

    private boolean isRecoverablePayment(Payment payment){
        return payment.getStatus().equals("rejected") && isPaymentStatusRecoverable(payment);
    }

    private boolean isPaymentStatusRecoverable(Payment payment){
        return payment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                payment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                payment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                payment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                payment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
    }

    public Token getToken() {
        return mToken;
    }

    private void setToken(Token token) {
        this.mToken = token;
    }

    public Payment getPayment() {
        return mPayment;
    }

    private void setPayment(Payment payment) {
        this.mPayment = payment;

        if (isStatusDetailCallForAuthorize())
        {
            setIsTokenRecoverable(true);
        }
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    private void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public PayerCost getPayerCost() {
        return mPayerCost;
    }

    private void setPayerCost(PayerCost payerCost) {
        this.mPayerCost = payerCost;
    }

    public Issuer getIssuer(){
        return mIssuer;
    }

    private void setIssuer(Issuer issuer){
        this.mIssuer = issuer;
    }

    public Boolean isTokenRecoverable() {
        return mIsTokenRecoverable;
    }

    public void setIsTokenRecoverable(Boolean isTokenRecoverable) {
        this.mIsTokenRecoverable = isTokenRecoverable;
    }

    private Boolean isStatusDetailCallForAuthorize(){
        return mPayment.getStatusDetail() != null &&
                mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
    }
}
