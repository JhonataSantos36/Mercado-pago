package com.mercadopago.core;

import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.ServicePreference;

/**
 * Created by mreverter on 1/23/17.
 */

class MercadoPagoClearableContext extends MercadoPagoContext {
    private MercadoPagoClearableContext(String publicKey, CheckoutPreference checkoutPreference, DecorationPreference decorationPreference, ServicePreference servicePreference) {
        super(publicKey, checkoutPreference, decorationPreference, servicePreference);
    }

    public static void clear() {
        instance = null;
    }
}
