package com.mercadopago.presenters;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.PaymentMethod;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GuessingCardPresenterTest {
    @Rule
    public ActivityTestRule<CheckoutActivity> mTestRule = new ActivityTestRule<>(CheckoutActivity.class, true, false);


    // Guessing tests

    @Test
    public void whenAllGuessedPaymentMethodsShareTypeThenDoNotAskForPaymentType() {

        PaymentMethod creditCard1 = new PaymentMethod();
        creditCard1.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod creditCard2 = new PaymentMethod();
        creditCard2.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard1);
        paymentMethodList.add(creditCard2);

        GuessingCardPresenter presenter = new GuessingCardPresenter(InstrumentationRegistry.getContext());

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        Assert.assertFalse(shouldAskPaymentType);
    }

    @Test
    public void whenNotAllGuessedPaymentMethodsShareTypeThenDoAskForPaymentType() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod debitCard = new PaymentMethod();
        debitCard.setPaymentTypeId(PaymentTypes.DEBIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);
        paymentMethodList.add(debitCard);

        GuessingCardPresenter presenter = new GuessingCardPresenter(InstrumentationRegistry.getContext());

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        Assert.assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListIsNullThenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = null;

        GuessingCardPresenter presenter = new GuessingCardPresenter(InstrumentationRegistry.getContext());

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        Assert.assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListIsEmptyThenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();

        GuessingCardPresenter presenter = new GuessingCardPresenter(InstrumentationRegistry.getContext());

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        Assert.assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenUniquePaymentMethodGuessedThenPaymentMethodShouldDefined() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);

        GuessingCardPresenter presenter = new GuessingCardPresenter(InstrumentationRegistry.getContext());

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        Assert.assertFalse(shouldAskPaymentType);
    }
}
