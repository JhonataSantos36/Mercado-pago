package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.constants.Sites;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by vaserber on 7/29/16.
 */
public class CardVaultActivityTest {


    @Rule
    public ActivityTestRule<CardVaultActivity> mTestRule = new ActivityTestRule<>(CardVaultActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;

    private FakeAPI mFakeAPI;

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_MERCHANT_PUBLIC_KEY;

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
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
    public void onInstallmentsEnabledMissingAmountAndSiteFinishActivity() {
        validStartIntent.putExtra("installmentsEnabled", true);
        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertTrue(activity.isFinishing());
    }

    @Test
    public void onInstallmentsEnabledStartInstallments() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(InstallmentsActivity.class.getName()));
    }

    @Test
    public void onInstallmentsNotEnabledDontStartInstallments() {
        validStartIntent.putExtra("installmentsEnabled", false);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);


        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));
    }

    @Test
    public void withoutInstallmentsEnabledDontStartInstallments() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);


        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));
    }

    @Test
    public void onInstallmentsEnabledWihoutAmountAndSiteFinishActivity() {
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertTrue(activity.isFinishing());
    }

    @Test
    public void onValidParamsStartGuessing() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(GuessingCardActivity.class.getName()));
    }

    @Test
    public void onGuessingResultCancelFinishActivity() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(GuessingCardActivity.class.getName()));

        assertEquals(null, activity.mPresenter.getPayerCost());
        assertEquals(null, activity.mPresenter.getIssuer());
        assertEquals(null, activity.mPresenter.getToken());
        assertEquals(null, activity.mPresenter.getPaymentMethod());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void onInstallmentsResultFinishWithPayerCost() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(InstallmentsActivity.class.getName()));

        assertEquals(installmentList.get(0).getPayerCosts().get(0).getInstallments(), activity.mPresenter.getPayerCost().getInstallments());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void onMultipleInstallmentsStartErrorActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getMultipleInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onUniquePayerCostFinishActivityWithResult() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsWithUniquePayerCostJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        assertEquals(installmentList.get(0).getPayerCosts().get(0).getInstallments(), activity.mPresenter.getPayerCost().getInstallments());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void onEmptyPayerCostListStartErrorActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        List<Installment> installmentList = new ArrayList<>();
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onInstallmentsCallApiErrorOpenErrorActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //Installments call
        mFakeAPI.addResponseToQueue("", 401, "");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void onInstallmentsResultCancelFinishActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(InstallmentsActivity.class.getName()));

        assertEquals(null, activity.mPresenter.getPayerCost());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void onInstallmentsCallApiErrorRetryAndRecover() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //First installments call
        mFakeAPI.addResponseToQueue("", 401, "");

        //Second installments call
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
        mFakeAPI.addResponseToQueue(installmentList, 200, "");

        mTestRule.launchActivity(validStartIntent);

        Intent errorResultIntent = new Intent();
        Instrumentation.ActivityResult retryResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(retryResult);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        intended(hasComponent(InstallmentsActivity.class.getName()));
    }

}
