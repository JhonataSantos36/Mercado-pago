package com.mercadopago.core;

import android.os.Build;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import junit.framework.Assert;

public class MercadoPagoServicesAdapterTest extends BaseTest<CheckoutActivity> {

    public MercadoPagoServicesAdapterTest() {
        super(CheckoutActivity.class);
    }

    public void testStartOkWithPublicKey() {

        try {
            new MercadoPagoServicesAdapter.Builder()
                    .setContext(getApplicationContext())
                    .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                    .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testStartOkWithPrivateKey() {

        try {
            new MercadoPagoServicesAdapter.Builder()
                    .setContext(getApplicationContext())
                    .setPrivateKey(StaticMock.DUMMY_PRIVATE_KEY)
                    .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testStartOkWithPublicAndPrivateKey() {

        try {
            new MercadoPagoServicesAdapter.Builder()
                    .setContext(getApplicationContext())
                    .setPrivateKey(StaticMock.DUMMY_PRIVATE_KEY)
                    .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                    .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testContextNull() {

        try {
            new MercadoPagoServicesAdapter.Builder()
                    .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                    .build();
            fail("Start should have failed on context null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("context is null"));
        }
    }

    public void testNullKey() {
        try {
            new MercadoPagoServicesAdapter.Builder()
                    .setContext(getApplicationContext())
                    .build();
            fail("Start should have failed on key null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("key is null"));
        }
    }

    public void testWhenAndroidVersionIsPriorICSThenPropertyKeepAliveFalseElseDefault() {
        new MercadoPagoServicesAdapter.Builder()
                .setContext(InstrumentationRegistry.getContext())
                .setPublicKey("DUMMY_PK")
                .build();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Assert.assertEquals("false", System.getProperty("http.keepAlive"));
        } else {
            Assert.assertEquals(null, System.getProperty("http.keepAlive"));
        }
    }
}
