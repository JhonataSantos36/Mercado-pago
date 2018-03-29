package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.lite.model.IdentificationType;
import com.mercadopago.mvp.TaggedCallback;

import java.util.List;

public class PayerInformationProviderImpl implements PayerInformationProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public PayerInformationProviderImpl(Context context, String publicKey, String payerAccessToken) {
        this.context = context;
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, payerAccessToken);
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_number);
    }

    @Override
    public String getInvalidIdentificationNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_name);
    }

    @Override
    public String getInvalidIdentificationLastNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_last_name);
    }

    @Override
    public String getInvalidIdentificationBusinessNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_last_name);
    }

    @Override
    public void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback) {
        mercadoPago.getIdentificationTypes(taggedCallback);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_identification_types);
    }
}
