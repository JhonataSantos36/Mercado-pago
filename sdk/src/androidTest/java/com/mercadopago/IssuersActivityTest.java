package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

    @BeforeClass
    static public void initialize(){
        Looper.prepare();
    }

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;
        mPaymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
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
        assertEquals(activity.mPresenter.getPublicKey(), mMerchantPublicKey);
        assertEquals(activity.mPresenter.getPaymentMethod().getId(), mPaymentMethod.getId());
    }

    @Test
    public void hideCardWhenNoTokenOrCard() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);

        mFakeAPI.addResponseToQueue(issuerList, 200, "");
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mLowResActive);
        assertNotNull(mTestRule.getActivity().mLowResToolbar);
        assertNull(mTestRule.getActivity().mNormalToolbar);
        assertNull(mTestRule.getActivity().mCardContainer);
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

        assertNotNull(mTestRule.getActivity().mCardContainer);
        assertNotNull(mTestRule.getActivity().mNormalToolbar);
        assertNotNull(mTestRule.getActivity().mFrontCardView);
        onView(withId(R.id.mpsdkCardFrontContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void showToolbarWithTitleWhenNoTokenOrCard() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        assertNotNull(mTestRule.getActivity().mLowResToolbar);
        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(R.string.mpsdk_card_issuers_title)));
    }

    @Test
    public void showCollapsingToolbarWhenToken() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        assertNull(mTestRule.getActivity().mLowResToolbar);
        assertNotNull(mTestRule.getActivity().mNormalToolbar);
        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCollapsingToolbar)).check(matches(isDisplayed()));
        String expected = mTestRule.getActivity().getApplicationContext().getResources().getString(R.string.mpsdk_card_issuers_title);
        assertEquals(mTestRule.getActivity().mNormalToolbar.getTitle().toString(), expected);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onView(withId(R.id.mpsdkCardLollipopImageView)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.mpsdkCardLowApiImageView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void finishOnInvalidParameterPaymentMethod() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;

        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("merchantPublicKey", mMerchantPublicKey);

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
        intended(hasComponent(ErrorActivity.class.getName()));
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
        sleep();
        assertEquals(referencesLayout.getAdapter().getItemCount(), issuerList.size());
    }

    private void sleep() {
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {

        }
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
        assertEquals(referencesLayout.getAdapter().getItemCount(), issuerList.size());
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

        int appBarColor = ViewUtils.getBackgroundColor(mTestRule.getActivity().mAppBar);
        assertEquals(appBarColor, decorationPreference.getLighterColor());
        int toolbarColor = ViewUtils.getBackgroundColor(mTestRule.getActivity().mNormalToolbar);
        assertEquals(toolbarColor, decorationPreference.getLighterColor());
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

        int color = ViewUtils.getBackgroundColor(mTestRule.getActivity().mLowResToolbar);
        assertEquals(color, (int)decorationPreference.getBaseColor());

        MPTextView toolbarTitle = (MPTextView) mTestRule.getActivity().findViewById(R.id.mpsdkTitle);
        int fontColor = toolbarTitle.getCurrentTextColor();
        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_white);
        assertEquals(fontColor, expectedColor);
    }

    @Test
    public void decorationPreferenceWithDarkFontAndTokenPaintsBackground() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        decorationPreference.enableDarkFont();
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        int appBarColor = ViewUtils.getBackgroundColor(mTestRule.getActivity().mAppBar);
        assertEquals(appBarColor, decorationPreference.getLighterColor());
        int toolbarColor = ViewUtils.getBackgroundColor(mTestRule.getActivity().mNormalToolbar);
        assertEquals(toolbarColor, decorationPreference.getLighterColor());
    }

    @Test
    public void decorationPreferenceWithDarkFontAndWithoutTokenPaintsToolbarAndTitle() {
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        decorationPreference.enableDarkFont();
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        int color = ViewUtils.getBackgroundColor(mTestRule.getActivity().mLowResToolbar);
        assertEquals(color, (int)decorationPreference.getBaseColor());

        MPTextView toolbarTitle = (MPTextView) mTestRule.getActivity().findViewById(R.id.mpsdkTitle);
        int fontColor = toolbarTitle.getCurrentTextColor();
        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_dark_font_color);
        assertEquals(fontColor, expectedColor);
    }

    //Timer
    @Test
    public void showCountDownTimerWhenItIsInitialized(){
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        CheckoutTimer.getInstance().start(60);

        mTestRule.launchActivity(validStartIntent);

        Assert.assertTrue(mTestRule.getActivity().findViewById(R.id.mpsdkTimerTextView).getVisibility() == View.VISIBLE);
        Assert.assertTrue(CheckoutTimer.getInstance().isTimerEnabled());
    }

    @Test
    public void finishActivityWhenSetOnFinishCheckoutListener(){
        String issuers = StaticMock.getIssuersJson();
        Type listType = new TypeToken<List<Issuer>>(){}.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        CheckoutTimer.getInstance().start(10);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                CheckoutTimer.getInstance().finishCheckout();
                Assert.assertTrue(mTestRule.getActivity().isFinishing());
            }
        });

        mTestRule.launchActivity(validStartIntent);
    }
}
