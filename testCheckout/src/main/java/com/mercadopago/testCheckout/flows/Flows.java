package com.mercadopago.testCheckout.flows;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.model.CardToken;
import com.mercadopago.testCheckout.pages.PaymentMethodPage;

public class Flows {

    private MercadoPagoCheckout.Builder builder;

    public Flows(MercadoPagoCheckout.Builder builder) {
        this.builder = builder;
    }

    public Flows() {
    }

    public void creditCardPaymentFlow(CardToken cardToken) {
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage();

        if (builder != null) {
            paymentMethodPage.start(builder);
        }

        paymentMethodPage.selectCard()
                .selectCreditCard()
                .enterCreditCardNumber()
                .enterCardholderName()
                .enterExpiryDate()
                .enterSecurityCode()
                .enterIdentificationNumber()
                .selectInstallments()
                .pressConfirmButton();
    }
}
