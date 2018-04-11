package com.mercadopago.providers;

import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
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
