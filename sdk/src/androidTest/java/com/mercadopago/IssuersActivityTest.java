package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.ViewUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
        onView(withId(R.id.mpsdkCardHolderExpiryYear)).check(matches(withText(token.getExpirationYear().toString().substring(2,4))));
        onView(withId(R.id.mpsdkCardHolderExpiryMonth)).check(matches(withText(token.getExpirationMonth().toString())));
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

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer issuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertNull(issuer);
        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void finishWhenIssuerIsUnique() {
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer issuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), issuer.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);

    }

    @Test
    public void initializeIssuersWhenListSent() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);

        validStartIntent.putExtra("issuers", JsonUtil.getInstance().toJson(issuerList));
        mTestRule.launchActivity(validStartIntent);

        RecyclerView referencesLayout = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkActivityIssuersView);
        assertEquals(referencesLayout.getChildCount(), issuerList.size());
    }

    @Test
    public void finishWhenIssuerIsUniqueWhenListSent() {
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);

        validStartIntent.putExtra("issuers", JsonUtil.getInstance().toJson(issuerList));
        mTestRule.launchActivity(validStartIntent);

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void selectIssuerAndGetResult() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        Issuer mockedIssuerFirst = issuerList.get(0);

        validStartIntent.putExtra("issuers", JsonUtil.getInstance().toJson(issuerList));
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActivityIssuersView)).perform(actionOnItemAtPosition(0, click()));

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuerFirst.getId(), selectedIssuer.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

//    @Test
//    public void onBackClickedGoBack() {
//        String issuers = StaticMock.getIssuersJson();
//        Type listType = new TypeToken<List<Issuer>>(){}.getType();
//        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//
//        pressBack();
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
//        assertTrue(mTestRule.getActivity().isFinishing());
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//
//        }
//    }

    @Test
    public void ifApiFailureShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenReceivedResponseFromErrorRecover() {
        mFakeAPI.addResponseToQueue("", 401, "");
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        RecyclerView referencesLayout = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkActivityIssuersView);
        assertEquals(referencesLayout.getChildCount(), issuerList.size());
    }

    @Test
    public void whenReceivedResponseFromErrorFinish() {
        mFakeAPI.addResponseToQueue("", 401, "");
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkExit)).perform(click());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void issuerIsNullOnInvalidIssuerList() {
        String invalidJsonIssuers = "[{\"id\":288,\"name\":\"Tarjeta Shopping\"}{\"id\":319,\"name\":\"Citi\"}]";

        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        validStartIntent.putExtra("issuers", invalidJsonIssuers);
        mTestRule.launchActivity(validStartIntent);

        assertFalse(mFakeAPI.hasQueuedResponses());
    }

    @Test
    public void decorationPreferenceWithTokenPaintsBackground() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        FrameLayout cardBackground = (FrameLayout) mTestRule.getActivity().findViewById(R.id.mpsdkCardBackground);
        int color = ViewUtils.getBackgroundColor(cardBackground);
        assertEquals(color, decorationPreference.getLighterColor());
    }

    @Test
    public void decorationPreferenceWithoutTokenPaintsToolbar() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        Toolbar toolbar = (Toolbar) mTestRule.getActivity().findViewById(R.id.mpsdkRegularToolbar);
        int color = ViewUtils.getBackgroundColor(toolbar);
        assertEquals(color, (int)decorationPreference.getBaseColor());
    }


}
