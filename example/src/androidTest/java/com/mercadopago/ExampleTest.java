package com.mercadopago;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.mercadopago.example.R;
import com.mercadopago.testcheckout.CheckoutResource;
import com.mercadopago.testcheckout.flows.CheckoutTestFlow;
import com.mercadopago.testcheckout.input.Card;
import com.mercadopago.testcheckout.input.Country;
import com.mercadopago.testcheckout.input.FakeCard;
import com.mercadopago.testcheckout.input.Visa;
import com.mercadopago.testcheckout.pages.CongratsPage;
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
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleTest {

    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> mActivityRule =
            new ActivityTestRule<>(CheckoutExampleActivity.class);

    @Before
    public void setUp() {
        //Go to checkout.
        Matcher<View> startCho = withId(R.id.continueButton);
        onView(startCho).check(matches(isDisplayed()));
        onView(startCho).perform(click());
    }

    @Test
    public void withValidVisaCreditCardFlowIsOk() {
        CheckoutTestFlow checkoutTestFlow = CheckoutTestFlow.createFlow();
        Card card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = checkoutTestFlow.runCreditCardPaymentFlowNoInstallments(card);
        assertTrue(congratsPage instanceof  CongratsPage);
    }
}