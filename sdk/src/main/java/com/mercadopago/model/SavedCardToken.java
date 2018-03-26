package com.mercadopago.model;

import android.content.Context;
import android.text.TextUtils;

import com.mercadopago.exceptions.CardTokenException;

public class SavedCardToken {

    private String cardId;
    private String securityCode;
    private Device device;

    public SavedCardToken(String cardId, String securityCode) {
        this.cardId = cardId;
        this.securityCode = securityCode;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Context context) {
        device = new Device(context);
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardNumber) {
        cardId = cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public boolean validate() {
        return validateCardId() && validateSecurityCode();
    }

    public boolean validateCardId() {
        return !TextUtils.isEmpty(cardId) && TextUtils.isDigitsOnly(cardId);
    }

    public boolean validateSecurityCode() {

        return CardToken.validateSecurityCode(securityCode);
    }

    public void validateSecurityCode(Card card) throws CardTokenException {

        // Validate security code length
        if (securityCode != null) {
            int cvvLength = (card.getSecurityCode() != null) ? card.getSecurityCode().getLength() : 0;
            if ((cvvLength != 0) && (securityCode.trim().length() != cvvLength)) {
                throw new CardTokenException(CardTokenException.INVALID_CVV_LENGTH, String.valueOf(cvvLength));
            }
        } else {
            throw new CardTokenException(CardTokenException.INVALID_FIELD);
        }
    }

}
