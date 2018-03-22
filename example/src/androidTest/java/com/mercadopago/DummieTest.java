package com.mercadopago;

import com.mercadopago.testCheckout.BaseCheckoutTest;
import com.mercadopago.testCheckout.pages.PaymentMethodPage;

import org.junit.Test;

public class DummieTest extends BaseCheckoutTest {



    @Test
    public void flowCreditCard() {
        new PaymentMethodPage()
                .selectCard()
                .selectCreditCard();
    }

}
