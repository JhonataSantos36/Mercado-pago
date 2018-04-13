package com.mercadopago.testcheckout.pages;


import android.support.test.espresso.contrib.RecyclerViewActions;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoCheckout;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class PaymentMethodPage extends PageObject {

    public CardPage selectCard() {
        onView(withId(R.id.mpsdkGroupsList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        return new CardPage();
    }


    public CashPage selectCash() {
        onView(withId(R.id.mpsdkGroupsList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        return new CashPage();
    }

    public PaymentMethodPage start(MercadoPagoCheckout.Builder builder) {
        builder.startForPayment();
        return this;
    }
}
