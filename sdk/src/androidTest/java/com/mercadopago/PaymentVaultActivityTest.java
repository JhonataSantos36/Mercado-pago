package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.LargeTest;


import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Token;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.JsonUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mreverter on 24/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest {

    @Rule
    public MockedApiTestRule<PaymentVaultActivity> mTestRule = new MockedApiTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent validStartIntent;

    @Before
    public void setupStartIntent() {
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
        validStartIntent.putExtra("amount", "100");
        validStartIntent.putExtra("purchaseTitle", "test item");
        validStartIntent.putExtra("currencyId", "ARS");
    }

    @Test
    public void setPublicKeyOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mMerchantPublicKey != null);
    }


    @Test
    public void setAmountOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mAmount != null);
    }

    @Test
    public void setPurchaseTitleOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPurchaseTitle != null);
    }

    @Test
    public void setMaxInstallmentsOnCreateIfReceived() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(3);
        validStartIntent.putExtra("paymentPreference", paymentPreference);
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentPreference.getMaxInstallments() == 3);
    }

    @Test
    public void setDefaultInstallmentsOnCreateIfReceived() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(3);
        validStartIntent.putExtra("paymentPreference", paymentPreference);
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentPreference.getDefaultInstallments() == 3);
    }

    @Test
    public void setExcludedPaymentTypesOnCreateIfReceived() {
        List<String> excludedTypes = new ArrayList<String>() {{
            add("ticket");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedTypes);
        validStartIntent.putExtra("paymentPreference", paymentPreference);
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentPreference.getExcludedPaymentTypes().contains("ticket"));
    }

    @Test
    public void setExcludedPaymentMethodsOnCreateIfReceived() {
        List<String> excludedPaymentMethods = new ArrayList<String>() {{
            add("oxxo");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", paymentPreference);
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentPreference.getExcludedPaymentMethodIds().contains("oxxo"));
    }

    @Test
    public void initializeGroupsRecyclerViewOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView != null);
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentMethodSearch != null);
    }

    @Test
    public void ifPaymentMethodSearchIsEmptyFinishActivity() {
        PaymentMethodSearch paymentMethodSearchJson = new PaymentMethodSearch();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void whenPaymentMethodSearchHasGroupsFillGroupsRecyclerView() {
        //Prepare API responses
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void ifSelectedSearchItemReceivedReturnIsItemSelectedTrue() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        validStartIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().isItemSelectedStart());
    }

    @Test
    public void ifSelectedSearchItemReceivedDoNotRetrievePaymentMethodSearch() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);

        mTestRule.addApiResponseToQueue(json, 200, "");

        validStartIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mPaymentMethodSearch == null);
    }

    @Test
    public void ifSelectedSearchItemReceivedShowItsChildren() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0);
        validStartIntent.putExtra("selectedSearchItem", item);
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        final PaymentMethodSearchItem firstSearchItem = paymentMethodSearch.getGroups().get(0);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    PaymentVaultActivity currentActivity = (PaymentVaultActivity) resumedActivities.iterator().next();
                    assertEquals(currentActivity.mSelectedSearchItem.getId(), firstSearchItem.getId());
                }
            }
        });
    }

    @Test
    public void whenItemSelectedIsCardTypeStartGuessingNewCardActivityWithPublicKeyAndPaymentTypeId() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0).getChildren().get(0);

        //TODO cambiar por flowcard
        /*intended(hasComponent(GuessingNewCardActivity.class.getName()));
        intended(hasExtra("merchantPublicKey", "1234"));
        intended(hasExtra("paymentTypeId", item.getId()));*/
    }

    @Test
    public void whenItemSelectedIsNotCardTypeAndDoesNotHaveChildrenStartPaymentMethodsActivityWithPublicKeyAndPaymentTypeId() {

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        //remove children
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));


        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1);

        intended(hasComponent(PaymentMethodsActivity.class.getName()));
        intended(hasExtra("merchantPublicKey", "1234"));
        intended(hasExtra("paymentTypeId", selectedSearchItem.getId()));
    }


    @Test
    public void whenInitializedDoNotShowShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertTrue(activity.mShoppingCartFragment.isHidden());
    }

    @Test
    public void ifNavigationBackClickedGoBack() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        mTestRule.isActivityFinishedOrFinishing();
    }

    @Test
    public void testOpenShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertTrue(activity.mShoppingCartFragment.isHidden());

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        assertTrue(!activity.mShoppingCartFragment.isHidden());
    }

    @Test
    public void testOpenAndCloseShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertTrue(activity.mShoppingCartFragment.isHidden());

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        assertTrue(!activity.mShoppingCartFragment.isHidden());


        onView(withId(R.id.shoppingCartIcon)).perform(click());
        assertTrue(activity.mShoppingCartFragment.isHidden());
    }

    //VALIDATIONS TESTS

    @Test
    public void ifCurrencyIdIsInvalidHideShoppingCartIcon() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidCurrencyIntent = new Intent();
        invalidCurrencyIntent.putExtras(validStartIntent.getExtras());
        invalidCurrencyIntent.putExtra("currencyId", "An invalid currency id");

        mTestRule.launchActivity(invalidCurrencyIntent);
        assertTrue(!mTestRule.getActivity().mShoppingCartIcon.isShown());
    }

    @Test
    public void ifCurrencyIdIsNullHideShoppingCartIcon() {

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent noCurrencyIntent = new Intent();
        noCurrencyIntent.putExtras(validStartIntent.getExtras());
        noCurrencyIntent.removeExtra("currencyId");

        mTestRule.launchActivity(noCurrencyIntent);
        assertTrue(!mTestRule.getActivity().mShoppingCartIcon.isShown());
    }

    @Test
    public void ifAmountIsNullFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("amount");

        mTestRule.launchActivity(invalidAmountIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifAmountIsNegativeFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.putExtra("amount", new BigDecimal(-100));

        mTestRule.launchActivity(invalidAmountIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPublicKeyIsNullFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("merchantPublicKey");

        mTestRule.launchActivity(invalidAmountIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPurchaseTitleIsNullHideShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("purchaseTitle");

        mTestRule.launchActivity(invalidAmountIntent);
        assertTrue(!mTestRule.getActivity().mShoppingCartIcon.isShown());
    }

    @Test
    public void ifNegativeMaxInstallmentsSetFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(-3);

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("paymentPreference", paymentPreference);

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifMaxInstallmentsSetAsZeroFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(0);

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("paymentPreference", paymentPreference);

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifNegativeDefaultInstallmentsSetFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(-3);

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("paymentPreference", paymentPreference);

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifDefaultInstallmentsSetAsZeroFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(0);

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("paymentPreference", paymentPreference);

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
    @Test
    public void ifAllPaymentTypesExcludedFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        List<String> excludedPaymentTypes = new ArrayList<String>(){{
            addAll(PaymentType.getAllPaymentTypes());
        }};

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);

        Intent invalidExclusionsIntent = new Intent();
        invalidExclusionsIntent.putExtras(validStartIntent.getExtras());
        invalidExclusionsIntent.putExtra("paymentPreference", paymentPreference);

        mTestRule.launchActivity(invalidExclusionsIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    //TODO caso de mismatching payment method ids

    //API EXCEPTIONS TEST
    @Test
    public void ifPaymentMethodSearchAPICallFailsFinishActivity() {
        mTestRule.addApiResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPaymentMethodsAPICallFailsFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 401, "");
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
    @Test
    public void whenCreateTokenAPIFailureFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");
        final Token token = new Token();
        token.setId("1");
        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("token", token);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        //TODO cambiar a flowcard
//        intending(hasComponent(GuessingNewCardActivity.class.getName())).respondWith(result);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
}
