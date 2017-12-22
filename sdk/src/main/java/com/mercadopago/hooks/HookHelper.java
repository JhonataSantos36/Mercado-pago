package com.mercadopago.hooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.model.PaymentData;

import java.util.Map;

public class HookHelper {


    public static Hook activateBeforePaymentMethodConfig(@Nullable final CheckoutHooks checkoutHooks,
                                                         @NonNull final String typeId,
                                                         @NonNull final Map<String, Object> data) {
        Hook hook = null;
        if (checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentTypeId(typeId).build();
            hook = checkoutHooks.beforePaymentMethodConfig(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }

    public static Hook activateAfterPaymentMethodConfig(@Nullable final CheckoutHooks checkoutHooks,
                                                        @NonNull final PaymentData paymentData,
                                                        @NonNull final Map<String, Object> data) {
        Hook hook = null;
        if (checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentData(paymentData).build();
            hook = checkoutHooks.afterPaymentMethodConfig(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }

    public static Hook activateBeforePayment(@Nullable final CheckoutHooks checkoutHooks,
                                             @NonNull final PaymentData paymentData,
                                             @NonNull final Map<String, Object> data) {
        Hook hook = null;
        if (checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentData(paymentData).build();
            hook = checkoutHooks.beforePayment(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }
}