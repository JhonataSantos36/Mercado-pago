package com.mercadopago.providers;

import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.preferences.CheckoutPreference;

/**
 * Created by vaserber on 2/2/17.
 */

public interface CheckoutProvider extends ResourcesProvider {
    void getCheckoutPreference(String checkoutPreferenceId, OnResourcesRetrievedCallback<CheckoutPreference> onResourcesRetrievedCallback);
}
