package com.mercadopago;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.content.ContextCompat;

import com.mercadopago.constants.Sites;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ViewUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by mreverter on 4/18/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest {
    @Rule
    public ActivityTestRule<PaymentVaultActivity> mTestRule = new ActivityTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent validStartIntent;
    private FakeAPI mFakeAPI;

    private BigDecimal transactionAmount = new BigDecimal(100);

    @Before
    public void setupStartIntent() {
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
        validStartIntent.putExtra("amount", JsonUtil.getInstance().toJson(transactionAmount));
        validStartIntent.putExtra("purchaseTitle", "test item");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void ifOnlyUniqueSearchItemAvailableThenSelectIt() {
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();
        Customer customer = StaticMock.getCustomer(3);
        mFakeAPI.addResponseToQueue(customer, 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    @Test
    public void decorateToolbarIfDecorationPreferenceSet() {
        //Prepare API responses
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        DecorationPreference decorationPreference = new DecorationPreference.Builder()
                .setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_light_grey))
                .enableDarkFont().build();

        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(ViewUtils.getBackgroundColor(mTestRule.getActivity().mAppBarLayout) == decorationPreference.getBaseColor());
    }

    private Discount getDirectDiscount() {
        Discount discount = new Discount();
        discount.setCouponAmount(new BigDecimal("100"));
        discount.setId(123L);
        discount.setAmountOff(new BigDecimal("100"));
        return discount;
    }
}
