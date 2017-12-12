package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.model.PaymentData;

import java.util.HashMap;
import java.util.Map;

public class HooksStore {

    private static HooksStore instance;
    private CheckoutHooks checkoutHooks;
    private Hook hook;
    private Map<String, Object> data = new HashMap();

    private HooksStore() {

    }

    public static HooksStore getInstance() {
        if (instance == null) {
            instance = new HooksStore();
        }
        return instance;
    }

    public Hook activateBeforePaymentMethodConfig(@NonNull final String typeId) {
        Hook hook = null;
        if (this.checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentTypeId(typeId).build();
            hook = this.checkoutHooks.beforePaymentMethodConfig(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }

    public Hook activateAfterPaymentMethodConfig(@NonNull final PaymentData paymentData) {
        Hook hook = null;
        if (this.checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentData(paymentData).build();
            hook = this.checkoutHooks.afterPaymentMethodConfig(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }

    public Hook activateBeforePayment(@NonNull final PaymentData paymentData) {
        Hook hook = null;
        if (this.checkoutHooks != null) {
            final HookComponent.Props props = new HookComponent.Props.Builder()
                    .setData(data)
                    .setPaymentData(paymentData).build();
            hook = this.checkoutHooks.beforePayment(props);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }
        return hook;
    }

    public void setCheckoutHooks(@NonNull final CheckoutHooks checkoutHooks) {
        this.checkoutHooks = checkoutHooks;
    }

    public Hook getHook() {
        return hook;
    }

    public void setHook(final Hook hook) {
        this.hook = hook;
    }

    public Map<String, Object> getData() {
        return data;
    }
}