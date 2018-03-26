package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.hooks.components.PaymentConfirm;
import com.mercadopago.hooks.components.PaymentMethodConfirm;
import com.mercadopago.hooks.components.PaymentTypeConfirm;

public class ExampleHooks extends DefaultCheckoutHooks {

    @Override
    public Hook beforePaymentMethodConfig(@NonNull final HookComponent.Props props) {
        return new Hook() {
            @Override
            public Component<HookComponent.Props, Void> createComponent() {
                return new PaymentTypeConfirm(props.toBuilder()
                        .setToolbarTitle("Payment Type").build());
            }

            @Override
            public boolean isEnabled() {
                return true; ///default is true, no need to override
            }
        };
    }

    @Override
    public Hook afterPaymentMethodConfig(@NonNull final HookComponent.Props props) {
        return new Hook() {
            @Override
            public Component<HookComponent.Props, Void> createComponent() {
                return new PaymentMethodConfirm(props);
            }
        };
    }

    @Override
    public Hook beforePayment(@NonNull final HookComponent.Props props) {
        return new Hook() {
            @Override
            public Component<HookComponent.Props, Void> createComponent() {
                return new PaymentConfirm(props.toBuilder()
                        .setToolbarTitle("Before payment").build());
            }
        };
    }
}