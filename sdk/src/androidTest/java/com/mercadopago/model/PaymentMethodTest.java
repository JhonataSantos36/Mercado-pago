package com.mercadopago.model;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.test.BaseTest;

import org.junit.Test;

public class PaymentMethodTest extends BaseTest<CheckoutActivity> {

    public PaymentMethodTest() {
        super(CheckoutActivity.class);
    }

    //TODO: fix
    @Test
    public void testIsIssuerRequired() {

        /*PaymentMethodComponent visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethodComponent master = StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required");
        assertTrue(!visa.isIssuerRequired());
        assertTrue(master.isIssuerRequired());*/
    }

    @Test
    public void testIsSecurityCodeRequired() {

        /*PaymentMethodComponent visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethodComponent tarshop = StaticMock.getPaymentMethod(getApplicationContext(), "_cvv_not_required");
        assertTrue(visa.isSecurityCodeRequired("466057"));
        assertTrue(tarshop.isSecurityCodeRequired("603488"));
        assertTrue(!tarshop.isSecurityCodeRequired("27995"));*/
    }
}
