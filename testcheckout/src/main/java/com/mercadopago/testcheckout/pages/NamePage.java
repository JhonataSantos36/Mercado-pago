package com.mercadopago.testcheckout.pages;

import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class NamePage extends PageObject {

    public ExpiryDatePage enterCardholderName(final String cardHolderName) {
        Matcher<View> cardCardholderNameEditTextMatcher = withId(com.mercadopago.R.id.mpsdkCardholderName);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.R.id.mpsdkNextButtonText);
        onView(cardCardholderNameEditTextMatcher).perform(typeText(cardHolderName));
        onView(cardNextButtonTextMatcher).perform(click());

        return new ExpiryDatePage();
    }
}
