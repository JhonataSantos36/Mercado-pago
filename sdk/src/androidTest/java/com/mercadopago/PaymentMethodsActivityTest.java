package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.ViewUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentMethodsActivityTest {

    @Rule
    public ActivityTestRule<PaymentMethodsActivity> mTestRule = new ActivityTestRule<>(PaymentMethodsActivity.class, true, false);
    private Intent validStartIntent;
    private FakeAPI mFakeAPI;
    private boolean mIntentsActive;

    @Before
    public void setValidStartIntent() {
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
        mIntentsActive = true;
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        if (mIntentsActive) {
            mIntentsActive = false;
            Intents.release();
        }
    }

    //Valid start

    @Test
    public void ifValidStartShowPaymentMethods() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        mTestRule.launchActivity(validStartIntent);

        // Validate view
        RecyclerView list = mTestRule.getActivity().mRecyclerView;
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Payment methods test failed, no items found");
        }
    }

    //Validations
    @Test
    public void ifPublicKeyNotSetShowErrorActivity() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        Intent intentWithoutPublicKey = new Intent(validStartIntent);
        intentWithoutPublicKey.removeExtra("merchantPublicKey");

        mTestRule.launchActivity(intentWithoutPublicKey);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //Payment method filters

    @Test
    public void ifPaymentTypesExcludedDoNotShowPaymentMethodsOfThatType() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(new ArrayList<String>() {{
            add("ticket");
        }});

        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);

        RecyclerView list = mTestRule.getActivity().mRecyclerView;
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i).getPaymentTypeId().equals("ticket")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Excluded payment types filter test failed, no items found");
        }
    }

    @Test
    public void ifDefaultPaymentTypesSetShowOnlyPaymentMethodsOfThatType() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentTypeId("ticket");

        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);

        RecyclerView list = mTestRule.getActivity().mRecyclerView;
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (!adapter.getItem(i).getPaymentTypeId().equals("ticket")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Default payment type filter test failed, no items found");
        }
    }

    @Test
    public void ifPaymentMethodsExcludedDoNotShowThem() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(new ArrayList<String>() {{
            add("visa");
            add("master");
        }});

        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);

        RecyclerView list = mTestRule.getActivity().mRecyclerView;
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i).getPaymentTypeId().equals("visa")
                        || adapter.getItem(i).getPaymentTypeId().equals("master")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Excluded payment types filter test failed, no items found");
        }
    }

    //Decoration

    @Test
    public void ifDecorationColorSetAndDarFontEnabledDecorateToolbar() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();

        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.enableDarkFont();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_light_grey));

        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(ViewUtils.getBackgroundColor(mTestRule.getActivity().mToolbar) == decorationPreference.getBaseColor());
        Assert.assertTrue(mTestRule.getActivity().mTitle.getCurrentTextColor() == decorationPreference.getDarkFontColor(mTestRule.getActivity()));
    }

    //Api failure recovery (e.g. timeouts)
    @Test
    public void onAPIFailureShowErrorScreen() {
        mFakeAPI.addResponseToQueue("", 401, "");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onAPIFailureAndUserRetrySucceededShowBankDeals() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue("", 401, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkErrorRetry)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        // Validate view
        RecyclerView list = mTestRule.getActivity().mRecyclerView;
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Payment methods test failed, no items found");
        }
    }

    @Test
    public void onAPIFailureAndUserCanceledFinishActivity() {

        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkExit)).perform(click());

        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    //Payment method selection

    @Test
    public void whenPaymentMethodSelectedFinishActivityWithOkResultAndPaymentMethod() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkPaymentMethodsList)).perform(
                actionOnItemAtPosition(0, click()));

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        String selectedPaymentMethodJson = JsonUtil.getInstance().toJson(paymentMethods.get(0));

        assertTrue(result.getResultCode() == Activity.RESULT_OK);
        assertTrue(selectedPaymentMethodJson.equals(result.getExtras().getString("paymentMethod")));
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    //Back pressed

    @Test (expected = NoActivityResumedException.class)
    public void onBackPressedFinishActivity() {
        mTestRule.launchActivity(validStartIntent);
        pressBack();
    }

    //Bank deals
    @Test
    public void ifShowBankDealsSetAndBankDealsClickedStartBankDealsActivity() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        validStartIntent.putExtra("showBankDeals", true);
        mTestRule.launchActivity(validStartIntent);

        String promotionsText = mTestRule.getActivity().getString(R.string.mpsdk_bank_deals);
        onView(withText(promotionsText)).perform(click());

        intended(hasComponent(BankDealsActivity.class.getName()));
    }

    @Test
    public void ifShowBankDealsSetFalseHideBankDealsText() {
        List<PaymentMethod> paymentMethods = StaticMock.getPaymentMethods();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        validStartIntent.putExtra("showBankDeals", false);
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mBankDealsTextView.getVisibility() == View.GONE);
    }
}
