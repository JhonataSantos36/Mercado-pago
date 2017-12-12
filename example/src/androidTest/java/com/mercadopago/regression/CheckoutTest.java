package com.mercadopago.regression;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.regression.pageobjects.CardFormPageObject;
import com.mercadopago.regression.pageobjects.InstallmentsPageObject;
import com.mercadopago.regression.pageobjects.PaymentMethodSelectionPageObject;
import com.mercadopago.regression.pageobjects.PaymentResultPageObject;
import com.mercadopago.regression.pageobjects.ReviewAndConfirmPageObject;
import com.mercadopago.regression.pageobjects.examples.CheckoutExamplePageObject;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckoutTest {

    private CheckoutExamplePageObject checkoutExamplePageObject = new CheckoutExamplePageObject();
    private PaymentMethodSelectionPageObject paymentMethodSelectionPageObject = new PaymentMethodSelectionPageObject();
    private CardFormPageObject cardFormPageObject = new CardFormPageObject();
    private InstallmentsPageObject installmentsPageObject = new InstallmentsPageObject();
    private ReviewAndConfirmPageObject rycPageObject = new ReviewAndConfirmPageObject();
    private PaymentResultPageObject paymentResultPageObject = new PaymentResultPageObject();

    @Test
    public void test() {
        checkoutExamplePageObject.launch();
        checkoutExamplePageObject.clickStartCheckout();

        paymentMethodSelectionPageObject.selectCreditCard();

        cardFormPageObject.enterCreditCardNumber("4509953566233704");
        cardFormPageObject.clickNext();
        cardFormPageObject.enterCardHolderName("APRO");
        cardFormPageObject.clickNext();
        cardFormPageObject.enterExpiryDate("1119");
        cardFormPageObject.clickNext();
        cardFormPageObject.enterSecurityCode("123");
        cardFormPageObject.clickNext();
        cardFormPageObject.enterIdentificationNumber("43333000");
        cardFormPageObject.clickNext();

        installmentsPageObject.selectOneInstallment();

        rycPageObject.clickConfirmPayment();

        paymentResultPageObject.checkApprovedShown();
    }
}