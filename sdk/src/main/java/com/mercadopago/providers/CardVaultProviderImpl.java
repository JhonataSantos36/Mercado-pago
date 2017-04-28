package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 4/18/17.
 */

public class CardVaultProviderImpl implements CardVaultProvider {

    private final Context context;
    private final MercadoPagoServices mercadoPago;

    public CardVaultProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;

        this.mercadoPago = new MercadoPagoServices.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .build();
    }

    @Override
    public String getMultipleInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_multiple_installments_for_issuer);
    }

    @Override
    public String getMissingInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_installment_for_issuer);
    }

    @Override
    public String getMissingPayerCostsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_payer_cost);
    }

    @Override
    public String getMissingAmountErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_amount);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getMissingSiteErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_site);
    }

    @Override
    public void getInstallmentsAsync(final String bin, final Long issuerId, final String paymentMethodId, final BigDecimal amount, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, new Callback<List<Installment>>() {
            @Override
            public void success(List<Installment> installments) {
                onResourcesRetrievedCallback.onSuccess(installments);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }


}
