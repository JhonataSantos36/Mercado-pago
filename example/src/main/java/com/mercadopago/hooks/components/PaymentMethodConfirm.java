package com.mercadopago.hooks.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.hooks.HookComponent;

public class PaymentMethodConfirm extends HookComponent<Void> {

    static {
        RendererFactory.register(PaymentMethodConfirm.class, PaymentMethodConfirmRenderer.class);
    }

    public PaymentMethodConfirm(@NonNull final Props props) {
        super(props);
    }
}
