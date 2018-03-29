package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.mvp.TaggedCallback;

import java.util.List;

/**
 * Created by mromar on 4/26/17.
 */

public class IssuersProviderImpl implements IssuersProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public IssuersProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;

        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
    }

    @Override
    public void getIssuers(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback) {
        mercadoPago.getIssuers(paymentMethodId, bin, taggedCallback);
    }

    @Override
    public MercadoPagoError getEmptyIssuersError() {
        String message = context.getString(R.string.mpsdk_standard_error_message);
        String detail = context.getString(R.string.mpsdk_error_message_detail_issuers);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public String getCardIssuersTitle() {
        return context.getString(R.string.mpsdk_card_issuers_title);
    }
}
