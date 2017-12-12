package com.mercadopago.regression.pageobjects;

import com.mercadopago.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ReviewAndConfirmPageObject {
    public void clickConfirmPayment() {
        onView(withId(R.id.mpsdkCheckoutFloatingConfirmButton)).perform(click());
    }
}
