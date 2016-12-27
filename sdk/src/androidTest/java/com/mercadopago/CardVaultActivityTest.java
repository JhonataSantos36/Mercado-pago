package com.mercadopago;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.constants.Sites;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Card;
import com.mercadopago.model.DummyCard;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.CardTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
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
        CardToken cardToken = StaticMock.getCardToken();
        Issuer mockedIssuer = StaticMock.getIssuer();

        //Start guessing
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //Start issuers
        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mockedIssuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        //Start installments
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(GuessingCardActivity.class.getName())), times(1));
        intended((hasComponent(IssuersActivity.class.getName())), times(1));
        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
    }

    @Test
    public void onInstallmentsNotEnabledDontStartInstallments() {
        validStartIntent.putExtra("installmentsEnabled", false);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        CardToken mockedCardToken = StaticMock.getCardToken();
        Issuer mockedIssuer = StaticMock.getIssuer();
        //Start guessing
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(mockedCardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //Start issuers
        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mockedIssuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        //Dont start installments

        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(GuessingCardActivity.class.getName())), times(1));
        intended((hasComponent(IssuersActivity.class.getName())), times(1));
        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));

        ActivityResult activityResult = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        String pm = JsonUtil.getInstance().toJson(paymentMethod);
        assertTrue(pm.equals(activityResult.getExtras().getString("paymentMethod")));
        String iss = JsonUtil.getInstance().toJson(mockedIssuer);
        assertTrue(iss.equals(activityResult.getExtras().getString("issuer")));
    }

    private void sleep() {
        mTestRule.launchActivity(validStartIntent);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void onInstallmentsNotEnabledDontStartInstallmentsForSavedCardsForm() {
        Card card = StaticMock.getCard();
        Token token = StaticMock.getToken();

        validStartIntent.putExtra("installmentsEnabled", false);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));
    }

    @Test
    public void onInstallmentsEnabledInSavedCardFlowStartInstallmentsActivity() {
        Card card = StaticMock.getCard();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Token token = StaticMock.getToken();

        BigDecimal amount = new BigDecimal(100.50);

        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("amount", amount.toString());
        validStartIntent.putExtra("installmentsEnabled", true);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
    }

    @Test
    public void onInstallmentsEnabledWihoutAmountAndSiteFinishActivity() {
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        CardToken cardToken = StaticMock.getCardToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
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
        CardToken cardToken = StaticMock.getCardToken();

        //Add api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
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
    public void onGuessingResultCancelFinishActivityAndTrack() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        Token token = StaticMock.getToken();

        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));

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
        CardToken cardToken = StaticMock.getCardToken();

        //Start guessing
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //Start issuers
        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        //Start installments
        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(InstallmentsActivity.class.getName()));

        assertEquals(installmentList.get(0).getPayerCosts().get(0).getInstallments(), activity.mPresenter.getPayerCost().getInstallments());

    }

    @Test
    public void onMultipleInstallmentsStartErrorActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        CardToken cardToken = StaticMock.getCardToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

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
        CardToken cardToken = StaticMock.getCardToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");


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
    public void onIssuersResultCancelFinishActivity() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Issuer issuer = StaticMock.getIssuer();
        CardToken cardToken = StaticMock.getCardToken();

        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        Intent issuersResultIntent = new Intent();
        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, issuersResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);


        //Api calls for this flow
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        String paymentMethods = StaticMock.getPaymentMethodList();
        String issuers = StaticMock.getIssuersJson();
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
        mFakeAPI.addResponseToQueue(issuers, 200, "");


        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(IssuersActivity.class.getName()));

        assertEquals(null, activity.mPresenter.getIssuer());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void openSecurityCodeActivityOnSavedCardWithInstallmentsEnabled() {
        Card savedCard = StaticMock.getCard();
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(savedCard));

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));
    }

    @Test
    public void openSecurityCodeActivityOnSavedCardWithInstallmentsNotEnabled() {
        Card savedCard = StaticMock.getCard();
        validStartIntent.putExtra("installmentsEnabled", false);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(savedCard));

        //Api calls for this flow
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));
    }

    @Test
    public void openSecurityCodeActivityOnSavedCardAndCreateToken() {
        Card savedCard = StaticMock.getCard();
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(savedCard));

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));

        ActivityResult activityResult = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        String pm = JsonUtil.getInstance().toJson(savedCard.getPaymentMethod());
        assertTrue(pm.equals(activityResult.getExtras().getString("paymentMethod")));
        String iss = JsonUtil.getInstance().toJson(savedCard.getIssuer());
        assertTrue(iss.equals(activityResult.getExtras().getString("issuer")));
        String token = JsonUtil.getInstance().toJson(mockedToken);
        assertTrue(token.equals(activityResult.getExtras().getString("token")));
    }

    @Test
    public void finishOnSecurityCodeActivityCancel() {
        Card savedCard = StaticMock.getCard();
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(savedCard));

        String installments = StaticMock.getInstallmentsJson();
        Type listType = new TypeToken<List<Installment>>() {
        }.getType();
        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);

        //Api calls for this flow
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(installmentList, 200, "");
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent installmentsIntent = new Intent();
        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));

        assertTrue(activity.isFinishing());
    }

    @Test
    public void finishOnSecurityCodeActivityCancelWithoutInstallments() {
        Card savedCard = StaticMock.getCard();
        validStartIntent.putExtra("installmentsEnabled", false);
        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(savedCard));

        //Api calls for this flow
        Token mockedToken = StaticMock.getToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");

        Intent securityCodeIntent = new Intent();
        securityCodeIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        Instrumentation.ActivityResult securityCodeResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, securityCodeIntent);
        intending(hasComponent(SecurityCodeActivity.class.getName())).respondWith(securityCodeResult);

        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(0));
        intended((hasComponent(SecurityCodeActivity.class.getName())), times(1));

        assertTrue(activity.isFinishing());
    }

    //Recoverable token
    @Test
    public void showSecurityCodeActivityWhenPaymentRecoveryIsRecoverableToken(){
        Token token = StaticMock.getToken();
        Payment payment = StaticMock.getPaymentRejectedCallForAuthorize();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Issuer issuer  = StaticMock.getIssuer();

        PaymentRecovery paymentRecovery = new PaymentRecovery(token, payment, paymentMethod, payerCost, issuer);
        validStartIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(SecurityCodeActivity.class.getName()), times(1));
    }

    //Recoverable payment
    @Test
    public void showGuessingCardActivityWhenPaymentRecoveryIsNotRecoverableToken(){
        addBankDealsCall();
        addPaymentMethodsCall();

        Token token = StaticMock.getToken();
        Payment payment = StaticMock.getPaymentRejectedBadFilledSecurityCode();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
        Issuer issuer  = StaticMock.getIssuer();

        PaymentRecovery paymentRecovery = new PaymentRecovery(token, payment, paymentMethod, payerCost, issuer);
        validStartIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(GuessingCardActivity.class.getName()), times(1));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Test
    public void onSaveInstanceAndBringUpRestoreState() {
        validStartIntent.putExtra("amount", "1000");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
        validStartIntent.putExtra("installmentsEnabled", true);

        mTestRule.launchActivity(validStartIntent);

        new Handler(mTestRule.getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTestRule.getActivity().recreate();

            }
        });
        assertEquals(mTestRule.getActivity().mPresenter.getPublicKey(), mMerchantPublicKey);
        assertEquals(mTestRule.getActivity().mPresenter.getAmount().toString(), "1000");
        assertEquals(mTestRule.getActivity().mPresenter.getSite().getId(), Sites.ARGENTINA.getId());
        assertEquals(mTestRule.getActivity().mPresenter.installmentsRequired(), true);
    }

    private void addBankDealsCall() {
        List<BankDeal> bankDeals = StaticMock.getBankDeals();
        mFakeAPI.addResponseToQueue(bankDeals, 200, "");
    }

    private void addPaymentMethodsCall() {
        String paymentMethods = StaticMock.getPaymentMethodList();
        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
    }

