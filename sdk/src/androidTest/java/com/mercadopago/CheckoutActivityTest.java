package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.JsonUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by mreverter on 29/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckoutActivityTest {

    @Rule
    public MockedApiTestRule<CheckoutActivity> mTestRule = new MockedApiTestRule<>(CheckoutActivity.class, true, false);
    private Intent validStartIntent;
    private CheckoutPreference preferenceWithoutExclusions;

    @Before
    public void setValidStartIntent() {
        validStartIntent = new Intent();
        preferenceWithoutExclusions = StaticMock.getPreferenceWithoutExclusions();
        validStartIntent.putExtra("checkoutPreference", preferenceWithoutExclusions);
        validStartIntent.putExtra("merchantPublicKey", "1234");
    }

    @Test
    public void setInitialParametersOnCreate() {
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mCheckoutPreference != null
                && activity.mCheckoutPreference.getId().equals(preferenceWithoutExclusions.getId())
                && activity.mMerchantPublicKey != null
                && activity.mMerchantPublicKey.equals("1234"));
    }

    @Test
    public void ifValidStartInstantiateMercadoPago() {
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mMercadoPago != null);
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

        validStartIntent.putExtra("checkoutPreference", preferenceWithManyItems);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        assertTrue(activity.mPurchaseTitle.contains(firstItem.getTitle())
                && activity.mPurchaseTitle.contains(",")
                && activity.mPurchaseTitle.contains(extraItem.getTitle()));
    }

    @Test
    public void whenPaymentMethodReceivedShowPaymentMethodRow() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        String comment = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod).getComment();

        onView(withId(R.id.contentLayout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.comment))
                .check(matches(withText(comment)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.image);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

        assertTrue(bitmap == bitmap2);
    }

    @Test
    public void whenEditButtonClickStartPaymentVaultActivity() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.initIntents();
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        onView(withId(R.id.imageEdit)).perform(click());

        intended(hasComponent(PaymentVaultActivity.class.getName()));
    }

    @Test
    public void onBackPressedAfterEditImageClickedRestoreState() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        onView(withId(R.id.imageEdit)).perform(click());

        pressBack();

        String comment = paymentMethodSearch.getSearchItemByPaymentMethod(paymentMethod).getComment();

        onView(withId(R.id.contentLayout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.comment))
                .check(matches(withText(comment)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.image);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

        assertTrue(bitmap == bitmap2);
    }

    @Test
    public void onBackPressedAfterPaymentMethodSelectionStartPaymentVault() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        pressBack();

        onView(withId(R.id.groupsList))
                .check(matches(isDisplayed()));
    }

    @Test(expected = NoActivityResumedException.class)
    public void onBackPressedTwiceAfterPaymentMethodSelectionFinishActivity() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        pressBack();
        pressBack();
    }

    @Test
    public void whenPaymentMethodSelectedShowShoppingCart() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.shoppingCartFragment);
        assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);
    }

    //VALIDATIONS TESTS

    @Test
    public void ifPublicKeyNotSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("checkoutPreference", preferenceWithoutExclusions);

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPreferenceNotSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        invalidStartIntent.putExtra("publicKey", "1234");

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifNeitherPreferenceNorPublicKeySetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifTermsAndConditionsClickedStartTermAndConditionsActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.termsAndConditions)).perform(click());
        intended(hasComponent(TermsAndConditionsActivity.class.getName()));
    }

    @Test
    public void whenOfflinePaymentMethodSelectedSetItAsResultForCheckoutActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);
        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1).getChildren().get(1);

        assertTrue(selectedSearchItem.getId().contains(mTestRule.getActivity().mSelectedPaymentMethod.getId()));
    }

    @Test
    public void whenResultFromGuessingNewCardFormReceivedSetItAsResultForCheckoutActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        final Token token = new Token();
        token.setId("1");
        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(GuessingNewCardActivity.class.getName())).respondWith(result);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
        assertEquals(mTestRule.getActivity().mCreatedToken.getId(), token.getId());

    }

    @Test
    public void setResultFromGuessingNewCardWithIssuer() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");

        final Token token = new Token();
        token.setId("1");
        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");
        final Issuer issuer = new Issuer();
        issuer.setId((long) 1234);

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        guessingFormResultIntent.putExtra("issuer", issuer);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(GuessingNewCardActivity.class.getName())).respondWith(result);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
        assertEquals(mTestRule.getActivity().mCreatedToken.getId(), token.getId());
        assertEquals(mTestRule.getActivity().mSelectedIssuer.getId(), issuer.getId());
    }

    @Test
    public void getPaymentMethodResultFromPaymentMethodsActivity() {

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        PaymentMethodSearchItem itemWithoutChildren = paymentMethodSearch.getGroups().get(1);
        itemWithoutChildren.setChildren(new ArrayList<PaymentMethodSearchItem>());
        paymentMethodSearch.getGroups().set(1, itemWithoutChildren);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        PaymentMethod paymentMethodToList = new PaymentMethod();
        paymentMethodToList.setId("oxxo");
        paymentMethodToList.setName("Oxxo");
        paymentMethodToList.setPaymentTypeId("ticket");
        paymentMethodList.add(paymentMethodToList);

        mTestRule.addApiResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.addApiResponseToQueue(paymentMethodList, 200, "");

        mTestRule.launchActivity(validStartIntent);


        Intent paymentMethodsResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethodOff();

        paymentMethodsResultIntent.putExtra("paymentMethod", paymentMethod);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentMethodsResultIntent);

        intending(hasComponent(PaymentMethodsActivity.class.getName())).respondWith(result);
        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));


        assertEquals(mTestRule.getActivity().mSelectedPaymentMethod.getId(), paymentMethod.getId());
    }

    @Test
    public void createPaymentForOfflinePaymentMethodStartsInstructionsActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(payment, 200, "");


        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.payButton)).perform(click());

        intended(hasComponent(InstructionsActivity.class.getName()));
    }

    @Test
    public void createPaymentForOnlinePaymentMethodStartsCongratsActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        Payment payment = StaticMock.getPayment(InstrumentationRegistry.getContext());

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue(payment, 200, "");

        final Token token = new Token();
        token.setId("1");

        mTestRule.addApiResponseToQueue(token, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(mTestRule.getActivity());

        CardToken cardToken = new CardToken("4509953566233704", 12, 99, "1234", "Holder Name Perez", "DNI", "34543454");

        guessingFormResultIntent.putExtra("paymentMethod", paymentMethod);
        guessingFormResultIntent.putExtra("cardToken", cardToken);
        Instrumentation.ActivityResult guessingCardResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(GuessingNewCardActivity.class.getName())).respondWith(guessingCardResult);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intent congratsIntent = new Intent();
        Instrumentation.ActivityResult congratsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, congratsIntent);

        intending(hasComponent(OldCongratsActivity.class.getName())).respondWith(congratsResult);
        onView(withId(R.id.payButton)).perform(click());

        //TODO cambiar cuando creemos la nueva congrats
        intended(hasComponent(OldCongratsActivity.class.getName()));
    }

    @Test
    public void forCreatePaymentAPIFailureFinishActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mTestRule.addApiResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.addApiResponseToQueue("", 401, "");


        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.groupsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.payButton)).perform(click());

        mTestRule.isActivityFinishedOrFinishing();
    }

    @Test
    public void ifInvalidPreferenceSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        CheckoutPreference invalidPreference = preferenceWithoutExclusions;
        invalidPreference.setItems(null);
        validStartIntent.putExtra("checkoutPreference", "1234");

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }
}
