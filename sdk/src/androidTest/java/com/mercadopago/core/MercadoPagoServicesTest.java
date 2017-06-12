package com.mercadopago.core;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

/**
 * Created by mreverter on 1/18/17.
 */

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
}
