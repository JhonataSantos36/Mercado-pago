package com.mercadopago.model;

public interface CardInformation {

    public static int CARD_NUMBER_MAX_LENGTH = 16;

    Integer getExpirationMonth();

    Integer getExpirationYear();

    Cardholder getCardHolder();

    String getLastFourDigits();

    String getFirstSixDigits();

    Integer getSecurityCodeLength();
}
