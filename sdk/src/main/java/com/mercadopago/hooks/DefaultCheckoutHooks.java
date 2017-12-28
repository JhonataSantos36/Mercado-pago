package com.mercadopago.hooks;

import android.support.annotation.NonNull;

public class DefaultCheckoutHooks implements CheckoutHooks {

    @Override
    public Hook beforePaymentMethodConfig(@NonNull final HookComponent.Props props) {
        return null;
    }

    @Override
    public Hook afterPaymentMethodConfig(@NonNull final HookComponent.Props props) {
        return null;
    }

    @Override
    public Hook beforePayment(@NonNull final HookComponent.Props props) {
        return null;
    }
}