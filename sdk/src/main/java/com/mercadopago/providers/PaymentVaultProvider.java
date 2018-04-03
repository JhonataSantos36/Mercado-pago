package com.mercadopago.providers;

import android.support.annotation.NonNull;

import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.preferences.PaymentPreference;

import java.math.BigDecimal;

/**
 * Created by mreverter on 1/30/17.
 */

public interface PaymentVaultProvider extends ResourcesProvider {
    String getTitle();

    void getPaymentMethodSearch(BigDecimal amount, PaymentPreference paymentPreference, Payer payer, Site site, OnResourcesRetrievedCallback<PaymentMethodSearch> onResourcesRetrievedCallback);

    void getDirectDiscount(String amount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    String getInvalidSiteConfigurationErrorMessage();

    String getInvalidAmountErrorMessage();

    String getAllPaymentTypesExcludedErrorMessage();

    String getInvalidDefaultInstallmentsErrorMessage();

    String getInvalidMaxInstallmentsErrorMessage();

    String getStandardErrorMessage();

    String getEmptyPaymentMethodsErrorMessage();

    void trackInitialScreen(PaymentMethodSearch paymentMethodSearch, String siteId);

    void trackChildrenScreen(@NonNull PaymentMethodSearchItem paymentMethodSearchItem, @NonNull String siteId);

}
