package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.TaggedCallback;

import java.util.List;

public class PaymentMethodsProviderImpl implements PaymentMethodsProvider {
    private final MercadoPagoServicesAdapter mercadoPago;

    public PaymentMethodsProviderImpl(Context context, String publicKey) throws IllegalStateException {
        if (publicKey == null) {
            throw new IllegalStateException("public key not set");
        } else if (context == null) {
            throw new IllegalStateException("context not set");
        }

        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey);
    }

    @Override
    public void getPaymentMethods(final TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
        mercadoPago.getPaymentMethods(resourcesRetrievedCallback);
    }
}
