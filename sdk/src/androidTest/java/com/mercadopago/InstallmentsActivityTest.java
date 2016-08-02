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
import com.mercadopago.constants.Sites;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
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
import java.math.BigDecimal;
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
import static com.mercadopago.utils.ActivityResultUtil.getActivityResult;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by vaserber on 7/12/16.
 */
public class InstallmentsActivityTest {

    @Rule
    public ActivityTestRule<InstallmentsActivity> mTestRule = new ActivityTestRule<>(InstallmentsActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;
    private Site mSite;
    private String mAmount;

    private FakeAPI mFakeAPI;

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;
        mPaymentMethod = StaticMock.getPaymentMethodOn();
        mSite = Sites.ARGENTINA;
        mAmount = "1000";

        validStartIntent = new Intent();
        validStartIntent.putExtra("publicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        validStartIntent.putExtra("amount", mAmount);
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
    public void getActivityParametersOnCreateIsValid() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

        InstallmentsActivity activity = mTestRule.launchActivity(validStartIntent);

        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertEquals(activity.mCurrentPaymentMethod.getId(), mPaymentMethod.getId());
        assertEquals(activity.mSite.getId(), mSite.getId());
        assertEquals(activity.mAmount, new BigDecimal(mAmount));
        assertNotNull(activity.mPayerCosts);
        assertEquals(activity.mPayerCosts.size(), payerCostList.size());
        assertNull(activity.mSelectedIssuer);
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithoutPayerCostsButWithIssuerIsValid() {
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>(){}.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        InstallmentsActivity activity = mTestRule.launchActivity(validStartIntent);

        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertEquals(activity.mCurrentPaymentMethod.getId(), mPaymentMethod.getId());
        assertEquals(activity.mSite.getId(), mSite.getId());
        assertEquals(activity.mAmount, new BigDecimal(mAmount));
        assertEquals(activity.mSelectedIssuer.getId(), issuer.getId());
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void hideCardWhenNoToken() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        mTestRule.launchActivity(validStartIntent);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {

        }

        onView(withId(R.id.mpsdkCardBackground)).check(matches(not(isDisplayed())));
    }

    @Test
    public void hideCardWhenNoPaymentMethod() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        Token token = StaticMock.getToken();
        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        validStartIntent.removeExtra("paymentMethod");

        mTestRule.launchActivity(validStartIntent);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {

        }

        onView(withId(R.id.mpsdkCardBackground)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showCardWhenTokenAndPaymentMethod() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);
        Token token = StaticMock.getToken();

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkCardBackground)).check(matches(isDisplayed()));
    }

    @Test
    public void showToolbarWithTitleWhenNoToken() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkToolbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(R.string.mpsdk_card_installments_title)));
    }

    @Test
    public void showTransparentToolbarWhenToken() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);
        Token token = StaticMock.getToken();

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkRegularToolbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkToolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void initializeCardWhenToken() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);
        Token token = StaticMock.getToken();

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumberTextView)).check(matches(withText(containsString(token.getLastFourDigits()))));
        onView(withId(R.id.mpsdkCardholderNameView)).check(matches(withText(token.getCardholder().getName().toUpperCase())));
        onView(withId(R.id.mpsdkCardHolderExpiryYear)).check(matches(withText(token.getExpirationYear().toString().substring(2,4))));
        onView(withId(R.id.mpsdkCardHolderExpiryMonth)).check(matches(withText(token.getExpirationMonth().toString())));
    }

    @Test
    public void ifPayerCostsSetAllowStartWithoutPaymentMethod() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        Issuer issuer = StaticMock.getIssuer();

        Intent intent = new Intent();
        intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        intent.putExtra("publicKey", mMerchantPublicKey);
        intent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        intent.putExtra("amount", mAmount);
        intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

        mTestRule.launchActivity(intent);
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void ifPayerCostListSetAllowStartWithoutPublicKey() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        Issuer issuer = StaticMock.getIssuer();

        Intent intent = new Intent();
        intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        intent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        intent.putExtra("amount", mAmount);
        intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));

        mTestRule.launchActivity(intent);
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void ifPayerCostListSetAllowStartWithoutIssuer() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        Issuer issuer = StaticMock.getIssuer();

        Intent intent = new Intent();
        intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        intent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        intent.putExtra("amount", mAmount);
        intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

        mTestRule.launchActivity(intent);
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void ifPayerCostListNotSetFinishOnInvalidParameterPublicKey() {

        Issuer issuer = StaticMock.getIssuer();

        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        invalidIntent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        invalidIntent.putExtra("amount", mAmount);
        invalidIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));

        mTestRule.launchActivity(invalidIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void ifPayerCostsNotSetFinishOnInvalidParameterPaymentMethod() {

        Issuer issuer = StaticMock.getIssuer();

        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        invalidIntent.putExtra("publicKey", mMerchantPublicKey);
        invalidIntent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        invalidIntent.putExtra("amount", mAmount);

        mTestRule.launchActivity(invalidIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void ifPayerCostsNotSetFinishOnInvalidParameterIssuer() {
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>(){}.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        InstallmentsActivity activity = mTestRule.launchActivity(validStartIntent);

        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertEquals(activity.mCurrentPaymentMethod.getId(), mPaymentMethod.getId());
        assertEquals(activity.mSite.getId(), mSite.getId());
        assertEquals(activity.mAmount, new BigDecimal(mAmount));
        assertNull(activity.mSelectedIssuer);
        assertNull(activity.mPayerCosts);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishOnInvalidParameterAmount() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        Intent invalidIntent = new Intent();
        invalidIntent.putExtra("publicKey", mMerchantPublicKey);
        invalidIntent.putExtra("site", JsonUtil.getInstance().toJson(mSite));
        invalidIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));
        invalidIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));

        mTestRule.launchActivity(invalidIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showErrorOnEmptyInstallmentsAsyncResponse() {
        List<Installment> installmentList = new ArrayList<>();
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void showErrorOnMultipleInstallmentsAsyncResponse() {
        List<Installment> installmentList = new ArrayList<Installment>();
        installmentList.add(new Installment());
        installmentList.add(new Installment());

        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void initializePayerCostsWhenListSent() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);

        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

        mTestRule.launchActivity(validStartIntent);

        RecyclerView referencesLayout = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkActivityInstallmentsView);
        assertEquals(referencesLayout.getChildCount(), payerCostList.size());
    }

    @Test
    public void selectPayerCostAndGetResult() {
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>(){}.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        List<PayerCost> payerCosts = installmentList.get(0).getPayerCosts();
        PayerCost mockedPayerCostFirst = payerCosts.get(0);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActivityInstallmentsView)).perform(actionOnItemAtPosition(0, click()));

        ActivityResult result = getActivityResult(mTestRule.getActivity());
        PayerCost selectedPayerCost = JsonUtil.getInstance().fromJson(result.getExtras().getString("payerCost"), PayerCost.class);
        assertEquals(mockedPayerCostFirst.getRecommendedMessage(), selectedPayerCost.getRecommendedMessage());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void ifApiFailureShowErrorActivity() {
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifAfterApiFailureUserRetriesAndSucceedsShowPayerCosts() {
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        mFakeAPI.addResponseToQueue("", 401, "");
        String installmentsJson = StaticMock.getInstallmentsJson();
        mFakeAPI.addResponseToQueue(installmentsJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        //Prepare assertion data
        Type listType = new TypeToken<List<Installment>>(){}.getType();
        List<Installment> installmentsList = JsonUtil.getInstance().getGson().fromJson(installmentsJson, listType);


        RecyclerView referencesLayout = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkActivityInstallmentsView);
        assertEquals(referencesLayout.getChildCount(), installmentsList.get(0).getPayerCosts().size());
    }

    @Test
    public void decorationPreferenceWithTokenPaintsBackground() {
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);
        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

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
        String payerCosts = StaticMock.getPayerCostsJson();
        Type listType = new TypeToken<List<PayerCost>>(){}.getType();
        List<PayerCost> payerCostList = JsonUtil.getInstance().getGson().fromJson(payerCosts, listType);
        validStartIntent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCostList));

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        Toolbar toolbar = (Toolbar) mTestRule.getActivity().findViewById(R.id.mpsdkRegularToolbar);
        int color = ViewUtils.getBackgroundColor(toolbar);
        assertEquals(color, (int)decorationPreference.getBaseColor());
    }

    @Test
    public void whenDefaultPayerCostFoundSetItAsResult() {
        //Add API response
        String installmentsJson = StaticMock.getInstallmentsJson();
        mFakeAPI.addResponseToQueue(installmentsJson, 200, "");

        //Set default installment
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(3);

        //Prepare start intent
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        //Prepare assertion data
        Type listType = new TypeToken<List<Installment>>(){}.getType();
        List<Installment> installmentsList = JsonUtil.getInstance().getGson().fromJson(installmentsJson, listType);

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());
        String payerCostJson = JsonUtil.getInstance().toJson(installmentsList.get(0).getPayerCosts().get(1));

        //Assertions
        assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
        assertEquals(payerCostJson, activityResult.getExtras().getString("payerCost"));
    }

    @Test
    public void whenEmptyInstallmentsReceivedStartErrorActivity() {
        //Add API response
        List<Installment> installments = new ArrayList<>();
        mFakeAPI.addResponseToQueue(installments, 200, "");

        //Prepare start intent
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenEmptyPayerCostsReceivedStartErrorActivity() {
        //Add API response
        List<Installment> installments = new ArrayList<>();
        installments.add(new Installment());
        List<PayerCost> payerCosts = new ArrayList<>();
        installments.get(0).setPayerCosts(payerCosts);
        mFakeAPI.addResponseToQueue(installments, 200, "");

        //Prepare start intent
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenOnlyOnePayerCostAvailableFinishWithItAsResult() {
        //Prepare API response
        List<Installment> installments = new ArrayList<>();
        installments.add(new Installment());

        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        List<PayerCost> payerCosts = new ArrayList<>();
        payerCosts.add(payerCost);

        installments.get(0).setPayerCosts(payerCosts);

        //Add API response
        mFakeAPI.addResponseToQueue(installments, 200, "");

        //Prepare start intent
        Issuer issuer = StaticMock.getIssuer();
        validStartIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        mTestRule.launchActivity(validStartIntent);

        //Prepare assertion data

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());
        String payerCostJson = JsonUtil.getInstance().toJson(installments.get(0).getPayerCosts().get(0));

        //Assertions
        assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
        assertEquals(payerCostJson, activityResult.getExtras().getString("payerCost"));
    }
}
