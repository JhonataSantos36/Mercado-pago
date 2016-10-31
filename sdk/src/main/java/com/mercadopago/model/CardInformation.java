package com.mercadopago.model;

/**
 * Created by mreverter on 15/9/16.
 */
public interface CardInformation {

    public static int CARD_NUMBER_MAX_LENGTH = 16;

    Integer getExpirationMonth();
    Integer getExpirationYear();
    Cardholder getCardHolder();
    String getLastFourDigits();
    String getFirstSixDigits();
    Integer getSecurityCodeLength();
}
