package com.mercadopago.providers;

import com.mercadopago.lite.exceptions.CheckoutPreferenceException;
import com.mercadopago.lite.model.Campaign;
import com.mercadopago.model.Customer;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.Payer;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.lite.model.Site;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.lite.preferences.CheckoutPreference;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 2/2/17.
 */

public interface CheckoutProvider extends ResourcesProvider {
    void getCheckoutPreference(String checkoutPreferenceId, TaggedCallback<CheckoutPreference> taggedCallback);

    void getDiscountCampaigns(TaggedCallback<List<Campaign>> callback);

    void getDirectDiscount(BigDecimal amount, String payerEmail, TaggedCallback<Discount> taggedCallback);

    void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, Site site, TaggedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, TaggedCallback<Customer> onCustomerRetrievedCallback);

    String getCheckoutExceptionMessage(CheckoutPreferenceException exception);

    String getCheckoutExceptionMessage(IllegalStateException exception);

    void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, TaggedCallback<Payment> taggedCallback);

    void deleteESC(String cardId);

    boolean saveESC(String cardId, String value);

    void fetchFonts();
}
