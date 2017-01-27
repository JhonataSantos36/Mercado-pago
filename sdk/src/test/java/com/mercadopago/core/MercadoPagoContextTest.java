package com.mercadopago.core;

import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.ServicePreference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by mreverter on 1/23/17.
 */

public class MercadoPagoContextTest {

    @Before
    public void clearContext() {
        MercadoPagoClearableContext.clear();
    }

    @Test (expected=IllegalStateException.class)
    public void ifMercadoPagoContextNotInitializedAndInstanceRequiredThrowException() {
        MercadoPagoClearableContext.getInstance();
    }

    @Test
    public void ifMercadoPagoContextInitializedSetData() {
        String publicKey = "PK";
        ServicePreference servicePreference = Mockito.mock(ServicePreference.class);
        DecorationPreference decorationPreference = Mockito.mock(DecorationPreference.class);
        CheckoutPreference checkoutPreference = Mockito.mock(CheckoutPreference.class);

        new MercadoPagoClearableContext.Builder()
                .setPublicKey(publicKey)
                .setServicePreference(servicePreference)
                .setDecorationPreference(decorationPreference)
                .setCheckoutPreference(checkoutPreference)
                .initialize();

        Assert.assertEquals(publicKey, MercadoPagoContext.getInstance().getPublicKey());
        Assert.assertEquals(decorationPreference, MercadoPagoContext.getInstance().getDecorationPreference());
        Assert.assertEquals(servicePreference, MercadoPagoContext.getInstance().getServicePreference());
        Assert.assertEquals(checkoutPreference, MercadoPagoContext.getInstance().getCheckoutPreference());
    }

    @Test
    public void ifMercadoPagoContextInitializedRetrieveSingleInstance() {
        String publicKey = "PK";
        ServicePreference servicePreference = Mockito.mock(ServicePreference.class);
        DecorationPreference decorationPreference = Mockito.mock(DecorationPreference.class);
        CheckoutPreference checkoutPreference = Mockito.mock(CheckoutPreference.class);

        new MercadoPagoClearableContext.Builder()
                .setPublicKey(publicKey)
                .setServicePreference(servicePreference)
                .setDecorationPreference(decorationPreference)
                .setCheckoutPreference(checkoutPreference)
                .initialize();

        MercadoPagoContext mercadoPagoContext1 = MercadoPagoContext.getInstance();
        MercadoPagoContext mercadoPagoContext2 = MercadoPagoContext.getInstance();

        Assert.assertEquals(mercadoPagoContext1, mercadoPagoContext2);
    }
}
