package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.util.ApiUtil;

/**
 * Created by marlanti on 7/18/17.
 */

public class SecurityCodeProviderImpl implements SecurityCodeProvider {

    private final Context mContext;
    private final MercadoPagoServices mMercadoPagoServices;

    public SecurityCodeProviderImpl(Context context, String publicKey, String privateKey) {
        this.mContext = context;

        mMercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .build();
    }


    @Override
    public String getStandardErrorMessageGotten() {
        return mContext.getString(R.string.mpsdk_standard_error_message);
    }


    @Override
    public void cloneToken(final String tokenId, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {
        mMercadoPagoServices.cloneToken(tokenId, new Callback<Token>() {
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

        mMercadoPagoServices.putSecurityCode(tokenId, securityCodeIntent, new Callback<Token>() {
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

        mMercadoPagoServices.createToken(savedCardToken, new Callback<Token>() {
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
    public void validateSecurityCodeFromToken(String securityCode, PaymentMethod paymentMethod, String firstSixDigits) throws Exception {
        CardToken.validateSecurityCode(mContext, securityCode, paymentMethod, firstSixDigits);
    }

    @Override
    public void validateSecurityCodeFromToken(String securityCode) {
        CardToken.validateSecurityCode(securityCode);
    }

    @Override
    public void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws Exception {
        savedCardToken.validateSecurityCode(mContext, card);
    }
}
