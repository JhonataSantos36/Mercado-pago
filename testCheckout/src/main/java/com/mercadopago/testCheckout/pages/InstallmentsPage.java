package com.mercadopago.testCheckout.pages;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import com.mercadopago.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class InstallmentsPage extends PageObject {

    public ReviewAndConfirmPage selectInstallments(){

        Matcher<View> InstallmentsRecyclerViewMatcher = withId(R.id.mpsdkActivityInstallmentsView);

        onView(InstallmentsRecyclerViewMatcher)
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        return new ReviewAndConfirmPage();
    }
}
