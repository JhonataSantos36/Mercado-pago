package com.mercadopago;

import android.content.Intent;
import android.os.Looper;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * Created by mromar on 11/21/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SecurityCodeActivityTest {

    @Rule
    public ActivityTestRule<SecurityCodeActivity> mTestRule = new ActivityTestRule<>(SecurityCodeActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;

    private FakeAPI mFakeAPI;

    @BeforeClass
    static public void initialize(){
        Looper.prepare();
    }

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_TEST_PUBLIC_KEY;

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
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

    //Recoverable Token
    @Test
    public void askThreeDigitsAndCloneTokenWhenTokenIsReceived() {
        Token token = StaticMock.getToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        Token mockedClonedToken = StaticMock.getClonedToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedClonedToken), 200, "");

        Token mockedPutSecurityCodeToken = StaticMock.getClonedToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedPutSecurityCodeToken), 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkSecurityCodeNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);

        assertFalse(tokenResult.getId().equals(token.getId()));
    }

    @Test
    public void askFourDigitsAndCloneTokenWhenAmexTokenIsReceived() {
        Token token = StaticMock.getTokenAmex();
        PaymentMethod paymentMethod = StaticMock.getAmexPaymentMethodOn();

        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        Token mockedClonedToken = StaticMock.getClonedTokenAmex();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedClonedToken), 200, "");

        Token mockedPutSecurityCodeToken = StaticMock.getClonedTokenAmex();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedPutSecurityCodeToken), 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE_FOUR_DIGITS));
        onView(withId(R.id.mpsdkSecurityCodeNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);

        assertFalse(tokenResult.getId().equals(token.getId()));
    }

    @Test
    public void showErrorWhenSecurityCodeHaveToBeThreeDigitsAndEnterTwoDigits() {
        Token token = StaticMock.getToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE.substring(0, 1)));
        onView(withId(R.id.mpsdkSecurityCodeNextButton)).perform(click());
        onView(withId(R.id.mpsdkSecurityCodeErrorText)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenSecurityCodeHaveToBeFourDigitsAndEnterThreeDigits() {
        Token token = StaticMock.getTokenAmex();
        PaymentMethod paymentMethod = StaticMock.getAmexPaymentMethodOn();

        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE_FOUR_DIGITS.substring(0, 2)));
        onView(withId(R.id.mpsdkSecurityCodeNextButton)).perform(click());
        onView(withId(R.id.mpsdkSecurityCodeErrorText)).check(matches(isDisplayed()));
    }

    //Timer
    @Test
    public void showCountDownTimerWhenItIsInitialized(){

        Token token = StaticMock.getToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

        CheckoutTimer.getInstance().start(60);

        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().findViewById(R.id.mpsdkTimerTextView).getVisibility() == View.VISIBLE);
        assertTrue(CheckoutTimer.getInstance().isTimerEnabled());
    }

    @Test
    public void finishActivityWhenSetOnFinishCheckoutListener(){
        Token token = StaticMock.getToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

        CheckoutTimer.getInstance().start(10);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                CheckoutTimer.getInstance().finishCheckout();
                assertTrue(mTestRule.getActivity().isFinishing());
            }
        });

        mTestRule.launchActivity(validStartIntent);
    }
}
