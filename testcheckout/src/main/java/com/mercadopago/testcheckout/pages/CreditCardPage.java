package com.mercadopago.testcheckout.pages;


import android.view.View;

import com.mercadopago.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CreditCardPage extends PageObject {


    public NamePage enterCreditCardNumber(final String cardNumber) {
        Matcher<View> cardNumberEditTextMatcher = withId(R.id.mpsdkCardNumber);
        Matcher<View> cardNextButtonTextMatcher = withId(R.id.mpsdkNextButtonText);
        onView(cardNumberEditTextMatcher).perform(typeText(cardNumber));
        onView(cardNextButtonTextMatcher).perform(click());
        return new NamePage();
    }
}
