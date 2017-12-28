package com.mercadopago.hooks;

import android.support.annotation.NonNull;

public interface CheckoutHooks {

    Hook beforePaymentMethodConfig(@NonNull final HookComponent.Props props);

    Hook afterPaymentMethodConfig(@NonNull final HookComponent.Props props);

    Hook beforePayment(@NonNull final HookComponent.Props props);
}