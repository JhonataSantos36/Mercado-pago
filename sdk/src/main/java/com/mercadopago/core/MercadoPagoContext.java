package com.mercadopago.core;

import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.ServicePreference;

/**
 * Created by mreverter on 1/19/17.
 */
public class MercadoPagoContext {
    protected String publicKey;
    protected DecorationPreference decorationPreference;
    protected ServicePreference servicePreference;
    protected CheckoutPreference checkoutPreference;

    protected static MercadoPagoContext instance;

    public static MercadoPagoContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MercadoPago context not initialized");
        }
        return instance;
    }

    protected MercadoPagoContext(String publicKey, CheckoutPreference checkoutPreference, DecorationPreference decorationPreference, ServicePreference servicePreference) {
        this.publicKey = publicKey;
        this.decorationPreference = decorationPreference;
        this.servicePreference = servicePreference;
        this.checkoutPreference = checkoutPreference;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public DecorationPreference getDecorationPreference() {
        return decorationPreference;
    }

    public ServicePreference getServicePreference() {
        return servicePreference;
    }

    public CheckoutPreference getCheckoutPreference() {
        return checkoutPreference == null ? new CheckoutPreference() : checkoutPreference;
    }

    public static class Builder {
        private String publicKey;
        private DecorationPreference decorationPreference;
        private ServicePreference servicePreference;
        private CheckoutPreference checkoutPreference;

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.decorationPreference = decorationPreference;
            return this;
        }

        public Builder setServicePreference(ServicePreference servicePreference) {
            this.servicePreference = servicePreference;
            return this;
        }

        public Builder setCheckoutPreference(CheckoutPreference checkoutPreference) {
            this.checkoutPreference = checkoutPreference;
            return this;
        }

        public void initialize() {
            if (publicKey == null) throw new IllegalStateException("Public key required");
            instance = new MercadoPagoContext(publicKey, checkoutPreference, decorationPreference, servicePreference);
        }
    }
}
