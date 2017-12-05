package com.mercadopago.hooks;

import android.support.annotation.NonNull;

import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.DecorationPreference;

public class HooksStore {

    private static HooksStore instance;
    private CheckoutHooks checkoutHooks;
    private Hook hook;

    private HooksStore() {

    }

    public static HooksStore getInstance() {
        if (instance == null) {
            instance = new HooksStore();
        }
        return instance;
    }

    public boolean hasCheckoutHooks() {
        return checkoutHooks != null;
    }

    public Hook activateBeforePaymentMethodConfig(@NonNull final String typeId,
                                                 @NonNull final DecorationPreference preference) {
        Hook hook = null;

        if (this.checkoutHooks != null) {
            hook = this.checkoutHooks.beforePaymentMethodConfig(typeId, preference);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }

        return hook;
    }

    public Hook activateAfterPaymentMethodConfig(@NonNull final PaymentData paymentData,
                                                  @NonNull final DecorationPreference preference) {

        Hook hook = null;

        if (this.checkoutHooks != null) {
            hook = this.checkoutHooks.afterPaymentMethodConfig(paymentData, preference);
            if (hook != null && !hook.isEnabled()) {
                hook = null;
            }
        }

        return hook;
    }

    public Hook activateBeforePayment(@NonNull final PaymentData paymentData,
                                      @NonNull final DecorationPreference preference) {
        Hook hook = null;

        if (this.checkoutHooks != null) {
            hook = this.checkoutHooks.beforePayment(paymentData, preference);
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
}