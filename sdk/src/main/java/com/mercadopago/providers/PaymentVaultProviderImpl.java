package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 1/30/17.
 */

public class PaymentVaultProviderImpl implements PaymentVaultProvider {
    private final Context context;
    private final MercadoPago mercadoPago;
    private final String merchantBaseUrl;
    private final String merchantGetCustomerUri;
    private final String merchantAccessToken;

    public PaymentVaultProviderImpl(Context context, String publicKey, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken) {
        this.context = context;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantGetCustomerUri = merchantGetCustomerUri;
        this.merchantAccessToken = merchantAccessToken;
        this.mercadoPago = new MercadoPago.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }

    @Override
    public void getDirectDiscount(String amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getDirectDiscount(amount, payerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MPException(apiException));
            }
        });
    }

    @Override
    public void getPaymentMethodSearch(BigDecimal amount, final PaymentPreference paymentPreference, Payer payer, Boolean accountMoneyEnabled, final OnResourcesRetrievedCallback<PaymentMethodSearch> onResourcesRetrievedCallback) {

        List<String> excludedPaymentTypes = paymentPreference == null ? null : paymentPreference.getExcludedPaymentTypes();
        List<String> excludedPaymentMethodIds = paymentPreference == null ? null : paymentPreference.getExcludedPaymentMethodIds();
        mercadoPago.getPaymentMethodSearch(amount, excludedPaymentTypes, excludedPaymentMethodIds, payer, accountMoneyEnabled, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                if (!paymentMethodSearch.hasSavedCards() && isMerchantServerInfoAvailable()) {
                    addCustomerCardsFromMerchantServer(paymentMethodSearch, paymentPreference, onResourcesRetrievedCallback);
                } else {
                    onResourcesRetrievedCallback.onSuccess(paymentMethodSearch);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MPException(apiException));
            }
        });
    }

    private void addCustomerCardsFromMerchantServer(final PaymentMethodSearch paymentMethodSearch, final PaymentPreference paymentPreference, final OnResourcesRetrievedCallback<PaymentMethodSearch> onResourcesRetrievedCallback) {
        MerchantServer.getCustomer(context, merchantBaseUrl, merchantGetCustomerUri, merchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                List<Card> savedCards = paymentPreference == null ? customer.getCards() : paymentPreference.getValidCards(customer.getCards());
                paymentMethodSearch.addCards(savedCards, context.getString(R.string.mpsdk_last_digits_label));
                onResourcesRetrievedCallback.onSuccess(paymentMethodSearch);
            }

            @Override
            public void failure(ApiException apiException) {
                //Avoid failure caused by merchant's server
                onResourcesRetrievedCallback.onSuccess(paymentMethodSearch);
            }
        });
    }

    @Override
    public String getTitle() {
        return context.getString(R.string.mpsdk_title_activity_payment_vault);
    }

    @Override
    public String getInvalidSiteConfigurationErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_invalid_currency);
    }

    @Override
    public String getInvalidAmountErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_invalid_amount);
    }

    @Override
    public String getAllPaymentTypesExcludedErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_excluded_all_payment_type);
    }

    @Override
    public String getInvalidDefaultInstallmentsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_invalid_default_installments);
    }

    @Override
    public String getInvalidMaxInstallmentsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_invalid_max_installments);
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }

    @Override
    public String getEmptyPaymentMethodsErrorMessage() {
        return context.getString(R.string.mpsdk_no_payment_methods_found);
    }

    private boolean isMerchantServerInfoAvailable() {
        return merchantBaseUrl != null && !merchantBaseUrl.isEmpty() && merchantGetCustomerUri != null && !merchantGetCustomerUri.isEmpty();
    }
}
