package com.mercadopago.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.core.MercadoPagoServices;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
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
        super(mContext, mPublicKey, mPrivateKey);
    }

    public void createPayment(final PaymentBody paymentBody, final Callback<Payment> callback) {

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> payload = JsonUtil.getInstance()
                .getGson()
                .fromJson(JsonUtil.getInstance().toJson(paymentBody), type);

        payload.put("issuer_id", paymentBody.getIssuerId());
        payload.put("installments", paymentBody.getInstallments());
        payload.put("campaign_id", paymentBody.getCampaignId());
        super.createPayment(paymentBody.getTransactionId(), MP_API_BASE_URL,
                MP_CHECKOUT_PAYMENTS_URI,
                payload,
                new HashMap<String, String>(),
                callback);
    }
}
