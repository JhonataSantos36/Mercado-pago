package com.mercadopago.mocks;

/**
 * Created by vaserber on 8/24/17.
 */

public class DummyCard {

    private String paymentMethod;
    private String cardNumber;
    private String securityCode;
    private String numberWithMask;

    public DummyCard(String paymentMethod, String cardNumber, String securityCode, String numberWithMask) {
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.securityCode = securityCode;
        this.numberWithMask = numberWithMask;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getNumberWithMask() {
        return numberWithMask;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setNumberWithMask(String numberWithMask) {
        this.numberWithMask = numberWithMask;
    }
}
