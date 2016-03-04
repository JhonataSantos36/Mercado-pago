package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.LargeTest;


import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
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
    public MockedApiTestRule<PaymentVaultActivity> mTestRule = new MockedApiTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent startIntent;

    @Before
    public void setupStartIntent() {
        startIntent = new Intent();
        startIntent.putExtra("merchantPublicKey", "1234");
        startIntent.putExtra("amount", "100");
    }

    @Test
    public void setPublicKeyOnCreate() {
        mTestRule.launchActivity(startIntent);
        Assert.assertTrue(mTestRule.getActivity().mMerchantPublicKey != null);
    }

    @Test
    public void setAmountOnCreate() {
        mTestRule.launchActivity(startIntent);
        Assert.assertTrue(mTestRule.getActivity().mAmount != null);
    }

    @Test
    public void initializeGroupsRecyclerViewOnCreate() {
        mTestRule.launchActivity(startIntent);
        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView != null);
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        PaymentMethodSearch paymentMethodSearch = new PaymentMethodSearch();
        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(startIntent);
        Assert.assertTrue(mTestRule.getActivity().mPaymentMethodSearch != null);
    }

    @Test
    public void whenPaymentMethodSearchHasGroupsFillGroupsRecyclerView() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(json, 200, "");
        mTestRule.launchActivity(startIntent);
        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void ifSelectedSearchItemReceivedReturnIsItemSelectedTrue() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        startIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(startIntent);

        Assert.assertTrue(mTestRule.getActivity().isItemSelected());
    }

    @Test
    public void ifSelectedSearchItemReceivedDoNotRetrievePaymentMethodSearch() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);

        mTestRule.addApiResponseToQueue(json, 200, "");
        startIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(startIntent);

        Assert.assertTrue(mTestRule.getActivity().mPaymentMethodSearch == null);
    }

    @Test
    public void ifSelectedSearchItemReceivedShowItsChildren() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0);
        startIntent.putExtra("selectedSearchItem", item);
        mTestRule.launchActivity(startIntent);

        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.launchActivity(startIntent);
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

    @Test
    public void whenItemSelectedIsCardTypeStartGuessingNewCardActivityWithPublicKeyAndPaymentTypeId() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.launchActivity(startIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0).getChildren().get(0);

        Intents.intended(IntentMatchers.hasComponent(GuessingNewCardActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtra("merchantPublicKey", "1234"));
        Intents.intended(IntentMatchers.hasExtra("paymentTypeId", item.getId()));
    }
    @Test
    public void whenItemSelectedIsNotCardTypeAndDoesNotHaveChildrenStartPaymentMethodsActivityWithPublicKeyAndPaymentTypeId() {

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        //remove children
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(startIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));



        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1);

        Intents.intended(IntentMatchers.hasComponent(PaymentMethodsActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtra("merchantPublicKey", "1234"));
        Intents.intended(IntentMatchers.hasExtra("paymentTypeId", selectedSearchItem.getId()));
    }

    @Test
    public void whenOfflinePaymentMethodSelectedSetItAsResultForCheckoutActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.launchActivity(startIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1).getChildren().get(0);

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    CheckoutActivity currentActivity = (CheckoutActivity) resumedActivities.iterator().next();
                    Assert.assertEquals(currentActivity.mSelectedPaymentMethod.getId(), selectedSearchItem.getId());
                }
            }
        });
    }
    @Test
    public void whenResultFromGuessingNewCardFormReceivedSetItAsResultForCheckoutActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        final Token token = new Token();
        token.setId("1");
        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(startIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(mTestRule.getActivity().RESULT_OK, guessingFormResultIntent);

        Intents.intending(IntentMatchers.hasComponent(GuessingNewCardActivity.class.getName())).respondWith(result);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    CheckoutActivity currentActivity = (CheckoutActivity) resumedActivities.iterator().next();
                    Assert.assertEquals(currentActivity.mSelectedPaymentMethod.getId(), paymentMethod.getId());
                    Assert.assertEquals(currentActivity.mCreatedToken.getId(), token.getId());
                }
            }
        });
    }
}
