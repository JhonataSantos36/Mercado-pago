package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.LargeTest;


import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.MockedHttpClient;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.HttpClientUtil;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;


/**
 * Created by mreverter on 24/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest extends AndroidTestCase {

    @Rule
    public MockedApiTestRule<PaymentVaultActivity> mActivityRule = new MockedApiTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent startIntent;

    @Before
    public void setupStartIntent() {
        startIntent = new Intent();
        startIntent.putExtra("merchantPublicKey", "1234");
        startIntent.putExtra("amount", "100");
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        try {
            this.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson(this.getContext());
        mActivityRule.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mPaymentMethodSearch != null);
    }
}
