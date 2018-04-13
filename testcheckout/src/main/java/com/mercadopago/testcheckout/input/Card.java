package com.mercadopago.testcheckout.input;


public class Card {

    private final String escNumber;
    private final String cardHolderName;
    private final String cardNumber;
    private final String cardHolderIdentityNumber;
    private final String expDate;

    public Card(final String escNumber,
                final String cardHolderName,
                final String cardNumber,
                final String cardHolderIdentityNumber,
                final String expDate) {

        this.escNumber = escNumber;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardHolderIdentityNumber = cardHolderIdentityNumber;
        this.expDate = expDate;
    }

    public String cardNumber() {
        return cardNumber;
    }

    public String cardHolderName() {
        return cardHolderName;
    }

    public String escNumber() {
        return escNumber;
    }

    public String cardHolderIdentityNumber() {
        return cardHolderIdentityNumber;
    }

    public String expDate() {
        return expDate;
    }
}
