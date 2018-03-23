package com.mercadopago.testCheckout.flows;

import android.support.annotation.NonNull;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.testCheckout.input.Card;
import com.mercadopago.testCheckout.pages.PaymentMethodPage;

public class CheckoutTestFlow {

    private MercadoPagoCheckout.Builder builder;

    public static CheckoutTestFlow createFlow() {
        return new CheckoutTestFlow();
    }

    public static CheckoutTestFlow createFlowWithCheckout(@NonNull MercadoPagoCheckout.Builder builder) {
        return new CheckoutTestFlow(builder);
    }

    private CheckoutTestFlow() {
    }

    private CheckoutTestFlow(@NonNull MercadoPagoCheckout.Builder builder) {
        this.builder = builder;
    }

    public void runCreditCardPaymentFlowNoInstallments(Card card) {
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage();

        if (builder != null) {
            paymentMethodPage.start(builder);
        }

        paymentMethodPage.selectCard()
                .selectCreditCard()
                .enterCreditCardNumber(card.cardNumber())
                .enterCardholderName(card.cardHolderName())
                .enterExpiryDate(card.expDate())
                .enterSecurityCode(card.escNumber())
                .enterIdentificationNumber(card.cardHolderIdentityNumber())
                .selectInstallments(0)
                .pressConfirmButton();
    }
}
