package com.mercadopago.providers;

import com.mercadopago.model.Installment;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 4/18/17.
 */

public interface CardVaultProvider extends ResourcesProvider {

    String getMissingPublicKeyErrorMessage();

    String getMissingSiteErrorMessage();

    String getMissingAmountErrorMessage();

    String getMissingPayerCostsErrorMessage();

    String getMissingInstallmentsForIssuerErrorMessage();

    String getMultipleInstallmentsForIssuerErrorMessage();

    void getInstallmentsAsync(final String bin, final Long issuerId, final String paymentMethodId, final BigDecimal amount, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback);

    void createESCTokenAsync(final SavedESCCardToken escCardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    String findESCSaved(String cardId);

    void deleteESC(String cardId);
}
