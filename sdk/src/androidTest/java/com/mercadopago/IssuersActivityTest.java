package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by vaserber on 7/7/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class IssuersActivityTest {

    @Rule
    public ActivityTestRule<IssuersActivity> mTestRule = new ActivityTestRule<>(IssuersActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;
    private FakeAPI mFakeAPI;

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;
        mPaymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent = new Intent();
        validStartIntent.putExtra("publicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
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
    public void getActivityParametersOnCreate() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        IssuersActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertEquals(activity.mCurrentPaymentMethod.getId(), mPaymentMethod.getId());
    }

    @Test
    public void hideCardWhenNoToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkCardBackground)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showCardWhenToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkCardBackground)).check(matches(isDisplayed()));
    }

    @Test
    public void showToolbarWithTitleWhenNoToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkToolbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(R.string.mpsdk_card_issuers_title)));

    }

    @Test
    public void showTransparentToolbarWhenToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkToolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void initializeCardWhenToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumberTextView)).check(matches(withText(containsString(token.getLastFourDigits()))));
        onView(withId(R.id.mpsdkCardholderNameView)).check(matches(withText(token.getCardholder().getName().toUpperCase())));
//        onView(withId(R.id.mpsdkCardHolderExpiryYear)).check(matches(withText(token.getExpirationYear().toString().substring(2,4))));
//        onView(withId(R.id.mpsdkCardHolderExpiryMonth)).check(matches(withText(token.getExpirationMonth())));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void finishOnInvalidParameterPaymentMethod() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;

        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("publicKey", mMerchantPublicKey);

        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(invalidIntent);

        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishOnInvalidParameterPublicKey() {
        mPaymentMethod = StaticMock.getPaymentMethodOn();
        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));

        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(invalidIntent);

        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishOnEmptyIssuersAsyncResponse() {
        List<Issuer> issuerList = new ArrayList<>();
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }
}
