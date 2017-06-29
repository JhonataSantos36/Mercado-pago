package com.mercadopago.model;

/**
 * Created by vaserber on 10/24/16.
 */

public class CardInfo {

    private Integer cardNumberLength;
    private String lastFourDigits;
    private String firstSixDigits;

    public CardInfo(CardToken cardToken) {
        this.cardNumberLength = cardToken.getCardNumber().length();
        this.lastFourDigits = cardToken.getCardNumber().substring(cardNumberLength - 4, cardNumberLength);
        this.firstSixDigits = cardToken.getCardNumber().substring(0, 6);
    }

    public CardInfo(Token token) {
        this.cardNumberLength = token.getCardNumberLength();
        this.lastFourDigits = token.getLastFourDigits();
        this.firstSixDigits = token.getFirstSixDigits();
    }

    public CardInfo(Card card) {
        this.lastFourDigits = card.getLastFourDigits();
        this.firstSixDigits = card.getFirstSixDigits();
    }

    public static boolean canCreateCardInfo(Token token) {
        return token.getCardNumberLength() != null && token.getLastFourDigits() != null
                && token.getFirstSixDigits() != null;
    }

    public Integer getCardNumberLength() {
        return cardNumberLength;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public String getFirstSixDigits() {
        return firstSixDigits;
    }
}
