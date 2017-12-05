package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.DecorationPreference;

public class DefaultCheckoutHooks implements CheckoutHooks {

    @Override
    public Hook beforePaymentMethodConfig(@NonNull final String typeId,
                                         @NonNull final DecorationPreference decorationPreference) {
        return null;
    }

    @Override
    public Hook afterPaymentMethodConfig(@NonNull final PaymentData paymentData,
                                          @NonNull final DecorationPreference decorationPreference) {
        return null;
    }

    @Override
    public Hook beforePayment(@NonNull final PaymentData paymentData,
                              @NonNull final DecorationPreference decorationPreference) {
        return null;
    }
}