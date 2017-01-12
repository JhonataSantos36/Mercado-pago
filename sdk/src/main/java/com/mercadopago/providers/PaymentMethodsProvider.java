package com.mercadopago.providers;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public interface PaymentMethodsProvider extends ResourcesProvider{
    void getPaymentMethods(OnResourcesRetrievedCallback<List<PaymentMethod>> resourcesRetrievedCallback);
}
