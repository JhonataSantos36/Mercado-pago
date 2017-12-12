package com.mercadopago.regression.pageobjects.examples;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.mercadopago.examples.R;
import com.mercadopago.examples.checkout.CheckoutExampleActivity;

import org.junit.Rule;

public class CheckoutExamplePageObject {

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> mTestRule = new ActivityTestRule<>(CheckoutExampleActivity.class, true, false);

    public void clickStartCheckout() {
        Espresso.onView(ViewMatchers.withId(R.id.checkoutStartButton)).perform(ViewActions.click());
    }

    public void launch() {
        mTestRule.launchActivity(new Intent());
    }
}
