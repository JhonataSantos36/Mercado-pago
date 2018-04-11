package com.mercadopago.providers;

import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.tracker.MPTrackingContext;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 8/24/17.
 */

public interface GuessingCardProvider extends ResourcesProvider {

    MPTrackingContext getTrackingContext();

    void getPaymentMethodsAsync(final TaggedCallback<List<PaymentMethod>> taggedCallback);

    void createTokenAsync(CardToken cardToken, final TaggedCallback<Token> taggedCallback);

    void getIssuersAsync(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback);

    void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final TaggedCallback<List<Installment>> taggedCallback);

    void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback);

    void getBankDealsAsync(final TaggedCallback<List<BankDeal>> taggedCallback);

    String getMissingInstallmentsForIssuerErrorMessage();

    String getMultipleInstallmentsForIssuerErrorMessage();

    String getMissingPayerCostsErrorMessage();

    String getMissingIdentificationTypesErrorMessage();

    String getMissingPublicKeyErrorMessage();

    String getInvalidIdentificationNumberErrorMessage();

    String getInvalidExpiryDateErrorMessage();

    String getInvalidEmptyNameErrorMessage();

    String getSettingNotFoundForBinErrorMessage();

    String getInvalidFieldErrorMessage();
}
