package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.BankDeal;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BankDealsActivityTest {

    @Rule
    public ActivityTestRule<BankDealsActivity> mTestRule = new ActivityTestRule<>(BankDealsActivity.class, true, false);
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

    //Bank deals interaction
    @Test
    public void ifBankDealsReceivedShowThemInRecyclerView() {
        String bankDealsJson = StaticMock.getBankDealsJson();
        mFakeAPI.addResponseToQueue(bankDealsJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        // Validate view
        RecyclerView list = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkBankDealsList);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Bank Deals test failed, no items found");
        }
    }

    @Test
    public void onBankDealSelectedStartTermsAndConditionsActivityWithBankDealLegals() {
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        mFakeAPI.addResponseToQueue(bankDeals, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkBankDealsList)).perform(
                actionOnItemAtPosition(0, click()));

        String legals = bankDeals.get(0).getLegals();

        intended(allOf(hasComponent(TermsAndConditionsActivity.class.getName()), hasExtra("termsAndConditions", legals)));
    }

    //Initial validations
    @Test
    public void onStartWithoutPublicKeyShowErrorScreen() {
        Intent invalidStartIntent = new Intent(validStartIntent);
        invalidStartIntent.removeExtra("merchantPublicKey");

        mTestRule.launchActivity(invalidStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //Recover from API failure
    @Test
    public void onAPIFailureShowErrorScreen() {
        mFakeAPI.addResponseToQueue("", 401, "");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onAPIFailureAndUserRetrySucceededShowBankDeals() {
        String bankDealsJson = StaticMock.getBankDealsJson();
        mFakeAPI.addResponseToQueue("", 401, "");
        mFakeAPI.addResponseToQueue(bankDealsJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkErrorRetry)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        // Validate view
        RecyclerView list = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkBankDealsList);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Bank Deals test failed, no items found");
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

    //Canceled
    @Test (expected = NoActivityResumedException.class)
    public void ifUserCancelsFinish() {
        String bankDealsJson = StaticMock.getBankDealsJson();
        mFakeAPI.addResponseToQueue(bankDealsJson, 200, "");
        mTestRule.launchActivity(validStartIntent);
        pressBack();
    }

    @Test
    public void ifUserPressesBackInTermAndConditionsShowBankDeals() {
        String bankDealsJson = StaticMock.getBankDealsJson();
        mFakeAPI.addResponseToQueue(bankDealsJson, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkBankDealsList)).perform(actionOnItemAtPosition(0, click()));

        pressBack();

        onView(withId(R.id.mpsdkBankDealsList)).check(matches(isDisplayed()));
    }
}
