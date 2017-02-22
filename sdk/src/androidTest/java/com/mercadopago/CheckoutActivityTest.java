package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.NestedScrollViewScrollToAction;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static com.mercadopago.utils.ActivityResultUtil.getActivityResult;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

/**
 * Created by mreverter on 29/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckoutActivityTest {

    private final String PREF_ID = "157723203-a225d4d6-adab-4072-ad92-6389ede0cabd";

    @Rule
    public ActivityTestRule<CheckoutActivity> mTestRule = new ActivityTestRule<>(CheckoutActivity.class, true, false);
    private Intent validStartIntent;
    private FakeAPI mFakeAPI;
    private boolean mIntentsActive;

    @Before
    public void setValidStartIntent() {
        validStartIntent = new Intent();
        validStartIntent.putExtra("checkoutPreferenceId", PREF_ID);
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

    //Recoverable payment or token
    @Test
    public void onResultRecoverPaymentFromPaymentResultActivityStartCardVault() {
        CheckoutPreference preference = StaticMock.getPreferenceWithExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result
        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final Token token = StaticMock.getToken();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Issuer issuer = StaticMock.getIssuer();

        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        Intent paymentResultActivityResultIntent = new Intent();
        paymentResultActivityResultIntent.putExtra("nextAction", PaymentResultAction.RECOVER_PAYMENT);
        Instrumentation.ActivityResult paymentResultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, paymentResultActivityResultIntent);

        intending(hasComponent(PaymentResultActivity.class.getName())).respondWith(paymentResultActivityResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkReviewButtonText)).perform(click());

        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    //COMMON STATE TESTS

    @Test
    public void setInitialParametersOnCreate() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mCheckoutPreference != null
                && activity.mCheckoutPreferenceId.equals(PREF_ID)
                && activity.mMerchantPublicKey != null
                && activity.mMerchantPublicKey.equals("1234"));
    }

    @Test
    public void ifValidStartInstantiateMercadoPago() {
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mMercadoPago != null);
    }

    @Test
    public void getPreferenceByIdOnCreate() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mCheckoutPreference != null && activity.mCheckoutPreference.getId().equals(PREF_ID));
    }

    @Test
    public void ifPreferenceIdFromAPIIsDifferentShowErrorActivity() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        validStartIntent.putExtra("checkoutPreferenceId", "1234");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void whenPaymentMethodReceivedShowPaymentRowOff() {

        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        //Launch activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        sleep();
        //Validations
        PaymentMethodSearchItem searchItem = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod);

        onView(withId(R.id.mpsdkAdapterReviewPaymentDescription)).check(matches(withText(searchItem.getComment())));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkAdapterReviewPaymentImage);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.mpsdk_review_payment_off)).getBitmap();

        assertTrue(bitmap == bitmap2);

        onView(withId(R.id.mpsdkAdapterReviewPayerCostContainer)).check(matches(not(isDisplayed())));
    }

    @Test
    public void whenPaymentMethodReceivedShowPaymentRowOn() {

        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        //Launch activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        sleep();
        //Validations
        String description = activity.getString(R.string.mpsdk_review_description_card, paymentMethod.getName(),
                token.getLastFourDigits());
        onView(withId(R.id.mpsdkAdapterReviewPaymentDescription)).check(matches(withText(description)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkAdapterReviewPaymentImage);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.mpsdk_review_payment_on)).getBitmap();

        assertTrue(bitmap == bitmap2);
        onView(withId(R.id.mpsdkAdapterReviewPayerCostContainer)).perform(NestedScrollViewScrollToAction.scrollTo(), click());
        onView(withId(R.id.mpsdkAdapterReviewPayerCostContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void whenPaymentMethodReceivedShowProductRow() {

        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        //Launch activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        sleep();
        //Validations
        Item item = preference.getItems().get(0);

        onView(withId(R.id.mpsdkAdapterReviewProductText)).check(matches(withText(item.getTitle())));
        onView(withId(R.id.mpsdkAdapterReviewProductDescription)).check(matches(withText(item.getDescription())));
        onView(withId(R.id.mpsdkAdapterReviewProductPrice)).check(matches(withText(
                containsString(String.valueOf(item.getUnitPrice())))));
        if (item.getPictureUrl() == null) {
            ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkAdapterReviewProductImage);

            Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.review_product_placeholder)).getBitmap();

            assertTrue(bitmap == bitmap2);
        }

    }

    @Test
    public void onPreferenceWithManyItemsShowProductList() {
        CheckoutPreference preferenceWithManyItems = StaticMock.getCheckoutPreference();

        List<Item> items = preferenceWithManyItems.getItems();
        Item firstItem = items.get(0);
        Item extraItem = new Item("2", 1);
        extraItem.setTitle("Item2");
        extraItem.setUnitPrice(new BigDecimal(222));
        extraItem.setCurrencyId("MXN");
        items.add(extraItem);

        preferenceWithManyItems.setItems(items);

        mFakeAPI.addResponseToQueue(preferenceWithManyItems, 200, "");

        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        //Launch activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        RecyclerView referencesLayout = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkReviewProductRecyclerView);
        assertEquals(referencesLayout.getChildCount(), preferenceWithManyItems.getItems().size());
    }

    @Test
    public void whenEditButtonClickStartPaymentVaultActivity() {
        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkAdapterReviewPaymentChangeButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        //validations
        intended(hasComponent(PaymentVaultActivity.class.getName()), times(2));
    }

    @Test
    public void onBackPressedAfterEditImageClickedRestoreState() {

        //Prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        releaseIntents();

        //Perform actions
        onView(withId(R.id.mpsdkAdapterReviewPaymentChangeButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());
        pressBack();

        sleep();
        //Validations

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkAdapterReviewPaymentImage);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.mpsdk_review_payment_off)).getBitmap();

        assertTrue(bitmap == bitmap2);
    }

    @Test
    public void onBackPressedTwiceAfterPaymentMethodSelectionStartPaymentVault() {

        //prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        pressBack();
        pressBack();
        //validations

        intended(hasComponent(PaymentVaultActivity.class.getName()), times(2));
    }

    @Test(expected = NoActivityResumedException.class)
    public void onBackPressedThreeTimesAfterPaymentMethodSelectionFinishActivity() {
        //prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        //prepare mocked api response
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "");

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //Release intents to receive the canceled response from payment vault
        releaseIntents();

        pressBack();
        //Let payment vault start

        pressBack();
    }

    @Test(expected = NoActivityResumedException.class)
    public void onBackPressedBeforePaymentMethodSearchObtainedFinishActivityWithCanceledResult() {

        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        mFakeAPI.addResponseToQueue(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), 200, "", 5000);

        mTestRule.launchActivity(validStartIntent);
        pressBack();
        //validations
        intended(hasComponent(PaymentVaultActivity.class.getName()), times(0));
    }

    // EXCLUSIONS TESTS

    @Test(expected = NoActivityResumedException.class)
    public void ifUniquePaymentMethodInPaymentMethodSearchFinishActivityWhenBackPressed() {

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniquePaymentMethodOff();
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        //Launch activity
        mTestRule.launchActivity(validStartIntent);

        String paymentMethodId = paymentMethodSearch.getGroups().get(0).getId();
        assertTrue(mTestRule.getActivity().mSelectedPaymentMethod.getId().equals(paymentMethodId));
        pressBack();
    }

    @Test
    public void ifAllPaymentMethodsExcludedButOneDoNotMakeEditionAvailable() {

        //Preparing mocked api responses

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        //Payment method options service responds with one payment method
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniquePaymentMethodOff();
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        //Launch activity
        mTestRule.launchActivity(validStartIntent);
        sleep();
        onView(withId(R.id.mpsdkAdapterReviewPaymentChangeButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkReviewChangePaymentText)).check(matches(not(isDisplayed())));
        intended(hasComponent(PaymentVaultActivity.class.getName()), times(1));
    }

    @Test
    public void ifAllPaymentMethodsExcludedButCreditCardOneDoMakeEditionAvailable() {

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        //Payment method options service responds only credit card
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        //prepare next activity result
        Intent paymentMethodSelectionResult = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        final Token token = StaticMock.getToken();
        final Issuer issuer = StaticMock.getIssuer();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();

        paymentMethodSelectionResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodSelectionResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodSelectionResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        paymentMethodSelectionResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodSelectionResult);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        //Launch activity
        mTestRule.launchActivity(validStartIntent);
        sleep();
        onView(withId(R.id.mpsdkAdapterReviewPaymentChangeButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());
        sleep();
        intended(hasComponent(CardVaultActivity.class.getName()), times(2));
    }

    // VALIDATIONS TESTS
    @Test
    public void ifPublicKeyNotSetStartErrorActivityAndFinishWithMPExceptionOnResponse() {

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("checkoutPreferenceId", PREF_ID);

        mFakeAPI.addResponseToQueue(StaticMock.getPreferenceWithExclusions(), 200, "");
        mTestRule.launchActivity(invalidStartIntent);

        onView(withId(R.id.mpsdkExit)).perform(click());

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());
        MPException mpException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("mpException"), MPException.class);

        assertTrue(mpException.getErrorDetail().equals("public key not set"));
    }

    @Test
    public void ifPreferenceIdNotSetShowErrorActivityAndFinishWithMPExceptionOnResponse() {

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("merchantPublicKey", "1234");

        mTestRule.launchActivity(invalidStartIntent);

        onView(withId(R.id.mpsdkExit)).perform(click());

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());
        MPException mpException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("mpException"), MPException.class);

        assertTrue(mpException.getErrorDetail().equals("preference id not set"));
    }

    @Test
    public void ifNeitherPreferenceIdNorPublicKeySetStartErrorActivity() {

        Intent invalidStartIntent = new Intent();

        mTestRule.launchActivity(invalidStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifTermsAndConditionsClickedStartTermAndConditionsActivity() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutTermsAndConditions)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        //validations
        intended(hasComponent(TermsAndConditionsActivity.class.getName()));
    }

    @Test
    public void whenOfflinePaymentMethodSelectedSetItAsResultForCheckoutActivity() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1).getChildren().get(1);

        PaymentMethod selectedPaymentMethod = new PaymentMethod();
        selectedPaymentMethod.setId(selectedSearchItem.getId());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(selectedPaymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        //validations
        assertTrue(selectedSearchItem.getId().contains(mTestRule.getActivity().mSelectedPaymentMethod.getId()));
    }

    @Test
    public void whenResultFromGuessingNewCardFormReceivedSetItAsResultForCheckoutActivity() {
        //prepared mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //prepare next activity result
        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Token token = StaticMock.getToken();

        guessingFormResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingFormResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        guessingFormResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //validations
        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
        assertEquals(mTestRule.getActivity().mCreatedToken.getId(), token.getId());
        assertEquals(mTestRule.getActivity().mSelectedPayerCost.getInstallments(), payerCost.getInstallments());
    }

    @Test
    public void setPaymentMethodResultWithIssuer() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        //prepare next activity result
        Intent paymentMethodSelectionResult = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        final Token token = StaticMock.getToken();
        final Issuer issuer = StaticMock.getIssuer();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();

        paymentMethodSelectionResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodSelectionResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodSelectionResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        paymentMethodSelectionResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodSelectionResult);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //validations
        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
        assertEquals(mTestRule.getActivity().mCreatedToken.getId(), token.getId());
        assertEquals(mTestRule.getActivity().mSelectedPayerCost.getInstallments(), payerCost.getInstallments());
        assertTrue(mTestRule.getActivity().mSelectedIssuer.getId().equals(issuer.getId()));
    }

    // CREATE PAYMENT TESTS

    @Test
    public void createPaymentForOnlinePaymentMethodStartsCongratsActivity() {
        //prepared mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    @Test
    public void createPaymentForOfflinePaymentMethodStartsInstructionsActivity() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        //validations
        intended(hasComponent(InstructionsActivity.class.getName()));
    }

    @Test
    public void whenPaymentCreationFailsWithBadRequestShowErrorScreen() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.BAD_REQUEST, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenPaymentCreationFailsWithServerErrorShowErrorScreen() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenPaymentCreationFailsWithServerErrorAndUserPressesBackShowRegularLayout() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        //In Error Screen
        pressBack();

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).check(matches(isDisplayed()));
    }

    @Test
    public void whenCardPaymentCreationFailsAndErrorScreenShownAndUserPressesBackAndPayedAgainCheckSameTransactionId() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR, "");

        //Prepare payment method selection response
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        //create payment
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        //save transaction id
        Long transactionId = mTestRule.getActivity().mTransactionId;

        //In Error Screen
        pressBack();

        //Retry payment
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        assertTrue(transactionId == mTestRule.getActivity().mTransactionId);
    }

    @Test
    public void whenPaymentCreationStatusIsProcessingStartPaymentInProcessActivity() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.PROCESSING, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PendingActivity.class.getName()));
    }

    @Test
    public void ifPaymentCreationFailsButApiExceptionDoesNotHaveStatusShowErrorActivity() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        ApiException apiExceptionWithoutStatus = StaticMock.getApiExceptionWithoutStatus();

        //200 to put a recognizable status, to detect test failure
        mFakeAPI.addResponseToQueue(apiExceptionWithoutStatus, 500, "");

        mTestRule.launchActivity(validStartIntent);
        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPaymentCreationFailsAndApiExceptionStatusIsUnknownShowErrorActivity() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mFakeAPI.addResponseToQueue("", 832, "");

        mTestRule.launchActivity(validStartIntent);
        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifInvalidPreferenceSetStartErrorActivity() {
        Intent invalidStartIntent = new Intent();
        CheckoutPreference invalidPreference = StaticMock.getPreferenceWithExclusions();
        invalidPreference.setItems(null);

        validStartIntent.putExtra("checkoutPreferenceId", PREF_ID);

        mFakeAPI.addResponseToQueue(invalidPreference, 200, "");

        mTestRule.launchActivity(invalidStartIntent);
        intending(hasComponent(ErrorActivity.class.getName()));
    }

    //CARD PAYMENT METHOD TESTS
    @Test
    public void onCardPaymentMethodSelectedShowPaymentMethod() {
        //Prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token token = StaticMock.getToken();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Issuer issuer = StaticMock.getIssuer();

        Intent resultIntent = new Intent();

        resultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        resultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        resultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        resultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        Instrumentation.ActivityResult paymentVaultResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentVaultResult);

        //Mock API Calls
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        //Launch Activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        //Data to assert
        String paymentMethodDescription = activity.getString(R.string.mpsdk_review_description_card, paymentMethod.getName(),
                token.getLastFourDigits());

        //Assertions
        onView(withId(R.id.mpsdkAdapterReviewPaymentDescription)).check(matches(withText(paymentMethodDescription)));
    }


    // RESULTS TESTS

    @Test
    public void onResultActivityResponseFinishWithPaymentResult() {
        //Prepare PaymentVaultActivity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        PayerCost payerCost = StaticMock.getPayerCostWithoutInterests();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        Instrumentation.ActivityResult paymentVaultResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentVaultResult);

        Payment payment = StaticMock.getPayment();
        Intent resultActivityResultIntent = new Intent();
        resultActivityResultIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        Instrumentation.ActivityResult resultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultActivityResultIntent);

        intending(hasComponent(PaymentResultActivity.class.getName())).respondWith(resultActivityResult);

        //Mock API Calls
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //Launch Activity
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        ActivityResult checkoutActivityResult = getActivityResult(mTestRule.getActivity());
        Payment resultPayment = JsonUtil.getInstance().fromJson(checkoutActivityResult.getExtras().getString("payment"), Payment.class);

        assertTrue(payment.getId().equals(resultPayment.getId()));
        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void onCancelResultFromPaymentVaultWithoutEditionRequested() {
        //Mock API Calls
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent()));

        mTestRule.launchActivity(validStartIntent);
        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void onCancelResultFromPaymentVaultAfterEditionShowPreviousSelection() {
        //Mock API Calls
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent()));

        mTestRule.launchActivity(validStartIntent);
        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void onResultSelectOtherFromResultActivityStartPaymentMethodSelection() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result
        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Token token = StaticMock.getToken();
        final Issuer issuer = StaticMock.getIssuer();

        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        Intent resultActivityResultIntent = new Intent();
        resultActivityResultIntent.putExtra("nextAction", PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD);
        Instrumentation.ActivityResult resultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultActivityResultIntent);

        intending(hasComponent(PaymentResultActivity.class.getName())).respondWith(resultActivityResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentVaultActivity.class.getName()), times(2));
    }

    @Test
    public void ifCustomerReceivedSaveId() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        preference.setPaymentPreference(null);
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mCustomerId.equals(customer.getId()));
    }

    @Test
    public void ifCustomerSavedAndCardPaymentTypeSetIdToPayer() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();

        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        mFakeAPI.addResponseToQueue("", 400, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final Token token = StaticMock.getToken();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Issuer issuer = StaticMock.getIssuer();


        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkReviewButtonText)).perform(click());
        assertTrue(mTestRule.getActivity().mCheckoutPreference.getPayer().getId().equals(customer.getId()));
    }

    @Test
    public void ifCustomerSavedAndNotCardPaymentTypeSelectedDoNotSetIdToPayer() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();

        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        mFakeAPI.addResponseToQueue("", 400, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkReviewButtonText)).perform(click());
        assertTrue(mTestRule.getActivity().mCheckoutPreference.getPayer().getId() == null);
    }

    // CUSTOMER CARDS

    @Test
    public void ifMerchantServerInfoAddedGetCustomerCards() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        preference.setPaymentPreference(null);
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        Gson gson = new Gson();
        String cardsJson = gson.toJson(mTestRule.getActivity().mSavedCards);
        intended(allOf(hasComponent(PaymentVaultActivity.class.getName()), hasExtra("cards", cardsJson)));
    }

    @Test
    public void ifPaymentPreferenceSetAndCustomerCardsAvailableFilterThem() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(new ArrayList<String>() {{
            add("visa");
        }});

        preference.setPaymentPreference(paymentPreference);
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mSavedCards.size() == 1);
    }

    @Test
    public void ifCardsAvailableMakePaymentMethodEditionAvailable() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();

        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(1);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson(), PaymentMethodSearch.class);

        paymentMethodSearch.getGroups().removeAll(paymentMethodSearch.getGroups());

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Token token = StaticMock.getToken();
        final Issuer issuer = StaticMock.getIssuer();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));

        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkAdapterReviewPaymentChangeButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentVaultActivity.class.getName()), times(2));
    }
    //TODO preguntar
    @Test
    public void ifPaymentPreferenceIsNullDoNotFilterCards() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        preference.setPaymentPreference(null);
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Customer customer = StaticMock.getCustomer(2);
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mSavedCards.size() == 2);
    }
    // RECOVERY TESTS

    @Test
    public void afterPreferenceGetFromAPIFailsWithRecoverableErrorAndRetrySelectedRetryAPICall() {
        CheckoutPreference checkoutPreference = StaticMock.getCheckoutPreference();

        mFakeAPI.addResponseToQueue("", 400, "");
        mFakeAPI.addResponseToQueue(checkoutPreference, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        assertTrue(mTestRule.getActivity().mCheckoutPreference.getId().equals(checkoutPreference.getId()));
    }

    @Test
    public void afterPaymentMethodSearchGetFromAPIFailsWithRecoverableErrorAndRetrySelectedRetryAPICall() {
        CheckoutPreference checkoutPreference = StaticMock.getCheckoutPreference();
        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();

        mFakeAPI.addResponseToQueue(checkoutPreference, 200, "");
        mFakeAPI.addResponseToQueue("", 400, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        assertTrue(mTestRule.getActivity().mPaymentMethodSearch != null);
    }

    // PAYMENT RECOVERY
    @Test
    public void onResultRecoverPaymentFromRejectionActivityStartCardVaultActivity() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        Payment paymentRecovered = StaticMock.getPaymentApprovedVisa();
        mFakeAPI.addResponseToQueue(paymentRecovered, 200, "");

        //prepare next activity result
        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final Token token = StaticMock.getToken();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Issuer issuer = StaticMock.getIssuer();

        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        Intent paymentResultActivityResultIntent = new Intent();
        paymentResultActivityResultIntent.putExtra("nextAction", PaymentResultAction.RECOVER_PAYMENT);
        Instrumentation.ActivityResult paymentResultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, paymentResultActivityResultIntent);

        intending(hasComponent(PaymentResultActivity.class.getName())).respondWith(paymentResultActivityResult);

        Intent cardVaultResultIntent = new Intent();
        cardVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        cardVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        cardVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        cardVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult cardVaultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, cardVaultResultIntent);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(cardVaultActivityResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        Intents.release();
        Intents.init();

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()));
    }

    @Test
    public void onResultRecoverPaymentFromCallForAuthorizeActivityStartCardVaultActivity() {
        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedCallForAuthorize();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        Payment paymentRecovered = StaticMock.getPaymentApprovedVisa();
        mFakeAPI.addResponseToQueue(paymentRecovered, 200, "");

        //prepare next activity result
        Intent paymentVaultResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
        final Token token = StaticMock.getToken();
        final PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        final Issuer issuer = StaticMock.getIssuer();

        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);

        Intent cardVaultResultIntent = new Intent();
        cardVaultResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        cardVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        cardVaultResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        cardVaultResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult cardVaultActivityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, cardVaultResultIntent);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(cardVaultActivityResult);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).perform(click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(2));
    }

    //CONGRATS DISPLAY TESTS
    @Test
    public void ifCongratsDisplaySetZeroAndPaymentApprovedDontStartPaymentResultActivity() {
        validStartIntent.putExtra("congratsDisplay", 0);

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(0));
    }

    @Test
    public void ifCongratsDisplayIsZeroButPaymentWasRejectedDoStartPaymentResultActivity() {
        validStartIntent.putExtra("congratsDisplay", 0);

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    @Test
    public void ifCongratsDisplaySetNegativeAndPaymentApprovedStartPaymentResultActivity() {
        validStartIntent.putExtra("congratsDisplay", -1);

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    @Test
    public void ifCongratsDisplaySetNegativeAndPaymentRejectedStartPaymentResultActivity() {
        validStartIntent.putExtra("congratsDisplay", -1);

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }


    @Test
    public void ifCongratsDisplaySetAndPaymentApprovedStartPaymentResultActivity() {
        validStartIntent.putExtra("congratsDisplay", 10);

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    @Test
    public void ifCongratsDisplayNotSetAndPaymentApprovedStartPaymentResultActivity() {

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    @Test
    public void ifCongratsDisplayNotSetAndPaymentNotApprovedStartPaymentResultActivity() {

        CheckoutPreference preference = StaticMock.getCheckoutPreference();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getPaymentMethodSearchWithoutCustomOptionsAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();
        Issuer issuer = StaticMock.getIssuer();
        Intent paymentMethodResult = new Intent();
        paymentMethodResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodResult.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        paymentMethodResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodResult);
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCheckoutConfirmButton)).perform(NestedScrollViewScrollToAction.scrollTo(), click());

        intended(hasComponent(PaymentResultActivity.class.getName()), times(1));
    }

    //BINARY MODE TESTS
    @Test
    public void ifBinaryModeSetTrueSetActivityFlagTrue() {
        validStartIntent.putExtra("binaryModeEnabled", true);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mBinaryModeEnabled);
    }

    @Test
    public void ifBinaryModeNotSetSetFalseAsDefault() {
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(!activity.mBinaryModeEnabled);
    }

}