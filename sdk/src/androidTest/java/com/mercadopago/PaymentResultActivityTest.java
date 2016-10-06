package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.Card;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mromar on 7/11/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentResultActivityTest {

    @Rule
    public ActivityTestRule<PaymentResultActivity> mTestRule = new ActivityTestRule<>(PaymentResultActivity.class, true, false);
    public Intent validStartIntent, nullPaymentIntent, nullPublicKeyIntent, nullPaymentMethodIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;

    @Before
    public void validStartParameters() {
        mPayment = getPayment();
        mMerchantPublicKey = "1234";
        mPaymentMethod = getPaymentMethodCard();
    }

    @Before
    public void initIntents(){
        Intents.init();
    }

    @After
    public void releaseIntents(){
        Intents.release();
    }

    private PaymentMethod getPaymentMethodCard() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        return paymentMethod;
    }

    private PaymentMethod getOffLinePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("pagofacil");
        paymentMethod.setName("pagofacil");
        paymentMethod.setPaymentTypeId("ticket");
        return paymentMethod;
    }

    private Payment getPayment(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(300));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        Card card = new Card();
        card.setLastFourDigits("1234");

        Payer payer = new Payer();
        payer.setId("178101336");
        payer.setEmail("juan.perez@email.com");

        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setStatus("approved");
        payment.setStatusDetail("accredited");
        payment.setCard(card);
        payment.setId(123456789L);
        payment.setPaymentMethodId("master");
        payment.setInstallments(6);
        payment.setTransactionDetails(transactionDetails);
        payment.setCurrencyId("ARS");

        return payment;
    }

    private void createIntent(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPayment(){
        nullPaymentIntent = new Intent();
        nullPaymentIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        nullPaymentIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
    }

    private void createIntentWithNullPublicKey(){
        nullPublicKeyIntent = new Intent();
        nullPublicKeyIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        nullPublicKeyIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPaymentMethod(){
        nullPaymentMethodIntent = new Intent();
        nullPaymentMethodIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        nullPaymentMethodIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    @Test
    public void startCongratsActivityWhenStatusPaymentIsApproved(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(CongratsActivity.class.getName()), times(1));
    }

    @Test
    public void startCallForAuthorizeActivityWhenStatusPaymentIsRejectedForCallForAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(CallForAuthorizeActivity.class.getName()), times(1));
    }

    @Test
    public void startRejectionActivityWhenStatusPaymentIsRejected(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(RejectionActivity.class.getName()), times(1));
    }

    @Test
    public void startPendingActivityWhenStatusPaymentIsInProcess(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(PendingActivity.class.getName()), times(1));
    }

    @Test
    public void startInstructionsActivityWhenIsOffLinePaymentType(){
        mPaymentMethod = getOffLinePaymentMethod();

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        Intents.intended(hasComponent(InstructionsActivity.class.getName()), times(1));
    }

    @Test
    public void showErrorWhenStartResultActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(nullPaymentIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(nullPublicKeyIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithNullPaymentMethod() {
        createIntentWithNullPaymentMethod();
        mTestRule.launchActivity(nullPaymentMethodIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithNullPaymentStatus() {
        mPayment.setStatus(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithEmptyPaymentStatus() {
        mPayment.setStatus("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithIncorrectPaymentStatus() {
        mPayment.setStatus("other");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithNullPaymentStatusDetail() {
        mPayment.setStatusDetail(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartResultActivityWithEmptyPaymentStatusDetail() {
        mPayment.setStatusDetail("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void finishCongratsActivityWhenClickOnExitButton(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkExitCongrats)).perform(click());

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnExitButton(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkExitCallForAuthorize)).perform(ViewActions.scrollTo(),click());

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishRejectionActivityWhenClickOnExitButton(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkExitRejection)).perform(click());

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishPendingActivityWhenClickOnExitButton(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkExitPending)).perform(click());

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishInstructionsActivityWhenClickOnExitButton(){
        mPaymentMethod = getOffLinePaymentMethod();
        mPayment.setPaymentMethodId("pagofacil");

        Intent instructionsResultIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, instructionsResultIntent);

        intending(hasComponent(InstructionsActivity.class.getName())).respondWith(result);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnSelectOtherPaymentMethodButton(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).perform(click());

        //Result finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }
}
