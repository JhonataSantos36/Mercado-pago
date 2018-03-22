package com.mercadopago.testCheckout.base;


import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.plugins.DataInitializationTask;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.testCheckout.BaseCheckoutTest;
import com.mercadopago.testCheckout.pages.PaymentMethodPage;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CardTest extends BaseCheckoutTest {

    private MercadoPagoCheckout.Builder builder;


    @Before
    public void setUp() {
        final Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("amount", 120f);

        builder = new MercadoPagoCheckout.Builder()
                .setContext(InstrumentationRegistry.getContext())
                .setPublicKey("TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee")
                .setCheckoutPreference(new CheckoutPreference("243962506-e9464aff-30dd-43e0-a6fa-37e3a54b884c"))
                .setDataInitializationTask(new DataInitializationTask(defaultData) {
                    @Override
                    public void onLoadData(@NonNull final Map<String, Object> data) {
                        data.put("user", "Nico");
                    }
                });
    }

    @Test
    public void flowCreditCard() {
        new PaymentMethodPage()
                .start(builder)
                .selectCard()
                .selectCreditCard();
    }

    @Test
    public void flowDebitCard() {
        new PaymentMethodPage()
                .start(builder)
                .selectCard()
                .selectDebitCard();
    }
}
