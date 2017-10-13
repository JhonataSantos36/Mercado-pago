package com.mercadopago.providers;

import com.mercadopago.mvp.ResourcesProvider;

public interface PaymentResultProvider extends ResourcesProvider {
    String getStandardErrorMessage();
}
