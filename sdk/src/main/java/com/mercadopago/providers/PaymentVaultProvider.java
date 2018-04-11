package com.mercadopago.providers;

import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.lite.preferences.PaymentPreference;
import android.support.annotation.NonNull;

import java.math.BigDecimal;

/**
 * Created by mreverter on 1/30/17.
 */

public interface PaymentVaultProvider extends ResourcesProvider {
    String getTitle();

    void getPaymentMethodSearch(BigDecimal amount, PaymentPreference paymentPreference, Payer payer, Site site, TaggedCallback<PaymentMethodSearch> taggedCallback);

    void getDirectDiscount(String amount, String payerEmail, TaggedCallback<Discount> taggedCallback);

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
