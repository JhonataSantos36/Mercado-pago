package com.mercadopago.model;

/**
 * Created by mreverter on 15/9/16.
 */
public interface CardInformation {
    Integer getExpirationMonth();
    Integer getExpirationYear();
    Cardholder getCardHolder();
    String getLastFourDigits();
    String getFirstSixDigits();
    Integer getSecurityCodeLength();
}
