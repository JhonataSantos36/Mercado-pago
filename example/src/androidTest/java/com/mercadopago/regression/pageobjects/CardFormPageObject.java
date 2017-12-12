package com.mercadopago.regression.pageobjects;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CardFormPageObject {
    public void enterCreditCardNumber(String cardNumber) {
        typeIn(com.mercadopago.R.id.mpsdkCardNumber, cardNumber);
    }

    public void enterCardHolderName(String name) {
        typeIn(com.mercadopago.R.id.mpsdkCardholderName, name);
    }

    public void enterExpiryDate(String expiryDate) {
        typeIn(com.mercadopago.R.id.mpsdkCardExpiryDate, expiryDate);
    }

    public void clickNext() {
        onView(withId(com.mercadopago.R.id.mpsdkNextButton)).perform(click());
    }

    private void typeIn(int resource, String text) {
        onView(withId(resource))
                .perform(typeText(text));
    }

    public void enterSecurityCode(String securityCode) {
        onView(withId(com.mercadopago.R.id.mpsdkCardSecurityCode))
                .perform(typeText(securityCode));
    }

    public void enterIdentificationNumber(String identificationNumber) {
        onView(withId(com.mercadopago.R.id.mpsdkCardIdentificationNumber))
                .perform(typeText(identificationNumber));
    }
}
