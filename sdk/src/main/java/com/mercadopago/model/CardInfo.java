package com.mercadopago.model;

import com.mercadopago.lite.model.Card;
import com.mercadopago.lite.model.CardToken;
import com.mercadopago.lite.model.Token;

/**
 * Created by vaserber on 10/24/16.
 */

public class CardInfo {

    private Integer cardNumberLength;
    private final String lastFourDigits;
    private final String firstSixDigits;

    public CardInfo(CardToken cardToken) {
        cardNumberLength = cardToken.getCardNumber().length();
        lastFourDigits = cardToken.getCardNumber().substring(cardNumberLength - 4, cardNumberLength);
        firstSixDigits = cardToken.getCardNumber().substring(0, 6);
    }

    public CardInfo(Token token) {
        cardNumberLength = token.getCardNumberLength();
        lastFourDigits = token.getLastFourDigits();
        firstSixDigits = token.getFirstSixDigits();
    }

    public CardInfo(Card card) {
        lastFourDigits = card.getLastFourDigits();
        firstSixDigits = card.getFirstSixDigits();
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
