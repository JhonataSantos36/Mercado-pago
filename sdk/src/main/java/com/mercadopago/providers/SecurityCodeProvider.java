package com.mercadopago.providers;

import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.lite.model.Card;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.SavedCardToken;
import com.mercadopago.lite.model.SavedESCCardToken;
import com.mercadopago.lite.model.Token;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by marlanti on 7/18/17.
 */

public interface SecurityCodeProvider extends ResourcesProvider {

    String getStandardErrorMessageGotten();

    String getTokenAndCardNotSetMessage();

    String getPaymentMethodNotSetMessage();

    String getCardInfoNotSetMessage();

    String getTokenAndCardWithoutRecoveryCantBeBothSetMessage();

    void cloneToken(final String tokenId, final TaggedCallback<Token> taggedCallback);

    void putSecurityCode(String securityCode, String tokenId, TaggedCallback<Token> taggedCallback);

    void createToken(final SavedCardToken savedCardToken, final TaggedCallback<Token> taggedCallback);

    void createToken(final SavedESCCardToken savedESCCardToken, final TaggedCallback<Token> taggedCallback);

    void validateSecurityCodeFromToken(String mSecurityCode, PaymentMethod mPaymentMethod, String firstSixDigits) throws CardTokenException;

    void validateSecurityCodeFromToken(String mSecurityCode) throws CardTokenException;

    void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws CardTokenException;

    boolean isESCEnabled();

}
