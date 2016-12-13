package com.mercadopago;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.constants.Sites;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

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

    //TODO viene de guessing card activity test, arreglar
//    @Test
//    public void createTokenOnSecurityCodeNotRequired() {
////        addInitCalls();
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//        Issuer mockedIssuer = StaticMock.getIssuer();
//        List<Issuer> issuerList = new ArrayList<>();
//        issuerList.add(mockedIssuer);
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//        DummyCard card = CardTestUtils.getPaymentMethodOnWithoutRequiredSecurityCode();
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
////        identificationNumberIsCurrentEditText();
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
//        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
//        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
//        assertEquals(mockedToken.getId(), tokenResult.getId());
//        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
//        assertEquals("tarshop", pmResult.getId());
//
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
//    }

    //TODO viene de guessing card activity test, arreglar
//    @Test
//    public void createTokenOnIdentificationNumberNotRequired() {
////        addBankDealsCall();
//        PaymentMethod pm = StaticMock.getPaymentMethodWithIdentificationNotRequired();
//        List<PaymentMethod> paymentMethods = new ArrayList<>();
//        paymentMethods.add(pm);
//        mFakeAPI.addResponseToQueue(paymentMethods, 200, "");
//
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        Issuer mockedIssuer = StaticMock.getIssuer();
//        List<Issuer> issuerList = new ArrayList<>();
//        issuerList.add(mockedIssuer);
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("cordial").getCardNumber()));
////        sleep();
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
////        sleep();
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
////        sleep();
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
////        sleep();
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
//        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
//        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
//        assertEquals(mockedToken.getId(), tokenResult.getId());
//        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
//        assertEquals(pm.getId(), pmResult.getId());
//
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
//    }

    //TODO lo mismo
//    @Test
//    public void setTokenPMAndIssuerOnResult() {
////        addInitCalls();
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//        Issuer mockedIssuer = StaticMock.getIssuer();
//        List<Issuer> issuerList = new ArrayList<>();
//        issuerList.add(mockedIssuer);
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(CardTestUtils.getDummyCard("master").getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
//        assertEquals(mockedIssuer.getId(), selectedIssuer.getId());
//        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);
//        assertEquals(mockedToken.getId(), tokenResult.getId());
//        PaymentMethod pmResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("paymentMethod"), PaymentMethod.class);
//        assertEquals("master", pmResult.getId());
//
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
//    }
    //TODO lo mismo
//    @Test
//    public void openIssuerSelection() {
////        addInitCalls();
//
//        Token mockedToken = StaticMock.getTokenMasterIssuers();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        String issuers = StaticMock.getIssuersJson();
//        mFakeAPI.addResponseToQueue(issuers, 200, "");
//
//        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
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
//        onView(withId(R.id.mpsdkActivityIssuersView)).perform(actionOnItemAtPosition(0, click()));
//
//        Type listType = new TypeToken<List<Issuer>>() {
//        }.getType();
//        List<Issuer> issuerList = JsonUtil.getInstance().getGson().fromJson(issuers, listType);
//
//
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        Issuer selectedIssuer = JsonUtil.getInstance().fromJson(result.getExtras().getString("issuer"), Issuer.class);
//        assertNotNull(selectedIssuer);
//        assertEquals(issuerList.get(0).getId(), selectedIssuer.getId());
//
//        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
//
//    }
    //TODO lo mismo
//    @Test
//    public void onIssuerApiFailOpenErrorActivity() {
//        addInitCalls();
//
//        Token mockedToken = StaticMock.getTokenMasterIssuers();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        //Issuers call
//        mFakeAPI.addResponseToQueue("", 401, "");
//
//        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        intended(hasComponent(ErrorActivity.class.getName()));
//    }

    //TODO lo mismo
//    @Test
//    public void onEmptyIssuerListOpenErrorActivity() {
////        addInitCalls();
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//        List<Issuer> issuerList = new ArrayList<>();
//        mFakeAPI.addResponseToQueue(issuerList, 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(getDummyCard("master").getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        intended(hasComponent(ErrorActivity.class.getName()));
//    }
//TODO Arreglar
//    @Test
//    public void onTokenApiFailOpenErrorActivity() {
//        addInitCalls();

        //Token call
//        mFakeAPI.addResponseToQueue("", 401, "");
//
//        String issuers = StaticMock.getIssuersJson();
//        mFakeAPI.addResponseToQueue(issuers, 200, "");
//
//        DummyCard card = CardTestUtils.getPaymentMethodWithMultipleIssuers();
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        intended(hasComponent(ErrorActivity.class.getName()));
//    }

    //TODO arreglar
//    @Test
//    public void ifApiFailureIssuersRecover() {
//        addInitCalls();
//
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        //Issuers Call
//        mFakeAPI.addResponseToQueue("", 401, "");
//        String issuers = StaticMock.getIssuersJson();
//        mFakeAPI.addResponseToQueue(issuers, 200, "");
//
//        DummyCard card = CardTestUtils.getPaymentMethodOnWithBackSecurityCode();
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardNumber)).perform(typeText(card.getCardNumber()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardholderName)).perform(typeText(StaticMock.DUMMY_CARDHOLDER_NAME));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardExpiryDate)).perform(typeText(StaticMock.DUMMY_EXPIRATION_DATE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(card.getSecurityCode()));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//        onView(withId(R.id.mpsdkCardIdentificationNumber)).perform(typeText(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        Intent errorResultIntent = new Intent();
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, errorResultIntent);
//        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);
//
//        onView(withId(R.id.mpsdkErrorRetry)).perform(click());
//
//        intended(hasComponent(IssuersActivity.class.getName()));
//    }
}
