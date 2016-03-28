package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ImageView;

import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
        Assert.assertTrue(activity.mCheckoutPreference != null
                && activity.mCheckoutPreference.getId().equals(preferenceWithoutExclusions.getId())
                && activity.mMerchantPublicKey != null
                && activity.mMerchantPublicKey.equals("1234"));
    }

    @Test
    public void ifValidStartInstantiateMercadoPago() {
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);
        Assert.assertTrue(activity.mMercadoPago != null);
    }

    @Test
    public void whenPaymentMethodReceivedShowPaymentMethodRow() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.contentLayout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.payment_method_comment))
                .check(matches(withText(paymentMethodInfo)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.payment_method_image);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

        Assert.assertTrue(bitmap == bitmap2);
    }

    @Test
    public void whenEditButtonClickStartPaymentVaultActivity() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("oxxo");
        paymentMethod.setName("Oxxo");
        paymentMethod.setPaymentTypeId("ticket");

        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        onView(withId(R.id.imageEdit)).perform(click());

        Intents.intended(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName()));
    }

    @Test
    public void onBackPressedAfterEditImageClickedRestoreState() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");
        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodsJson(), 200, "");

        onView(withId(R.id.imageEdit)).perform(click());

        pressBack();

        onView(withId(R.id.contentLayout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.payment_method_comment))
                .check(matches(withText(paymentMethodInfo)));

        ImageView paymentMethodImage = (ImageView) activity.findViewById(R.id.payment_method_image);

        Bitmap bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.oxxo)).getBitmap();

        Assert.assertTrue(bitmap == bitmap2);
    }

    @Test
    public void onBackPressedAfterPaymentMethodSelectionStartPaymentVault() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");
        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodsJson(), 200, "");

        pressBack();

        onView(withId(R.id.groupsList))
                .check(matches(isDisplayed()));
    }

    @Test(expected = NoActivityResumedException.class)
    public void onBackPressedTwiceAfterPaymentMethodSelectionFinishActivity() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        mTestRule.restartIntents();

        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodSearchAsJson(), 200, "");
        mTestRule.addApiResponseToQueue(StaticMock.getCompletePaymentMethodsJson(), 200, "");

        pressBack();
        pressBack();
    }

    @Test
    public void whenPaymentMethodSelectedShowShoppingCart() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);
        Assert.assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testCloseShoppingCart() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);
        Assert.assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);
    }

    @Test
    public void testCloseAndOpenShoppingCart() {
        PaymentMethod paymentMethod = getOfflinePaymentMethod();
        String paymentMethodInfo = "Dummy info";

        Intent paymentVaultResultIntent = new Intent();
        paymentVaultResultIntent.putExtra("paymentMethod", paymentMethod);
        paymentVaultResultIntent.putExtra("paymentMethodInfo", paymentMethodInfo);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, paymentVaultResultIntent);

        mTestRule.initIntents();
        Intents.intending(IntentMatchers.hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);
        CheckoutActivity activity = mTestRule.launchActivity(validStartIntent);

        View itemInfoLayout = activity.findViewById(R.id.itemInfoLayout);
        Assert.assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertTrue(itemInfoLayout.getVisibility() != View.VISIBLE);

        onView(withId(R.id.shoppingCartIcon)).perform(click());
        Assert.assertTrue(itemInfoLayout.getVisibility() == View.VISIBLE);
    }

    //VALIDATIONS TESTS

    @Test
    public void ifPublicKeyNotSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        validStartIntent.putExtra("checkoutPreference", preferenceWithoutExclusions);

        mTestRule.launchActivity(invalidStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifPreferenceNotSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        validStartIntent.putExtra("publicKey", "1234");

        mTestRule.launchActivity(invalidStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    @Test
    public void ifInvalidPreferenceSetCallFinish() {
        mTestRule.initIntents();

        Intent invalidStartIntent = new Intent();
        CheckoutPreference invalidPreference = preferenceWithoutExclusions;
        invalidPreference.setItems(null);
        validStartIntent.putExtra("checkoutPreference", "1234");

        mTestRule.launchActivity(invalidStartIntent);
        Assert.assertTrue(mTestRule.isActivityFinishedOrFinishing());
    }

    private PaymentMethod getOfflinePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("oxxo");
        paymentMethod.setName("Oxxo");
        paymentMethod.setPaymentTypeId("ticket");
        return paymentMethod;
    }

}
