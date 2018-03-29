package com.mercadopago.providers;

import com.mercadopago.lite.model.Discount;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;

public interface DiscountsProvider extends ResourcesProvider {
    void getDirectDiscount(String transactionAmount, String payerEmail, TaggedCallback<Discount> taggedCallback);

    void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, TaggedCallback<Discount> taggedCallback);

    String getApiErrorMessage(String error);

    String getStandardErrorMessage();
}
