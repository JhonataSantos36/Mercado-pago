package com.mercadopago.testCheckout.pages;

import android.view.View;

import com.mercadopago.model.Identification;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SecurityCodePage extends PageObject {

    public IdentificationPage enterSecurityCode(){
        Matcher<View> cardSecurityCodeEditTextMatcher = withId(com.mercadopago.R.id.mpsdkCardSecurityCode);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.R.id.mpsdkNextButtonText);
        onView(cardSecurityCodeEditTextMatcher).perform(typeText("123"));
        onView(cardNextButtonTextMatcher).perform(click());

        return new IdentificationPage();
    }
}
