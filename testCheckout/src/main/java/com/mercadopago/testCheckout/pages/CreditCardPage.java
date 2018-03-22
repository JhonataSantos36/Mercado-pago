package com.mercadopago.testCheckout.pages;


import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import com.mercadopago.R;

public class CreditCardPage extends PageObject {


    public NamePage enterCreditCardNumber(){

        Matcher<View> cardNumberEditTextMatcher = withId(R.id.mpsdkCardNumber);
        Matcher<View> cardNextButtonTextMatcher = withId(R.id.mpsdkNextButtonText);
        onView(cardNumberEditTextMatcher).perform(typeText("4242424242424242"));
        onView(cardNextButtonTextMatcher).perform(click());
        return new NamePage();
    }
}
