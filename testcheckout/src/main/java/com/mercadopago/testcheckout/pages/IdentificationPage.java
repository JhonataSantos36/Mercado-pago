package com.mercadopago.testcheckout.pages;

import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class IdentificationPage extends PageObject {

    public InstallmentsPage enterIdentificationNumber(final String idNumber) {
        Matcher<View> cardIdentificationNumberEditTextMatcher = withId(com.mercadopago.R.id.mpsdkCardIdentificationNumber);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.R.id.mpsdkNextButtonText);
        onView(cardIdentificationNumberEditTextMatcher).perform(typeText(idNumber));
        onView(cardNextButtonTextMatcher).perform(click());
        return new InstallmentsPage();
    }
}
