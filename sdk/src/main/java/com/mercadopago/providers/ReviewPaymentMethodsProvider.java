package com.mercadopago.providers;

import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by vaserber on 8/17/17.
 */

public interface ReviewPaymentMethodsProvider extends ResourcesProvider {

    String getEmptyPaymentMethodsListError();

    String getStandardErrorMessage();
}
