package com.mercadopago.providers;

import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.tracker.MPTrackingContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 8/24/17.
 */

public interface GuessingCardProvider extends ResourcesProvider {

    MPTrackingContext getTrackingContext();

    void getPaymentMethodsAsync(final OnResourcesRetrievedCallback<List<PaymentMethod>> onResourcesRetrievedCallback);

    void createTokenAsync(CardToken cardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback);

    void getIssuersAsync(String paymentMethodId, String bin, final OnResourcesRetrievedCallback<List<Issuer>> onResourcesRetrievedCallback);

    void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback);

    void getIdentificationTypesAsync(final OnResourcesRetrievedCallback<List<IdentificationType>> onResourcesRetrievedCallback);

    void getBankDealsAsync(final OnResourcesRetrievedCallback<List<BankDeal>> onResourcesRetrievedCallback);

    void getDirectDiscountAsync(String transactionAmount, String payerEmail, String merchantDiscountUrl, String merchantDiscountUri, Map<String, String> discountAdditionalInfo, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getMPDirectDiscount(String transactionAmount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

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
