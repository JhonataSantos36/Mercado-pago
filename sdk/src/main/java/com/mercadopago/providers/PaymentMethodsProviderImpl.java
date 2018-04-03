package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.util.ApiUtil;

import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public class PaymentMethodsProviderImpl implements PaymentMethodsProvider {
    private final MercadoPagoServicesAdapter mercadoPago;

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
                MercadoPagoError exception = new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PAYMENT_METHODS);
                resourcesRetrievedCallback.onFailure(exception);
            }
        });
    }

    protected MercadoPagoServicesAdapter createMercadoPago(Context context, String publicKey) {
        return new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }
}
