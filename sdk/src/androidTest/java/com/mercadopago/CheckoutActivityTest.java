package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.test.FakeAPI;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
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
    public void setInitialParametersOnCreate() {
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mCheckoutPreference != null && activity.mCheckoutPreference.getId().equals(PREF_ID));
    }

    @Test
    public void ifPreferenceIdFromAPIIsDifferentShowErrorActivity() {
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        validStartIntent.putExtra("checkoutPreferenceId", "1234");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPreferenceHasManyItemsAppendTitles() {
        CheckoutPreference preferenceWithManyItems =  StaticMock.getPreferenceWithoutExclusions();

        List<Item> items = preferenceWithManyItems.getItems();
        Item firstItem = items.get(0);
        Item extraItem = new Item("2", 1);
        extraItem.setTitle("Item2");
        extraItem.setUnitPrice(new BigDecimal(100));
        extraItem.setCurrencyId("MXN");
        items.add(extraItem);

        preferenceWithManyItems.setItems(items);

        mFakeAPI.addResponseToQueue(preferenceWithManyItems, 200, "");

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mPurchaseTitle.contains(firstItem.getTitle())
                && activity.mPurchaseTitle.contains(",")
                && activity.mPurchaseTitle.contains(extraItem.getTitle()));
    }

    @Test
    public void whenPaymentMethodReceivedShowPaymentMethodRow() {

        //Prepare result from next activity
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //Preparing mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");

        //Launch activity
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        //Validations
        String comment = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod).getComment();

        onView(withId(R.id.mpsdkComment))
                .check(matches(withText(comment)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkImage);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

        assertTrue(bitmap == bitmap2);
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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions

        onView(withId(R.id.mpsdkEditHint)).perform(click());

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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        //Perform actions
        onView(withId(R.id.mpsdkEditHint)).perform(click());
        pressBack();

        //Validations
        String comment = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod).getComment();

        onView(withId(R.id.mpsdkComment))
                .check(matches(withText(comment)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.mpsdkImage);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        mFakeAPI.addResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        pressBack();
        pressBack();
        //validations

        intended(hasComponent(PaymentVaultActivity.class.getName()), times(2));
    }

    public void onBackPressedThreeTimesAfterPaymentMethodSelectionFinishActivity() {
        //prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        //prepare mocked api response
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");
        mFakeAPI.addResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //Let payment vault start
        pressBack();
        pressBack();

        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void whenPaymentMethodSelectedShowShoppingCart() {
        //prepare next activity result
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        //prepare mocked api response
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        //validations
        View itemInfoLayout = activity.findViewById(R.id.mpsdkShoppingCartFragment);
        assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);
    }

    //VALIDATIONS TESTS

    @Test
    public void ifPublicKeyNotSetCallFinish() {

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("checkoutPreferenceId", PREF_ID);

        mFakeAPI.addResponseToQueue(StaticMock.getPreferenceWithExclusions(), 200, "");
        mTestRule.launchActivity(invalidStartIntent);

        intending(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPreferenceIdNotSetShowErrorActivity() {

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("publicKey", "1234");

        mTestRule.launchActivity(invalidStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkTermsAndConditions)).perform(click());

        //validations
        intended(hasComponent(TermsAndConditionsActivity.class.getName()));
    }

    @Test
    public void whenOfflinePaymentMethodSelectedSetItAsResultForCheckoutActivity() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
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
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //prepare next activity result
        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        final Token token = new Token();
        token.setId("1");

        guessingFormResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingFormResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //validations
        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
        assertEquals(mTestRule.getActivity().mCreatedToken.getId(), token.getId());

    }

    @Test
    public void setPaymentMethodResultWithIssuer() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        //prepare next activity result
        Intent paymentMethodSelectionResult = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        final Token token = new Token();
        token.setId("1");
        final Issuer issuer = new Issuer();
        issuer.setId((long) 1234);

        paymentMethodSelectionResult.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        paymentMethodSelectionResult.putExtra("token", JsonUtil.getInstance().toJson(token));
        paymentMethodSelectionResult.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
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
        assertEquals(mTestRule.getActivity().mSelectedIssuer.getId(), issuer.getId());
    }

    @Test
    public void getPaymentMethodResultFromPaymentMethodsActivity() {

        //prepared mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        PaymentMethodSearchItem itemWithoutChildren = paymentMethodSearch.getGroups().get(1);
        itemWithoutChildren.setChildren(new ArrayList<PaymentMethodSearchItem>());
        paymentMethodSearch.getGroups().set(1, itemWithoutChildren);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //
        Intent paymentMethodsResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        paymentMethodsResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodsResultIntent);

        intending(hasComponent(PaymentMethodsActivity.class.getName())).respondWith(result);


        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));


        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
    }

    @Test
    public void createPaymentForOfflinePaymentMethodStartsInstructionsActivity() {
        //prepare mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment();
        mFakeAPI.addResponseToQueue(payment, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkPayButton)).perform(click());

        //validations
        intended(hasComponent(InstructionsActivity.class.getName()));
    }

    @Test
    public void createPaymentForOnlinePaymentMethodStartsCongratsActivity() {
        //prepared mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());
        mFakeAPI.addResponseToQueue(payment, 200, "");

        //prepare next activity result
        Intent resultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        resultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        Instrumentation.ActivityResult paymentMethodResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(paymentMethodResult);
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPayButton)).perform(click());

        intended(hasComponent(CongratsActivity.class.getName()));
    }

    @Test
    public void whenPaymentCreationFailsWithBadRequestShowErrorScreen() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.BAD_REQUEST, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkPayButton)).perform(click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenPaymentCreationFailsWithServerErrorShowErrorScreen() {
        //Prepare mocked api responses
        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
        mFakeAPI.addResponseToQueue(preference, 200, "");

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR, "");

        mTestRule.launchActivity(validStartIntent);

        //perform actions
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkGroupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.mpsdkPayButton)).perform(click());

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //TODO Wait for ResultActivity
//    @Test
//    public void whenPaymentCreationFailsWithProcessingCodeShowCongratsActivityInProcess() {
//        //Prepare mocked api responses
//        CheckoutPreference preference = StaticMock.getPreferenceWithoutExclusions();
//        mFakeAPI.addResponseToQueue(preference, 200, "");
//
//        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
//        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
//        mFakeAPI.addResponseToQueue("", ApiUtil.StatusCodes.PROCESSING, "");
//
//        //Set up intents
//        mTestRule.initIntentsRecording();
//
//        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());
//
//        Intent intent = new Intent();
//        intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
//
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
//        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);
//
//        //Launch activity
//
//        mTestRule.launchActivity(createValidStartIntent);
//
//        onView(withId(R.id.mpsdkPayButton)).perform(click());
//
//        intended(hasComponent(CongratsActivity.class.getName()));
//        onView(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))).check(matches(isDisplayed()));
//    }

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
}
