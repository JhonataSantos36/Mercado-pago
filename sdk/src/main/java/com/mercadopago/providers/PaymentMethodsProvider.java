package com.mercadopago.providers;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public interface PaymentMethodsProvider extends ResourcesProvider{
    void getPaymentMethods(TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback);
}
