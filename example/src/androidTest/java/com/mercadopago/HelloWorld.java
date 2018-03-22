package com.mercadopago;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.mercadopago.examples.R;
import com.mercadopago.testCheckout.BaseCheckoutTest;
import com.mercadopago.testCheckout.flows.Flows;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HelloWorld extends BaseCheckoutTest{

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void mainActivityTest() {
        Matcher<View> ourCho = withId(R.id.startButton);
        Matcher<View> startCho = withId(R.id.continueButton);
        Flows flows = new Flows();

        onView(ourCho).check(matches(isDisplayed()));
        onView(ourCho).perform(click());
        onView(startCho).check(matches(isDisplayed()));
        onView(startCho).perform(click());

        flows.creditCardPaymentFlow(null);

    }
}