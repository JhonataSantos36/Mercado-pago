package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;

public class PaymentResultProviderImpl implements PaymentResultProvider {
    private final Context context;

    public PaymentResultProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }
}