//    @Test
//    public void onTokenApiFailOpenErrorActivity() {
//        validStartIntent.putExtra("amount", "1000");
//        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
//        validStartIntent.putExtra("installmentsEnabled", true);
//
//        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
//        Issuer issuer = StaticMock.getIssuer();
//        CardToken cardToken = StaticMock.getCardToken();
//
//        Intent guessingResultIntent = new Intent();
//        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
//        guessingResultIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
//        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);
//
//        Intent issuersResultIntent = new Intent();
//        issuersResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
//        Instrumentation.ActivityResult issuerResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, issuersResultIntent);
//        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);
//
//        String installments = StaticMock.getInstallmentsJson();
//        Type listType = new TypeToken<List<Installment>>() {
//        }.getType();
//        List<Installment> installmentList = JsonUtil.getInstance().getGson().fromJson(installments, listType);
//
//        //Api calls for this flow
//        List<BankDeal> bankDeals = StaticMock.getBankDeals();
//        String paymentMethods = StaticMock.getPaymentMethodList();
//        String issuers = StaticMock.getIssuersJson();
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(bankDeals), 200, "");
//        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
//        mFakeAPI.addResponseToQueue(issuers, 200, "");
//        mFakeAPI.addResponseToQueue(installmentList, 200, "");
//        mFakeAPI.addResponseToQueue("", 400, "");
//
//        Intent installmentsIntent = new Intent();
//        installmentsIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(installmentList.get(0).getPayerCosts().get(0)));
//        Instrumentation.ActivityResult installmentsResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsIntent);
//        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);
//
//        CardVaultActivity activity = mTestRule.launchActivity(validStartIntent);
//
//        intended((hasComponent(GuessingCardActivity.class.getName())), times(1));
//        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
//        intended((hasComponent(IssuersActivity.class.getName())), times(1));
//        intended((hasComponent(ErrorActivity.class.getName())), times(1));
//
//    }

}
