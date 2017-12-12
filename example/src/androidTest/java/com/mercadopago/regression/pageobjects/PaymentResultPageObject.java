package com.mercadopago.regression.pageobjects;

import android.support.test.espresso.assertion.ViewAssertions;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class PaymentResultPageObject {
    public void checkApprovedShown() {
        onView(withId(com.mercadopago.R.id.mpsdkTitleBackground)).check(ViewAssertions.matches(isDisplayed()));
    }
}
