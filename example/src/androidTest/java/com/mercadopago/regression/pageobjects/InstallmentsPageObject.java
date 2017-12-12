package com.mercadopago.regression.pageobjects;

import com.mercadopago.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class InstallmentsPageObject {
    public void selectOneInstallment() {
        onView(withId(R.id.mpsdkActivityInstallmentsView))
                .perform(actionOnItemAtPosition(0, click()));
    }


}
