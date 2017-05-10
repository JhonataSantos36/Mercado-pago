package com.mercadopago.providers;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Issuer;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.util.List;

/**
 * Created by mromar on 4/26/17.
 */

public interface IssuersProvider extends ResourcesProvider {

    void getIssuers(String paymentMethodId, String bin, final OnResourcesRetrievedCallback<List<Issuer>> onResourcesRetrievedCallback);

    MercadoPagoError getEmptyIssuersError();

    String getCardIssuersTitle();
}
