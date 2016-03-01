package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.LargeTest;


import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.JsonUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.runner.lifecycle.Stage.RESUMED;

import java.util.Collection;

/**
 * Created by mreverter on 24/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest {

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
    public void setPublicKeyOnCreate() {
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mMerchantPublicKey != null);
    }

    @Test
    public void setAmountOnCreate() {
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mAmount != null);
    }

    @Test
    public void initializeGroupsRecyclerViewOnCreate() {
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mSearchItemsRecyclerView != null);
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        PaymentMethodSearch paymentMethodSearch = new PaymentMethodSearch();
        mActivityRule.addApiResponseToQueue(paymentMethodSearch, 200, "");
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mPaymentMethodSearch != null);
    }

    @Test
    public void whenPaymentMethodSearchHasGroupsFillGroupsRecyclerView() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        mActivityRule.addApiResponseToQueue(json, 200, "");
        mActivityRule.launchActivity(startIntent);
        Assert.assertTrue(mActivityRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void ifSelectedSearchItemReceivedReturnIsItemSelectedTrue() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        startIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mActivityRule.launchActivity(startIntent);

        Assert.assertTrue(mActivityRule.getActivity().isItemSelected());
    }

    @Test
    public void ifSelectedSearchItemReceivedDoNotRetrievePaymentMethodSearch() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);

        mActivityRule.addApiResponseToQueue(json, 200, "");
        startIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mActivityRule.launchActivity(startIntent);

        Assert.assertTrue(mActivityRule.getActivity().mPaymentMethodSearch == null);
    }

    @Test
    public void ifSelectedSearchItemReceivedShowItsChildren() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0);
        startIntent.putExtra("selectedSearchItem", item);
        mActivityRule.launchActivity(startIntent);

        Assert.assertTrue(mActivityRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mActivityRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mActivityRule.launchActivity(startIntent);
        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        final PaymentMethodSearchItem firstSearchItem = paymentMethodSearch.getGroups().get(0);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    PaymentVaultActivity currentActivity = (PaymentVaultActivity) resumedActivities.iterator().next();
                    Assert.assertEquals(currentActivity.mSelectedSearchItem.getId(), firstSearchItem.getId());
                }
            }
        });
    }
}
