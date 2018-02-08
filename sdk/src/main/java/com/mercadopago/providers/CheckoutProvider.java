package com.mercadopago.providers;

import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.preferences.CheckoutPreference;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 2/2/17.
 */

public interface CheckoutProvider extends ResourcesProvider {
    void getCheckoutPreference(String checkoutPreferenceId, OnResourcesRetrievedCallback<CheckoutPreference> onResourcesRetrievedCallback);

    void getDiscountCampaigns(OnResourcesRetrievedCallback<List<Campaign>> callback);

    void getDirectDiscount(BigDecimal amount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, Site site, OnResourcesRetrievedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, OnResourcesRetrievedCallback<Customer> onCustomerRetrievedCallback);

    String getCheckoutExceptionMessage(CheckoutPreferenceException exception);

    String getCheckoutExceptionMessage(IllegalStateException exception);

    void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, OnResourcesRetrievedCallback<Payment> onResourcesRetrievedCallback);

    void deleteESC(String cardId);

    boolean saveESC(String cardId, String value);

    void fetchFonts();
}
