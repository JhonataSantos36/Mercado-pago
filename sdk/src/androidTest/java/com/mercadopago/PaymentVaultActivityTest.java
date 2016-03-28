package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;


import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentType;
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
        Assert.assertTrue(mTestRule.getActivity().mMerchantPublicKey != null);
    }

    @Test
    public void setEditingStateOnCreate() {
        validStartIntent.putExtra("editing", true);
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mEditing);
    }


    @Test
    public void setEditingFalseByDefault() {
        mTestRule.launchActivity(validStartIntent);
        Assert.assertFalse(mTestRule.getActivity().mEditing);
    }

    @Test
    public void setAmountOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mAmount != null);
    }

    @Test
    public void setPurchaseTitleOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mPurchaseTitle != null);
    }

    @Test
    public void ifPurchaseTitleLengthIsOverMaxTruncateIt() {

        Integer purchaseTitleMaxLength = PaymentVaultActivity.PURCHASE_TITLE_MAX_LENGTH;

        StringBuilder largeTitleBuilder = new StringBuilder();
        for (int i = 0; i < purchaseTitleMaxLength + 10; i++) {
            largeTitleBuilder.append("a");
        }
        String reallyLongTitle = largeTitleBuilder.toString();

        validStartIntent.putExtra("purchaseTitle", reallyLongTitle);

        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().mShoppingCartController.getPurchaseTitle().length() <= purchaseTitleMaxLength);
    }

    @Test
    public void setMaxInstallmentsOnCreateIfReceived() {
        validStartIntent.putExtra("maxInstallments", "3");
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mMaxInstallments == 3);
    }

    @Test
    public void setDefaultInstallmentsOnCreateIfReceived() {
        validStartIntent.putExtra("defaultInstallments", "3");
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mDefaultInstallments == 3);
    }

    @Test
    public void setExcludedPaymentTypesOnCreateIfReceived() {
        List<String> excludedTypes = new ArrayList<String>() {{
            add("ticket");
        }};
        String exclusions = JsonUtil.parseList(excludedTypes);
        validStartIntent.putExtra("excludedPaymentTypes", exclusions);
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mExcludedPaymentTypes.contains("ticket"));
    }

    @Test
    public void setExcludedPaymentMethodsOnCreateIfReceived() {
        List<String> excludedPaymentMethods = new ArrayList<String>() {{
            add("oxxo");
        }};
        String exclusions = JsonUtil.parseList(excludedPaymentMethods);
        validStartIntent.putExtra("excludedPaymentMethodIds", exclusions);
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mExcludedPaymentMethodIds.contains("oxxo"));
    }

    @Test
    public void initializeGroupsRecyclerViewOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView != null);
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.getActivity().mPaymentMethodSearch != null);
    }

    @Test
    public void ifPaymentMethodSearchIsEmptyFinishActivity() {
        PaymentMethodSearch paymentMethodSearchJson = new PaymentMethodSearch();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void whenPaymentMethodSearchHasGroupsFillGroupsRecyclerView() {
        //Prepare API responses
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void ifSelectedSearchItemReceivedReturnIsItemSelectedTrue() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        validStartIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().isItemSelected());
    }

    @Test
    public void ifSelectedSearchItemReceivedDoNotRetrievePaymentMethodSearch() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(json, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        validStartIntent.putExtra("selectedSearchItem", paymentMethodSearch.getGroups().get(0));
        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().mPaymentMethodSearch == null);
    }

    @Test
    public void ifSelectedSearchItemReceivedShowItsChildren() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0);
        validStartIntent.putExtra("selectedSearchItem", item);
        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView.getAdapter().getItemCount() != 0);
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

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
                    Assert.assertEquals(currentActivity.mSelectedSearchItem.getId(), firstSearchItem.getId());
                }
            }
        });
    }

    @Test
    public void whenItemSelectedIsCardTypeStartGuessingNewCardActivityWithPublicKeyAndPaymentTypeId() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);

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
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        //remove children
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);

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
        String paymentMethodsJson = StaticMock.getCompletePaymentMethodsJson();


        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodsJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

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
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        final Token token = new Token();
        token.setId("1");
        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

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

    @Test
    public void setResultFromGuessingNewCardWithIssuer() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        final Token token = new Token();
        token.setId("1");
        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");
        final Issuer issuer = new Issuer();
        issuer.setId((long) 1234);

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        guessingFormResultIntent.putExtra("issuer", issuer);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

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
                    Assert.assertEquals(currentActivity.mSelectedIssuer.getId(), issuer.getId());
                }
            }
        });
    }

    @Test
    public void whenInitializedDoNotShowShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);

        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);
    }

    @Test
    public void testOpenShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);
        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertEquals(itemInfoLayout.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testOpenAndCloseShoppingCart() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        PaymentVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);
        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertEquals(itemInfoLayout.getVisibility(), View.VISIBLE);


        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);
    }

    //VALIDATIONS TESTS

    @Test
    public void ifCurrencyIdIsInvalidFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidCurrencyIntent = new Intent();
        invalidCurrencyIntent.putExtras(validStartIntent.getExtras());
        invalidCurrencyIntent.putExtra("currencyId", "An invalid currency id");

        mTestRule.launchActivity(invalidCurrencyIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }


    @Test
    public void ifAmountIsNullFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("amount");

        mTestRule.launchActivity(invalidAmountIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifAmountIsNegativeFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.putExtra("amount", new BigDecimal(-100));

        mTestRule.launchActivity(invalidAmountIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPublicKeyIsNullFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("merchantPublicKey");

        mTestRule.launchActivity(invalidAmountIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifNegativeMaxInstallmentsSetFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("maxInstallments", "-3");

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifMaxInstallmentsSetAsZeroFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("maxInstallments", "0");

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifNegativeDefaultInstallmentsSetFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("defaultInstallments", "-3");

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifDefaultInstallmentsSetAsZeroFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("defaultInstallments", "0");

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
    @Test
    public void ifAllPaymentTypesExcludedFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(new PaymentMethod());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        List<String> excludedPaymentTypes = new ArrayList<String>(){{
            addAll(PaymentType.getAllPaymentTypes());
        }};

        Intent invalidExclusionsIntent = new Intent();
        invalidExclusionsIntent.putExtras(validStartIntent.getExtras());
        invalidExclusionsIntent.putExtra("excludedPaymentTypes", JsonUtil.parseList(excludedPaymentTypes));

        mTestRule.launchActivity(invalidExclusionsIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    //API EXCEPTIONS TEST
    @Test
    public void ifPaymentMethodSearchAPICallFailsFinishActivity() {
        mTestRule.addApiResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPaymentMethodsAPICallFailsFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
}
