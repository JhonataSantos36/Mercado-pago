package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.hooks.components.PaymentConfirm;
import com.mercadopago.hooks.components.PaymentMethodConfirm;
import com.mercadopago.hooks.components.PaymentTypeConfirm;
import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.DecorationPreference;

public class ExampleHooks extends DefaultCheckoutHooks {

    @Override
    public Hook beforePaymentMethodConfig(@NonNull final String typeId,
                                         @NonNull final DecorationPreference decorationPreference) {

        return new Hook() {

            @Override
            public Component<HookComponent.Props> createComponent() {
                final HookComponent.Props props = new HookComponent.Props(
                        HooksStore.getInstance(),
                        typeId,
                        null,
                        decorationPreference,
                        "Hook 1",
                        true);

                return new PaymentTypeConfirm(props);
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Override
    public Hook afterPaymentMethodConfig(@NonNull final PaymentData paymentData,
                                          @NonNull final DecorationPreference decorationPreference) {

        return new Hook() {

            @Override
            public Component<HookComponent.Props> createComponent() {
                final HookComponent.Props props = new HookComponent.Props(
                        HooksStore.getInstance(),
                        null,
                        paymentData,
                        decorationPreference,
                        "Hook 2",
                        true);

                return new PaymentMethodConfirm(props);
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Override
    public Hook beforePayment(@NonNull final PaymentData paymentData,
                              @NonNull final DecorationPreference decorationPreference) {

        return new Hook() {

            @Override
            public Component<HookComponent.Props> createComponent() {
                final HookComponent.Props props = new HookComponent.Props(
                        HooksStore.getInstance(),
                        null,
                        paymentData,
                        decorationPreference,
                        "Hook 3",
                        false);

                return new PaymentConfirm(props);
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }
}
