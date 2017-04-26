package com.mercadopago;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.mercadopago.constants.Sites;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.CustomMatchers;
import com.mercadopago.utils.ViewUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static com.mercadopago.utils.CustomMatchers.atPosition;
import static org.hamcrest.Matchers.allOf;

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
    public void ifPaymentMethodSearchIsEmptyShowErrorActivity() {
        PaymentMethodSearch paymentMethodSearchJson = new PaymentMethodSearch();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();

        mFakeAPI.addResponseToQueue(getDirectDiscount(), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        final PaymentMethodSearchItem firstSearchItem = paymentMethodSearch.getGroups().get(0);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    PaymentVaultActivity currentActivity = (PaymentVaultActivity) resumedActivities.iterator().next();
                    Assert.assertEquals(currentActivity.mPaymentVaultPresenter.getSelectedSearchItem().getId(), firstSearchItem.getId());
                }
            }
        });
    }

    @Test
    public void ifNavigationBackClickedGoBack() {

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer(3);
        mFakeAPI.addResponseToQueue(customer, 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(1, click()));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        onView(withId(R.id.mpsdkGroupsList))
                .check(matches(atPosition(0, CustomMatchers.withAnyChildText(paymentMethodSearch.getGroups().get(0).getDescription()))));
    }

    @Test
    public void ifUserSelectsSavedCardStartCardFlow() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer(3);

        mFakeAPI.addResponseToQueue(customer, 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        Gson gson = new Gson();
        validStartIntent.putExtra("cards", gson.toJson(customer.getCards()));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(actionOnItemAtPosition(0, click()));

        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    @Test
    public void ifPaymentMethodSearchAPICallFailsShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    // RESULTS TESTS

    //From PaymentMethodsActivity

    @Test
    public void ifAfterAPIFailureUserRetriesAndSucceedsThenShowPaymentMethodSelection() {
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();

        mFakeAPI.addResponseToQueue("", 401, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).check(matches(isDisplayed()));
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

    @Test
    public void showCountDownTimerWhenItIsInitialized() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(getDirectDiscount(), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        List<String> excludedTypes = new ArrayList<String>() {{
            add("ticket");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        CheckoutTimer.getInstance().start(60);

        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().findViewById(R.id.mpsdkTimerTextView).getVisibility() == View.VISIBLE);
        Assert.assertTrue(CheckoutTimer.getInstance().isTimerEnabled());
        Looper.myLooper().quit();
    }

    @Test
    public void whenSetOnFinishCheckoutListenerThenFinishActivity() {
        Looper.prepare();
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        List<String> excludedTypes = new ArrayList<String>() {{
            add("ticket");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        CheckoutTimer.getInstance().start(10);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                CheckoutTimer.getInstance().finishCheckout();
                Assert.assertTrue(mTestRule.getActivity().isFinishing());
                Looper.myLooper().quit();
            }
        });
        mTestRule.launchActivity(validStartIntent);
    }

    private Discount getDirectDiscount() {
        Discount discount = new Discount();
        discount.setCouponAmount(new BigDecimal("100"));
        discount.setId(123L);
        discount.setAmountOff(new BigDecimal("100"));
        return discount;
    }
}
