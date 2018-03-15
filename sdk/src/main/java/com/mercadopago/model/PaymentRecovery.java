package com.mercadopago.model;

public class PaymentRecovery {
    private Token mToken;
    private String mStatusDetail;
    private PaymentMethod mPaymentMethod;
    private PayerCost mPayerCost;
    private Issuer mIssuer;

    public PaymentRecovery(Token token,
                           PaymentMethod paymentMethod,
                           PayerCost payerCost,
                           Issuer issuer,
                           String paymentStatus,
                           String paymentStatusDetail) {

        validate(token, paymentMethod, payerCost, issuer, paymentStatus, paymentStatusDetail);
        mToken = token;
        mPaymentMethod = paymentMethod;
        mPayerCost = payerCost;
        mIssuer = issuer;
        mStatusDetail = paymentStatusDetail;
    }

    private void validate(Token token, PaymentMethod paymentMethod, PayerCost payerCost, Issuer issuer, String paymentStatus, String paymentStatusDetail) {
        if (token == null) {
            throw new IllegalStateException("token is null");
        }

        if (paymentMethod == null) {
            throw new IllegalStateException("payment method is null");
        }

        if (payerCost == null) {
            throw new IllegalStateException("payer cost is null");
        }

        if (issuer == null) {
            throw new IllegalStateException("issuer is null");
        }

        if (!Payment.StatusDetail.isRecoverablePaymentStatus(paymentStatus, paymentStatusDetail)) {
            throw new IllegalStateException("this payment is not recoverable");
        }
    }

    public Token getToken() {
        return mToken;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public PayerCost getPayerCost() {
        return mPayerCost;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public boolean isTokenRecoverable() {
        return Payment.StatusDetail.isStatusDetailRecoverable(mStatusDetail);
    }

    public boolean isStatusDetailCallForAuthorize() {
        return mStatusDetail != null && Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE.equals(mStatusDetail);
    }

    public boolean isStatusDetailCardDisabled() {
        return mStatusDetail != null && Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED.equals(mStatusDetail);
    }

    public boolean isStatusDetailInvalidESC() {
        return mStatusDetail != null && Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC.equals(mStatusDetail);
    }

}
