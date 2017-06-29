package com.mercadopago.providers;

import com.mercadopago.model.Campaign;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.util.List;

/**
 * Created by mromar on 1/24/17.
 */
public interface DiscountsProvider extends ResourcesProvider {
    void getDirectDiscount(String transactionAmount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback);

    void getCampaigns(OnResourcesRetrievedCallback<List<Campaign>> onResourcesRetrievedCallback);

    String getApiErrorMessage(String error);

    String getStandardErrorMessage();
}
