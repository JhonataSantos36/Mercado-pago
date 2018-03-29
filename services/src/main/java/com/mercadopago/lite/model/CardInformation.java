package com.mercadopago.lite.model;

public interface CardInformation {

    int CARD_NUMBER_MAX_LENGTH = 16;

    Integer getExpirationMonth();

    Integer getExpirationYear();

    Cardholder getCardHolder();

    String getLastFourDigits();

    String getFirstSixDigits();

    Integer getSecurityCodeLength();
}
