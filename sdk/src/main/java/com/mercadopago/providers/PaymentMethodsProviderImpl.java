package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;

import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public class PaymentMethodsProviderImpl implements PaymentMethodsProvider {
    private MercadoPago mercadoPago;

    public PaymentMethodsProviderImpl(Context context, String publicKey) throws IllegalStateException {
        if (publicKey == null) {
            throw new IllegalStateException("public key not set");
        } else if (context == null) {
            throw new IllegalStateException("context not set");
        }

        mercadoPago = createMercadoPago(context, publicKey);
    }

    @Override
    public void getPaymentMethods(final OnResourcesRetrievedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                resourcesRetrievedCallback.onSuccess(paymentMethods);
            }

            @Override
            public void failure(ApiException apiException) {
                MPException exception = new MPException(apiException);
                resourcesRetrievedCallback.onFailure(exception);
            }
        });
    }

    protected MercadoPago createMercadoPago(Context context, String publicKey) {
        return new MercadoPago.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }
}
