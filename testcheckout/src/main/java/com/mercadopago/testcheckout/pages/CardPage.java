package com.mercadopago.testcheckout.pages;


import android.support.test.espresso.contrib.RecyclerViewActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CardPage extends PageObject {

    public CreditCardPage selectCreditCard() {
        onView(withId(com.mercadopago.R.id.mpsdkGroupsList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        return new CreditCardPage();
    }

    public DebitCardPage selectDebitCard() {
        onView(withId(com.mercadopago.R.id.mpsdkGroupsList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        return new DebitCardPage();
    }
}
