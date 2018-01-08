package com.mercadopago.core;

import com.mercadopago.preferences.PaymentResultScreenPreference;

/**
 * Created by mromar on 12/12/17.
 */

public class CheckoutSessionStore {

    private static CheckoutSessionStore INSTANCE;
    private static PaymentResultScreenPreference paymentResultScreenPreference;

    private CheckoutSessionStore() {
    }

    public static CheckoutSessionStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CheckoutSessionStore();
        }
        return INSTANCE;
    }

    public void setPaymentResultScreenPreference(PaymentResultScreenPreference paymentResultScreenPreference) {
        this.paymentResultScreenPreference = paymentResultScreenPreference;
    }

    public PaymentResultScreenPreference getPaymentResultScreenPreference() {
        if (this.paymentResultScreenPreference == null) {
            setPaymentResultScreenPreference(new PaymentResultScreenPreference.Builder().build());
        }
        return this.paymentResultScreenPreference;
    }
}
