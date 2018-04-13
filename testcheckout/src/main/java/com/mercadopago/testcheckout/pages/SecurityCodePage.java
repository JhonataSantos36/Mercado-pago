package com.mercadopago.testcheckout.pages;

import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SecurityCodePage extends PageObject {

    public IdentificationPage enterSecurityCode(final String escNumber) {
        Matcher<View> cardSecurityCodeEditTextMatcher = withId(com.mercadopago.R.id.mpsdkCardSecurityCode);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.R.id.mpsdkNextButtonText);
        onView(cardSecurityCodeEditTextMatcher).perform(typeText(escNumber));
        onView(cardNextButtonTextMatcher).perform(click());

        return new IdentificationPage();
    }
}
