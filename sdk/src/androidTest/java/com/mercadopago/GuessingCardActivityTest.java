package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.ArrayListMultimap;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.DummyCard;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.CardTestUtils;
import com.mercadopago.utils.ViewUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;

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
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
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
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
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
        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);
        validStartIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));
        Token token = StaticMock.getTokenAmex();
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        String contained = CardTestUtils.getMockedBinInFront(token.getFirstSixDigits());

        onView(withId(R.id.mpsdkCardNumberTextView)).check(matches(withText(containsString(contained))));
        onView(withId(R.id.mpsdkCardholderNameView)).check(matches(withText(token.getCardholder().getName().toUpperCase())));
        onView(withId(R.id.mpsdkCardHolderExpiryYear)).check(matches(withText(token.getExpirationYear().toString().substring(2,4))));
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
    public void initializeWithEmptyPaymentPreferenceIsValid() {
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
        addPaymentMethodsCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add("visa");
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("visa")));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorTextView)).check(matches(withText(R.string.mpsdk_invalid_payment_method)));
    }

    @Test
    public void initializeWithPaymentMethodExclusionSupported() {
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add("visa");
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(not(isDisplayed())));

    }

    @Test
    public void initializeWithPaymentTypeExclusionNotSupported() {
        addPaymentMethodsCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("credit_card");
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("visa")));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorTextView)).check(matches(withText(R.string.mpsdk_invalid_payment_method)));
    }

    @Test
    public void initializeWithPaymentTypeExclusionSupported() {
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        PaymentPreference paymentPreference = StaticMock.getEmptyPaymentPreference();
        List<String> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add("debit_card");
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkButtonContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorContainer)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showBankDealsOnlyCardSteps() {
        List<DummyCard> dummyCards = CardTestUtils.getAllCards();

        String paymentMethods = StaticMock.getPaymentMethodList();
        Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);

        String identificationTypes = StaticMock.getIdentificationTypeList();
        Type listType2 = new TypeToken<List<IdentificationType>>(){}.getType();
        List<IdentificationType> identificationTypeList = JsonUtil.getInstance().getGson().fromJson(identificationTypes, listType2);

        for (DummyCard card: dummyCards) {
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
    public void showNextAndBackButtonsOnStart() {
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkNextButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackInactiveButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkNextButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackButton)).check(matches(isDisplayingAtLeast(100)));
        onView(withId(R.id.mpsdkBackInactiveButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkEditTextNavigationWithValidData() {
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        cardNumberIsCurrentEditText();
        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        cardNumberIsCurrentEditText();
        for (String invalidNumber: CardTestUtils.getInvalidCardNumbers()) {
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        expiryDateIsCurrentEditText();
        for (String invalidDate: CardTestUtils.getInvalidExpiryDates()) {
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        securityCodeIsCurrentEditText();
        for (String invalidSecurityCode: CardTestUtils.getInvalidSecurityCodes()) {
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
        identificationNumberIsCurrentEditText();
        for (String invalidIdentificationNumber: CardTestUtils.getInvalidIdentificationNumber()) {
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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
        addPaymentMethodsCall();
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
    }

    @Test
    public void ignoreOnIdentificationNotRequired() {
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

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("cordial")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
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

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("cordial")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
        Issuer mockedIssuer = StaticMock.getIssuer();
        List<Issuer> issuerList = new ArrayList<>();
        issuerList.add(mockedIssuer);
        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
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
        addPaymentMethodsCall();
        addIdentificationTypesCall();

//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        String issuers = StaticMock.getIssuersJson();
//        Type listType = new TypeToken<List<Issuer>>(){}.getType();
//        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");

        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        intended(hasComponent(IssuersActivity.class.getName()));
//
        try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {

    }
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
//        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
//        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
//        assertEquals(mockedToken.getId(), tokenResult.getId());
//        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
//        assertEquals("master", pmResult.getId());
//
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void startBankDealsActivity() {
        addPaymentMethodsCall();
        addIdentificationTypesCall();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkButtonText)).perform(click());
        intended(hasComponent(BankDealsActivity.class.getName()));

    }


    @Test
    public void ifApiFailurePaymentMethodsShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        addIdentificationTypesCall();
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifApiFailureIdentificationTypesShowErrorActivity() {
        addPaymentMethodsCall();
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getCardNumber("master")));
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    private void addIdentificationTypesCall() {
        String identificationTypes = StaticMock.getIdentificationTypeList();
//        Type listType2 = new TypeToken<List<IdentificationType>>(){}.getType();
//        List<IdentificationType> identificationTypeList = JsonUtil.getInstance().getGson().fromJson(identificationTypes, listType2);
        mFakeAPI.addResponseToQueue(identificationTypes, 200, "");
    }

    private void addPaymentMethodsCall() {
        String paymentMethods = StaticMock.getPaymentMethodList();
//        Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
//        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
    }
}
