package com.mercadopago.providers;

import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
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

    void cloneToken(final String tokenId, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    void putSecurityCode(String securityCode, String tokenId, OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    void createToken(final SavedCardToken savedCardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    void createToken(final SavedESCCardToken savedESCCardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    void validateSecurityCodeFromToken(String mSecurityCode, PaymentMethod mPaymentMethod, String firstSixDigits) throws Exception;

    void validateSecurityCodeFromToken(String mSecurityCode);

    void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws Exception;

    boolean isESCEnabled();

}
