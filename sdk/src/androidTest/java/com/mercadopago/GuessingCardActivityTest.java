package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.DummyCard;
import com.mercadopago.model.DummyIdentificationType;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.CardTestUtils;
import com.mercadopago.utils.IdentificationTestUtils;
import com.mercadopago.utils.ViewUtils;
import com.mercadopago.views.MPTextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vaserber on 7/15/16.
 */
public class GuessingCardActivityTest {

    @Rule
    public ActivityTestRule<GuessingCardActivity> mTestRule = new ActivityTestRule<>(GuessingCardActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;

    private FakeAPI mFakeAPI;

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_TEST_PUBLIC_KEY;

        validStartIntent = new Intent();
        validStartIntent.putExtra("publicKey", mMerchantPublicKey);
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
        addBankDealsCall();
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        GuessingCardActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertNotNull(activity.mPaymentMethodList);
        assertEquals(activity.mPaymentMethodList.size(), paymentMethodList.size());
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithPaymentMethodListIsValid() {
        addBankDealsCall();
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);

        validStartIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));

        GuessingCardActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mPublicKey, mMerchantPublicKey);
        assertNotNull(activity.mPaymentMethodList);
        assertEquals(activity.mPaymentMethodList.size(), paymentMethodList.size());
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithTokenIsValid() {
        addBankDealsCall();
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);
        validStartIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));
        Token token = StaticMock.getTokenAmex();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        String contained = CardTestUtils.getMockedBinInFront(token.getFirstSixDigits());

        onView(withId(R.id.mpsdkCardNumberTextView)).check(matches(withText(containsString(contained))));
        onView(withId(R.id.mpsdkCardholderNameView)).check(matches(withText(token.getCardholder().getName().toUpperCase())));
        onView(withId(R.id.mpsdkCardHolderExpiryYear)).check(matches(withText(token.getExpirationYear().toString().substring(2, 4))));
        onView(withId(R.id.mpsdkCardHolderExpiryMonth)).check(matches(withText(token.getExpirationMonth().toString())));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        if (onView(withId(R.id.mpsdkCardSecurityView)).check(matches(isDisplayed())).equals(true)) {
            onView(withId(R.id.mpsdkCardSecurityView)).check(matches(withText(StaticMock.SECURITY_CODE_FRONT_HOLDER)));
        }
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithDecorationPreference() {
        addBankDealsCall();
        addPaymentMethodsCall();

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }

        FrameLayout cardBackground = (FrameLayout) mTestRule.getActivity().findViewById(R.id.mpsdkCardBackground);
        int color = ViewUtils.getBackgroundColor(cardBackground);
        assertEquals(color, decorationPreference.getLighterColor());
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithDecorationPreferenceDarkFont() {
        addBankDealsCall();
        addPaymentMethodsCall();

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        decorationPreference.enableDarkFont();
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        MPTextView toolbarTitle = (MPTextView) mTestRule.getActivity().findViewById(R.id.mpsdkButtonText);
        int fontColor = toolbarTitle.getCurrentTextColor();
        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_dark_font_color);
        assertEquals(fontColor, expectedColor);
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithEmptyPaymentPreferenceIsValid() {
        addBankDealsCall();
        addPaymentMethodsCall();

        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(StaticMock.getEmptyPaymentPreference()));

        mTestRule.launchActivity(validStartIntent);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithPaymentMethodExclusionNotSupported() {
        addBankDealsCall();
        addPaymentMethodsCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add("visa");
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("visa").getCardNumber()));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorTextView)).check(matches(withText(R.string.mpsdk_invalid_payment_method)));
    }

    @Test
    public void initializeWithoutPublicKeyIsInvalid() {
        Intent invalidStartIntent = new Intent();
        addBankDealsCall();
        addPaymentMethodsCall();

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithPaymentMethodExclusionSupported() {
        addInitCalls();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add("visa");
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(not(isDisplayed())));

    }

    @Test
    public void initializeWithPaymentTypeExclusionNotSupported() {
        addBankDealsCall();
        addPaymentMethodsCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("credit_card");
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("visa").getCardNumber()));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorTextView)).check(matches(withText(R.string.mpsdk_invalid_payment_method)));
    }

    @Test
    public void initializeWithPaymentTypeExclusionSupported() {
        addInitCalls();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("debit_card");
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showBankDealsOnlyCardSteps() {
        List<DummyCard> dummyCards = CardTestUtils.getSomeCards();

        List<BankDeal> bankDeals = StaticMock.getBankDeals();

        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);

        String identificationTypes = StaticMock.getIdentificationTypeList();
        Type listType2 = new TypeToken<List<IdentificationType>>() {
        }.getType();
        List<IdentificationType> identificationTypeList = JsonUtil.getInstance().getGson().fromJson(identificationTypes, listType2);

        for (DummyCard card : dummyCards) {
            mFakeAPI.addResponseToQueue(bankDeals, 200, "");
            mFakeAPI.addResponseToQueue(paymentMethodList, 200, "");
            mFakeAPI.addResponseToQueue(identificationTypeList, 200, "");
            Activity activity = mTestRule.launchActivity(validStartIntent);

            onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
            onView(withId(R.id.mpsdkButtonText)).check(matches(isDisplayed()));
            onView(withId(R.id.mpsdkButtonText)).check(matches(withText(R.string.mpsdk_bank_deals_action)));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
            onView(withId(R.id.mpsdkButtonText)).check(matches(isDisplayed()));
            onView(withId(R.id.mpsdkButtonText)).check(matches(withText(R.string.mpsdk_bank_deals_action)));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkButtonText)).check(matches(not(isDisplayed())));

            activity.finish();
        }
    }

    @Test
    public void onBankDealsEmptyDontShowBankDealsOnToolbar() {
        List<DummyCard> dummyCards = CardTestUtils.getSomeCards();

        List<BankDeal> bankDeals = new ArrayList<>();

        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);

        String identificationTypes = StaticMock.getIdentificationTypeList();
        Type listType2 = new TypeToken<List<IdentificationType>>() {
        }.getType();
        List<IdentificationType> identificationTypeList = JsonUtil.getInstance().getGson().fromJson(identificationTypes, listType2);

        for (DummyCard card : dummyCards) {
            mFakeAPI.addResponseToQueue(bankDeals, 200, "");
            mFakeAPI.addResponseToQueue(paymentMethodList, 200, "");
            mFakeAPI.addResponseToQueue(identificationTypeList, 200, "");
            Activity activity = mTestRule.launchActivity(validStartIntent);

            onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
            onView(withId(R.id.mpsdkButtonText)).check(matches(not(isDisplayed())));

            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
            onView(withId(R.id.mpsdkButtonText)).check(matches(not(isDisplayed())));

            onView(withId(R.id.mpsdkNextButton)).perform(click());
            onView(withId(R.id.mpsdkButtonText)).check(matches(not(isDisplayed())));

            activity.finish();
        }
    }

    @Test
    public void showNextAndBackButtonsOnStart() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkNextButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackInactiveButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkNextButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackInactiveButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkEditTextNavigationWithValidData() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        cardNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        cardholderNameIsCurrentEditText();
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        expiryDateIsCurrentEditText();
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        identificationNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkBackButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkBackButton)).perform(click());

        expiryDateIsCurrentEditText();
        onView(withId(R.id.mpsdkBackButton)).perform(click());

        cardholderNameIsCurrentEditText();
        onView(withId(R.id.mpsdkBackButton)).perform(click());

        cardNumberIsCurrentEditText();
    }

    private void identificationNumberIsCurrentEditText() {
        onView(withId(R.id.mpsdkCardNumber)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardholderName)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationType)).check(matches((isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationNumber)).check(matches((isDisplayed())));

        onView(withId(R.id.mpsdkCardIdentificationNumber)).check(matches(hasFocus()));
    }

    private void securityCodeIsCurrentEditText() {
        onView(withId(R.id.mpsdkCardNumber)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardholderName)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(hasFocus()));
    }

    private void expiryDateIsCurrentEditText() {
        onView(withId(R.id.mpsdkCardNumber)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardholderName)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardIdentificationNumber)).check(matches(not(isDisplayed())));

        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(hasFocus()));
    }

    private void cardholderNameIsCurrentEditText() {
        onView(withId(R.id.mpsdkCardNumber)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardholderName)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationType)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationNumber)).check(matches(not(isDisplayed())));

        onView(withId(R.id.mpsdkCardholderName)).check(matches(hasFocus()));
    }

    private void cardNumberIsCurrentEditText() {
        onView(withId(R.id.mpsdkCardNumber)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationType)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardIdentificationNumber)).check(matches(not(isDisplayed())));

        onView(withId(R.id.mpsdkCardNumber)).check(matches(hasFocus()));
    }

    @Test
    public void checkCardNumberNavigationWithInvalidData() {
        addInitCalls();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        cardNumberIsCurrentEditText();
        for (String invalidNumber : CardTestUtils.getInvalidCardNumbers()) {
            checkCardNumber(invalidNumber);
            cardNumberIsCurrentEditText();
        }
    }

    private void checkCardNumber(String number) {
        onView(withId(R.id.mpsdkCardNumber)).perform(clearText());
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(number));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
    }

    @Test
    public void checkCardholderNameNavigationWithInvalidData() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        cardholderNameIsCurrentEditText();
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(""));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        cardholderNameIsCurrentEditText();
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        expiryDateIsCurrentEditText();
    }

    @Test
    public void checkExpiryDateNavigationWithInvalidData() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        expiryDateIsCurrentEditText();
        for (String invalidDate : CardTestUtils.getInvalidExpiryDates()) {
            checkExpiryDateIsInvalid(invalidDate, onView(withId(R.id.mpsdkNextButton)));
            checkExpiryDateIsInvalid(invalidDate, onView(withId(R.id.mpsdkBackButton)));
        }
    }

    private void fillExpiryDate(String date) {
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(clearText());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(date));
    }

    private void checkExpiryDateIsInvalid(String expiryDate, ViewInteraction viewInteraction) {
        fillExpiryDate(expiryDate);
        viewInteraction.perform(click());
        expiryDateIsCurrentEditText();
    }

    @Test
    public void checkSecurityCodeNavigationWithInvalidData() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        securityCodeIsCurrentEditText();
        for (String invalidSecurityCode : CardTestUtils.getInvalidSecurityCodes()) {
            checkSecurityCodeIsInvalid(invalidSecurityCode, onView(withId(R.id.mpsdkNextButton)));
            checkSecurityCodeIsInvalid(invalidSecurityCode, onView(withId(R.id.mpsdkBackButton)));
        }
    }

    private void fillSecurityCode(String code) {
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(clearText());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(code));
    }

    private void checkSecurityCodeIsInvalid(String securityCode, ViewInteraction viewInteraction) {
        fillSecurityCode(securityCode);
        viewInteraction.perform(click());
        securityCodeIsCurrentEditText();
    }

    @Test
    public void checkIdentificationNumberNavigationWithInvalidData() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        for (String invalidIdentificationNumber : CardTestUtils.getInvalidIdentificationNumber()) {
            checkIdentificationNumberIsInvalid(invalidIdentificationNumber, onView(withId(R.id.mpsdkNextButton)));
            checkIdentificationNumberIsInvalid(invalidIdentificationNumber, onView(withId(R.id.mpsdkBackButton)));
        }
    }

    private void checkIdentificationNumberIsInvalid(String number, ViewInteraction viewInteraction) {
        fillIdentificationNumber(number);
        viewInteraction.perform(click());
        identificationNumberIsCurrentEditText();
    }

    private void fillIdentificationNumber(String number) {
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(clearText());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(number));
    }

    @Test
    public void onEmptyCardholderNameNavigateToBackButNotToNextField() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        fillCardholderName("");
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        cardholderNameIsCurrentEditText();
        fillCardholderName("");
        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardNumberIsCurrentEditText();
    }

    private void fillCardholderName(String name) {
        onView(withId(R.id.mpsdkCardholderName)).perform(clearText());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(name));
    }

    @Test
    public void onEmptyExpiryDateNavigateToBackButNotToNextField() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        fillExpiryDate("");
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        expiryDateIsCurrentEditText();
        fillExpiryDate("");
        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardholderNameIsCurrentEditText();
    }

    @Test
    public void onEmptySecurityCodeNavigateToBackButNotToNextField() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        fillSecurityCode("");
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        securityCodeIsCurrentEditText();
        fillSecurityCode("");
        onView(withId(R.id.mpsdkBackButton)).perform(click());
        expiryDateIsCurrentEditText();
    }

    @Test
    public void onEmptyIdentificationNumberNavigateToBackButNotToNextField() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        fillIdentificationNumber("");
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        fillIdentificationNumber("");
        onView(withId(R.id.mpsdkBackButton)).perform(click());
        securityCodeIsCurrentEditText();
    }

    @Test
    public void checkGoingToFrontSecurityCode() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithFrontSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));

        //Back
        onView(withId(R.id.mpsdkCardSecurityCodeView)).check(matches(not(withText(card.getSecurityCode()))));
        //Front
        onView(withId(R.id.mpsdkCardSecurityView)).check(matches(withText(card.getSecurityCode())));

        onView(withId(R.id.mpsdkNextButton)).perform(click());

        identificationNumberIsCurrentEditText();
    }

    @Test
    public void checkGoingToBackSecurityCode() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));

        //Back
        onView(withId(R.id.mpsdkCardSecurityCodeView)).check(matches((withText(card.getSecurityCode()))));
        //Front
        onView(withId(R.id.mpsdkCardSecurityView)).check(matches(not(withText(card.getSecurityCode()))));

        onView(withId(R.id.mpsdkNextButton)).perform(click());

        identificationNumberIsCurrentEditText();
    }

    @Test
    public void checkGoingBackToFrontCardFromSecurityCode() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        //Back
        onView(withId(R.id.mpsdkCardSecurityCodeView)).check(matches((withText(card.getSecurityCode()))));
        //Front
        onView(withId(R.id.mpsdkCardSecurityView)).check(matches(not(withText(card.getSecurityCode()))));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(clearText());

        onView(withId(R.id.mpsdkBackButton)).perform(click());

        //TODO check we are seeing the front side
        expiryDateIsCurrentEditText();
    }

    @Test
    public void checkStayingInFrontCardFromSecurityCode() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithFrontSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        //Back
        onView(withId(R.id.mpsdkCardSecurityCodeView)).check(matches(not(withText(card.getSecurityCode()))));
        //Front
        onView(withId(R.id.mpsdkCardSecurityView)).check(matches(withText(card.getSecurityCode())));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(clearText());

        onView(withId(R.id.mpsdkBackButton)).perform(click());

        //TODO check we are seeing the front side
        expiryDateIsCurrentEditText();
    }

    @Test
    public void checkGoingBackToBackCardFromIdentification() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();

        onView(withId(R.id.mpsdkBackButton)).perform(click());

        //TODO check we are seeing the back side
        securityCodeIsCurrentEditText();
    }

    @Test
    public void checkGoingBackToFrontCardFromIdentification() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithFrontSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();

        onView(withId(R.id.mpsdkBackButton)).perform(click());

        //TODO check we are seeing the front side
        securityCodeIsCurrentEditText();
    }

    @Test
    public void ignoreOnSecurityCodeNotRequired() {
        addInitCalls();

        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithoutRequiredSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(hasFocus()));

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardholderNameIsCurrentEditText();

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardNumberIsCurrentEditText();
    }

    @Test
    public void ignoreOnIdentificationNotRequired() {
        addBankDealsCall();
        PaymentMethod pm = StaticMock.getPaymentMethodWithIdentificationNotRequired();
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(pm);
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("cordial").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void createTokenOnSecurityCodeNotRequired() {
        addInitCalls();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithoutRequiredSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
        assertEquals(mockedToken.getId(), tokenResult.getId());
        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
        assertEquals("tarshop", pmResult.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void createTokenOnIdentificationNumberNotRequired() {
        addBankDealsCall();
        PaymentMethod pm = StaticMock.getPaymentMethodWithIdentificationNotRequired();
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(pm);
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");

        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("cordial").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
        assertEquals(mockedToken.getId(), tokenResult.getId());
        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
        assertEquals(pm.getId(), pmResult.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void setTokenPMAndIssuerOnResult() {
        addInitCalls();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
        assertEquals(mockedToken.getId(), tokenResult.getId());
        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
        assertEquals("master", pmResult.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void openIssuerSelection() {
        addInitCalls();

        Token mockedToken = StaticMock.getTokenMasterIssuers();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        String issuers = StaticMock.getIssuersJson();
        mFakeAPI.addResponseToQueue(issuers, 200, "");

        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        intended(hasComponent(IssuersActivity.class.getName()));

        onView(withId(R.id.mpsdkActivityIssuersView)).perform(actionOnItemAtPosition(0, click()));

        Type listType = new TypeToken<List<Issuer>>() {
        }.getType();
        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);


        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertNotNull(selectedIssuer);
        assertEquals(issuerList.get(0).getId(), selectedIssuer.getId());

        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);

    }

    @Test
    public void onIssuerApiFailOpenErrorActivity() {
        addInitCalls();

        Token mockedToken = StaticMock.getTokenMasterIssuers();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        //Issuers call
        mFakeAPI.addResponseToQueue("", 401, "");

        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onEmptyIssuerListOpenErrorActivity() {
        addInitCalls();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
        List<Issuer> issuerList = new ArrayList<>();
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onTokenApiFailOpenErrorActivity() {
        addInitCalls();

        //Token call
        mFakeAPI.addResponseToQueue("", 401, "");

        String issuers = StaticMock.getIssuersJson();
        mFakeAPI.addResponseToQueue(issuers, 200, "");

        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onIdentificationTypesApiFailRecover() {
        addBankDealsCall();
        addPaymentMethodsCall();
        //Identification types call
        mFakeAPI.addResponseToQueue("", 401, "");
        addIdentificationTypesCall();

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber().substring(0, 6)));

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber().substring(6, 16)));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
    }

    @Test
    public void onIdentificationTypesApiFailCancel() {
        addBankDealsCall();
        addPaymentMethodsCall();
        //Identification types call
        mFakeAPI.addResponseToQueue("", 401, "");
        addIdentificationTypesCall();

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber().substring(0, 6)));

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkExit)).perform(click());
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void onEmptyIdentificationTypesOpenErrorActivity() {
        addBankDealsCall();
        addPaymentMethodsCall();

        List<IdentificationType> list = new ArrayList<>();
        mFakeAPI.addResponseToQueue(list, 200, "");

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onBankDealsApiFailCancel() {
        //Bank deals call
        mFakeAPI.addResponseToQueue("", 401, "");

        addPaymentMethodsCall();
        addIdentificationTypesCall();

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkExit)).perform(click());
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void startBankDealsActivity() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkButtonText)).perform(click());
        intended(hasComponent(BankDealsActivity.class.getName()));
    }

    @Test
    public void ifApiFailurePaymentMethodsShowErrorActivity() {
        addBankDealsCall();
        mFakeAPI.addResponseToQueue("", 401, "");
        addIdentificationTypesCall();
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifApiFailureIdentificationTypesShowErrorActivity() {
        addBankDealsCall();
        addPaymentMethodsCall();
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifApiFailurePaymentMethodsRecover() {
        addBankDealsCall();
        //Payment methods call
        mFakeAPI.addResponseToQueue("", 401, "");
        addPaymentMethodsCall();

        addIdentificationTypesCall();

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
    }

    @Test
    public void decorationPreferencePaintsBackground() {
        addInitCalls();

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_red_error));
        validStartIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(validStartIntent);

        FrameLayout cardBackground = (FrameLayout) mTestRule.getActivity().findViewById(R.id.mpsdkCardBackground);
        int color = ViewUtils.getBackgroundColor(cardBackground);
        assertEquals(color, decorationPreference.getLighterColor());
    }

    @Test
    public void navigateWithoutSecurityCodeAndWithoutIdentificationNumber() {
        addBankDealsCall();
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod pm = StaticMock.getPaymentMethodWithIdentificationAndCVVNotRequired();
        paymentMethods.add(pm);
        validStartIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethods));

        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getPaymentMethodOnWithoutRequiredSecurityCode();
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
        assertEquals(mockedToken.getId(), tokenResult.getId());
        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
        assertEquals("tarshop", pmResult.getId());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void ifApiFailureIssuersRecover() {
        addInitCalls();

        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        //Issuers Call
        mFakeAPI.addResponseToQueue("", 401, "");
        String issuers = StaticMock.getIssuersJson();
        mFakeAPI.addResponseToQueue(issuers, 200, "");

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        intended(hasComponent(IssuersActivity.class.getName()));
    }

    @Test
    public void validateCardColorNaranja() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("naranja");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_naranja);
        validateCardColor(expectedColor);
    }


    @Test
    public void validateCardColorMaster() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("master");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_master);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorVisa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("visa");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_visa);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorAmex() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("amex");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_amex);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorTarshop() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("tarshop");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_tarshop);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorCordial() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cordial");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_cordial);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorCencosud() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cencosud");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_cencosud);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorArgencard() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("argencard");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_argencard);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorCabal() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cabal");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_cabal);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorNativa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("nativa");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_nativa);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorDiners() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("diners");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_diners);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorCordobesa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cordobesa");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_cordobesa);
        validateCardColor(expectedColor);
    }

    @Test
    public void validateCardColorCMR() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cmr");
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_cmr);
        validateCardColor(expectedColor);
    }

    public void validateCardColor(int expectedColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ImageView cardViewLow = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkCardLowApiImageView);
            int color = ViewUtils.getBackgroundColor(cardViewLow);
            assertEquals(color, expectedColor);
        }
    }

    @Test
    public void validateMaskNaranja() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("naranja");
        validateMask(card);
    }

    @Test
    public void validateMaskMaster() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("master");
        validateMask(card);
    }

    @Test
    public void validateMaskVisa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("visa");
        validateMask(card);
    }

    @Test
    public void validateMaskAmex() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("amex");
        validateMask(card);
    }

    @Test
    public void validateMaskTarshop() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("tarshop");
        validateMask(card);
    }

    @Test
    public void validateMaskCordial() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cordial");
        validateMask(card);
    }

    @Test
    public void validateMaskCencosud() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cencosud");
        validateMask(card);
    }

    @Test
    public void validateMaskArgencard() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("argencard");
        validateMask(card);
    }

    @Test
    public void validateMaskCabal() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cabal");
        validateMask(card);
    }

    @Test
    public void validateMaskNativa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("nativa");
        validateMask(card);
    }

    @Test
    public void validateMaskDiners() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("diners");
        validateMask(card);
    }

    @Test
    public void validateMaskCordobesa() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cordobesa");
        validateMask(card);
    }

    @Test
    public void validateMaskCMR() {
        addInitCalls();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("cmr");
        validateMask(card);
    }

    public void validateMask(DummyCard card) {
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkCardNumber)).check(matches(withText(card.getNumberWithMask())));
        onView(withId(R.id.mpsdkCardNumberTextView)).check(matches(withText(containsString(card.getNumberWithMask()))));
    }

    @Test
    public void validateDniMask() {
        addBankDealsCall();
        addPaymentMethodsCall();
        addIdentificationTypesCall();
        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("master");
        DummyIdentificationType identificationType = IdentificationTestUtils.getDummyIdentificationType("DNI");

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        validateMask(identificationType);
    }

    @Test
    public void validateCPFMask() {
        addBankDealsCall();
        addPaymentMethodsCall();

        String identificationTypes = StaticMock.getIdentificationTypeCPF();
        mFakeAPI.addResponseToQueue(identificationTypes, 200, "");

        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("master");
        DummyIdentificationType identificationType = IdentificationTestUtils.getDummyIdentificationType("CPF");

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        validateMask(identificationType);
    }

    @Test
    public void validateCNPJMask() {
        addBankDealsCall();
        addPaymentMethodsCall();

        String identificationTypes = StaticMock.getIdentificationTypeCNPJ();
        mFakeAPI.addResponseToQueue(identificationTypes, 200, "");

        mTestRule.launchActivity(validStartIntent);

        DummyCard card = CardTestUtils.getDummyCard("master");
        DummyIdentificationType identificationType = IdentificationTestUtils.getDummyIdentificationType("CNPJ");

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        validateMask(identificationType);
    }

    public void validateMask(DummyIdentificationType identificationType) {
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(identificationType.getIdentificationNumber()));
        onView(withId(R.id.mpsdkIdNumberView)).check(matches(withText(identificationType.getGetIdentificationNumberWithMask())));
    }

    @Test
    public void ignoreOnSecurityCodeNotRequiredThenTryOneWithRequired() {
        addInitCalls();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);
        DummyCard card = CardTestUtils.getPaymentMethodOnWithoutRequiredSecurityCode();

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardExpiryDate)).check(matches(hasFocus()));

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardholderNameIsCurrentEditText();

        onView(withId(R.id.mpsdkBackButton)).perform(click());
        cardNumberIsCurrentEditText();

        onView(withId(R.id.mpsdkCardNumber)).perform(clearText());

        card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));

        onView(withId(R.id.mpsdkNextButton)).perform(click());
        cardholderNameIsCurrentEditText();
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        expiryDateIsCurrentEditText();
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        securityCodeIsCurrentEditText();
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
    }


    private void addIdentificationTypesCall() {
        String identificationTypes = StaticMock.getIdentificationTypeList();
        mFakeAPI.addResponseToQueue(identificationTypes, 200, "");
    }

    private void addPaymentMethodsCall() {
        String paymentMethods = StaticMock.getPaymentMethodList();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
    }

    private void addBankDealsCall() {
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        mFakeAPI.addResponseToQueue(bankDeals, 200, "");
    }

    private void addInitCalls() {
        addBankDealsCall();
        addPaymentMethodsCall();
        addIdentificationTypesCall();
    }
}
