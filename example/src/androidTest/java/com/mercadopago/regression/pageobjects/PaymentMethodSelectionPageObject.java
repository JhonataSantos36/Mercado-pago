package com.mercadopago.regression.pageobjects;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;

public class PaymentMethodSelectionPageObject {
    public void selectCreditCard() {
        Espresso.onView(ViewMatchers.withId(com.mercadopago.R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(com.mercadopago.R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
    }
}
