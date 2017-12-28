package com.mercadopago.hooks.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.RendererFactory;
import com.mercadopago.hooks.HookComponent;

public class PaymentTypeConfirm extends HookComponent {

    static {
        RendererFactory.register(PaymentTypeConfirm.class, PaymentTypeConfirmRenderer.class);
    }

    public PaymentTypeConfirm(@NonNull final Props props) {
        super(props);
    }
}
