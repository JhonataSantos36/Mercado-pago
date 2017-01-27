package com.mercadopago.providers;

import com.mercadopago.model.Discount;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by mromar on 1/24/17.
 */
public interface DiscountsProvider extends ResourcesProvider {
    void getDirectDiscount(String transactionAmount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    String getApiErrorMessage(String error);

    String getStandardErrorMessage();

}
