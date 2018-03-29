package com.mercadopago.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.core.MercadoPagoServices;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.model.PaymentBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoServicesAdapter extends MercadoPagoServices {

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";
    private static final String MP_CHECKOUT_PAYMENTS_URI = "/v1/checkout/payments";

    public MercadoPagoServicesAdapter(@NonNull final Context mContext, @NonNull final String mPublicKey) {
        this(mContext, mPublicKey, null);
    }

    public MercadoPagoServicesAdapter(@NonNull final Context mContext,
                                      @NonNull final String mPublicKey,
                                      @Nullable final String mPrivateKey) {
        super(mContext, mPublicKey, mPrivateKey, ModelsAdapter.adapt(CustomServicesHandler.getInstance().getServicePreference()));
    }

    public void createPayment(final PaymentBody paymentBody, final Callback<Payment> callback) {
        Map<String, Object> adaptedBody = ModelsAdapter.adapt(paymentBody);
        super.createPayment(paymentBody.getTransactionId(), MP_API_BASE_URL,
                MP_CHECKOUT_PAYMENTS_URI,
                adaptedBody,
                new HashMap<String, String>(),
                callback);
    }
}
