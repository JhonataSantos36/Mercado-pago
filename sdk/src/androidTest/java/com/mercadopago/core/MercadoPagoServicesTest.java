package com.mercadopago.core;

import android.os.Build;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import junit.framework.Assert;

public class MercadoPagoServicesTest extends BaseTest<CheckoutActivity> {

    public MercadoPagoServicesTest() {
        super(CheckoutActivity.class);
    }

    public void testStartOkWithPublicKey() {

        try {
            new MercadoPagoServices.Builder()
                    .setContext(getApplicationContext())
                    .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                    .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testStartOkWithPrivateKey() {

        try {
            new MercadoPagoServices.Builder()
                    .setContext(getApplicationContext())
                    .setPrivateKey(StaticMock.DUMMY_PRIVATE_KEY)
                    .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testStartOkWithPublicAndPrivateKey() {

        try {
            new MercadoPagoServices.Builder()
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
            new MercadoPagoServices.Builder()
                    .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                    .build();
            fail("Start should have failed on context null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("context is null"));
        }
    }

    public void testNullKey() {

        try {
            new MercadoPagoServices.Builder()
                    .setContext(getApplicationContext())
                    .build();
            fail("Start should have failed on key null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("key is null"));
        }
    }

    public void testWhenAndroidVersionIsPriorICSThenPropertyKeepAliveFalseElseDefault() {
        new MercadoPagoServices.Builder()
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
