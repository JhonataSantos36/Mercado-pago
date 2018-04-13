package com.mercadopago.testcheckout.pages;

import android.view.View;

import com.mercadopago.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ReviewAndConfirmPage extends PageObject {

    public CongratsPage pressConfirmButton(){

        Matcher<View> checkoutConfirmButtonTextMatcher = withId(R.id.floating_confirm);
        onView(checkoutConfirmButtonTextMatcher).perform(click());
        return new CongratsPage();
    }
}
