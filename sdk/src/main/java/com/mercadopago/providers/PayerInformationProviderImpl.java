package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.util.ApiUtil;

import java.util.List;

/**
 * Created by mromar on 22/09/17.
 */

public class PayerInformationProviderImpl implements PayerInformationProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public PayerInformationProviderImpl(Context context, String publicKey, String payerAccessToken) {
        this.context = context;
        this.mercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(payerAccessToken)
                .build();
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_number);
    }

    @Override
    public String getInvalidIdentificationNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_name);
    }

    @Override
    public String getInvalidIdentificationLastNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_last_name);
    }

    @Override
    public String getInvalidIdentificationBusinessNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_last_name);
    }

    @Override
    public void getIdentificationTypesAsync(final OnResourcesRetrievedCallback<List<IdentificationType>> onResourcesRetrievedCallback) {
        mercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {
                onResourcesRetrievedCallback.onSuccess(identificationTypes);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES));
            }
        });
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_identification_types);
    }
}
