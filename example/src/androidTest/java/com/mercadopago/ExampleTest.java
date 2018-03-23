package com.mercadopago;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.testCheckout.BaseCheckoutTest;
import com.mercadopago.testCheckout.flows.CheckoutTestFlow;
import com.mercadopago.testCheckout.input.Card;
import com.mercadopago.testCheckout.input.Country;
import com.mercadopago.testCheckout.input.FakeCard;
import com.mercadopago.testCheckout.input.Visa;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ExampleTest extends BaseCheckoutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

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