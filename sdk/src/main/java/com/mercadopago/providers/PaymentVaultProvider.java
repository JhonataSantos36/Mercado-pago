package com.mercadopago.providers;

import com.mercadopago.model.Campaign;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 1/30/17.
 */

public interface PaymentVaultProvider extends ResourcesProvider {
    String getTitle();

    void getPaymentMethodSearch(BigDecimal amount, PaymentPreference paymentPreference, Payer payer, Boolean accountMoneyEnabled, OnResourcesRetrievedCallback<PaymentMethodSearch> onResourcesRetrievedCallback);

    void getDirectDiscount(String amount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getCampaigns(OnResourcesRetrievedCallback<List<Campaign>> onResourcesRetrievedCallback);

    String getInvalidSiteConfigurationErrorMessage();

    String getInvalidAmountErrorMessage();

    String getAllPaymentTypesExcludedErrorMessage();

    String getInvalidDefaultInstallmentsErrorMessage();

    String getInvalidMaxInstallmentsErrorMessage();

    String getStandardErrorMessage();

    String getEmptyPaymentMethodsErrorMessage();
}
