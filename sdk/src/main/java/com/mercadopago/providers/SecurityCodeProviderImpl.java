package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.MercadoPagoESC;
import com.mercadopago.util.MercadoPagoESCImpl;


/**
 * Created by marlanti on 7/18/17.
 */

public class SecurityCodeProviderImpl implements SecurityCodeProvider {

    private final Context mContext;
    private final MercadoPagoServicesAdapter mMercadoPagoServicesAdapter;
    private MercadoPagoESC mercadoPagoESC;

    private static final String TOKEN_AND_CARD_NOT_SET_MESSAGE = "token and card can't both be null";
    private static final String TOKEN_AND_CARD_WITHOUT_RECOVERY_SET_MESSAGE = "can't set token and card at the same time without payment recovery";
    private static final String PAYMENT_METHOD_NOT_SET = "payment method not set";
    private static final String CARD_INFO_NOT_SET = "card info can't be null";

    public SecurityCodeProviderImpl(Context context, String publicKey, String privateKey, boolean escEnabled) {
        this.mContext = context;

        mMercadoPagoServicesAdapter = new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .build();

        this.mercadoPagoESC = new MercadoPagoESCImpl(context, escEnabled);
    }

    @Override
    public boolean isESCEnabled() {
        return mercadoPagoESC.isESCEnabled();
    }

    @Override
    public String getStandardErrorMessageGotten() {
        return mContext.getString(R.string.mpsdk_standard_error_message);
    }

    @Override
    public String getCardInfoNotSetMessage() {
        return CARD_INFO_NOT_SET;
    }

    @Override
    public String getPaymentMethodNotSetMessage() {
        return PAYMENT_METHOD_NOT_SET;
    }

    @Override
    public String getTokenAndCardWithoutRecoveryCantBeBothSetMessage() {
        return TOKEN_AND_CARD_WITHOUT_RECOVERY_SET_MESSAGE;
    }

    @Override
    public String getTokenAndCardNotSetMessage() {
        return TOKEN_AND_CARD_NOT_SET_MESSAGE;
    }

    @Override
    public void cloneToken(final String tokenId, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {
        mMercadoPagoServicesAdapter.cloneToken(tokenId, new Callback<Token>() {
            @Override
            public void success(Token token) {
                onResourcesRetrievedCallback.onSuccess(token);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_TOKEN));

            }
        });
    }

    @Override
    public void putSecurityCode(final String securityCode, final String tokenId, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {
        SecurityCodeIntent securityCodeIntent = new SecurityCodeIntent();
        securityCodeIntent.setSecurityCode(securityCode);

        mMercadoPagoServicesAdapter.putSecurityCode(tokenId, securityCodeIntent, new Callback<Token>() {
            @Override
            public void success(Token token) {
                onResourcesRetrievedCallback.onSuccess(token);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_TOKEN));
            }
        });
    }

    @Override
    public void createToken(final SavedCardToken savedCardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {

        mMercadoPagoServicesAdapter.createToken(savedCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                onResourcesRetrievedCallback.onSuccess(token);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_TOKEN));
            }
        });

    }

    @Override
    public void createToken(SavedESCCardToken savedESCCardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {

        mMercadoPagoServicesAdapter.createToken(savedESCCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                onResourcesRetrievedCallback.onSuccess(token);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_TOKEN));
            }
        });
    }

    @Override
    public void validateSecurityCodeFromToken(String securityCode, PaymentMethod paymentMethod, String firstSixDigits) throws CardTokenException {
        CardToken.validateSecurityCode(securityCode, paymentMethod, firstSixDigits);
    }

    @Override
    public void validateSecurityCodeFromToken(String securityCode) throws CardTokenException {
        if (!CardToken.validateSecurityCode(securityCode)) {
            throw new CardTokenException(CardTokenException.INVALID_FIELD);
        }
    }

    @Override
    public void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws CardTokenException {
        savedCardToken.validateSecurityCode(card);
    }
}
