package com.mercadopago;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.testCheckout.CheckoutResource;
import com.mercadopago.testCheckout.flows.CheckoutTestFlow;
import com.mercadopago.testCheckout.input.Card;
import com.mercadopago.testCheckout.input.Country;
import com.mercadopago.testCheckout.input.FakeCard;
import com.mercadopago.testCheckout.input.Visa;
import com.mercadopago.testlib.HttpResource;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleTest {

    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        //Go to checkout.
        Matcher<View> ourCho = withId(R.id.startButton);
        Matcher<View> startCho = withId(R.id.continueButton);

        onView(ourCho).check(matches(isDisplayed()));
        onView(ourCho).perform(click());
        onView(startCho).check(matches(isDisplayed()));
        onView(startCho).perform(click());
    }

    @Test
    public void withValidVisaCreditCardFlowIsOk() {
        CheckoutTestFlow checkoutTestFlow = CheckoutTestFlow.createFlow();
        Card card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
        checkoutTestFlow.runCreditCardPaymentFlowNoInstallments(card);
    }
}