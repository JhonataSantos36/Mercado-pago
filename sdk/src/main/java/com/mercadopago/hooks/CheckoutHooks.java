package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.DecorationPreference;

public interface CheckoutHooks {

    Hook beforePaymentMethodConfig(@NonNull final String typeId,
                                  @NonNull final DecorationPreference decorationPreference);

    Hook afterPaymentMethodConfig(@NonNull final PaymentData paymentData,
                                   @NonNull final DecorationPreference decorationPreference);

    Hook beforePayment(@NonNull final PaymentData paymentData,
                       @NonNull final DecorationPreference decorationPreference);
}