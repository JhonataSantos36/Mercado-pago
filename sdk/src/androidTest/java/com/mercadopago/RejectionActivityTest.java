package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.Card;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.JsonUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mromar on 7/6/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RejectionActivityTest {

    @Rule
    public ActivityTestRule<RejectionActivity> mTestRule = new ActivityTestRule<>(RejectionActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;

    @Before
    public void validStartParameters() {
        mPayment = getPayment();
        mMerchantPublicKey = "1234";
        mPaymentMethod = getPaymentMethodCard();
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

    private Payment getPayment() {
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
        payment.setStatus("rejected");
        payment.setStatusDetail("rejected_high_risk");
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
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
    }

    private void createIntentWithNullPublicKey(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPaymentMethod(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForHighRisk(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_rejection_high_risk))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_high_risk))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenCreditCardPaymentIsRejectedForInsufficientAmount(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        titleMessage = mTestRule.getActivity().getString(R.string.mpsdk_text_you) + " " + mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_text_insufficient_amount);
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenOffLinePaymentIsRejectedForInsufficientAmount(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);
        mPayment.setPaymentMethodId("pagofacil");

        mPaymentMethod = getOffLinePaymentMethod();

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        titleMessage = mTestRule.getActivity().getString(R.string.mpsdk_text_you) + " " + mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_text_insufficient_amount);
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_insufficient_amount))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForOtherReason(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        titleMessage = mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_title_other_reason_rejection);
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_select_other_rejection))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForMaxAttempts(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_rejection_max_attempts))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_max_attempts))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForDuplicatedPayment(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        titleMessage = mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_title_other_reason_rejection);
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_duplicated_payment))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForCardDisabled(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        titleMessage = mTestRule.getActivity().getString(R.string.mpsdk_text_call_to) + " " + mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_text_active_card);
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_card_disabled))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForBadFilledOther(){
        String subtitleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        subtitleMessage = mTestRule.getActivity().getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_text_is_incorrect);
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(subtitleMessage)));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForBadFilledCardNumber(){
        String subtitleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        subtitleMessage = mTestRule.getActivity().getString(R.string.mpsdk_text_some_number) + " " + mPaymentMethod.getName() + " " + mTestRule.getActivity().getString(R.string.mpsdk_text_is_incorrect);
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(subtitleMessage)));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForBadFilledSecurityCode(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_security_code_rejection))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForBadFilledDate(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_date_rejection))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void showRejectedLayoutWhenStatusDetailIsValidButNoExist(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail("bad_fill_security_code");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    @Test
    public void finishRejectionLayoutWhenClickOnSelectOtherPaymentMethod(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).perform(click());

        //Rejection finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishRejectionLayoutWhenClickOnExitRejection(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitRejection)).perform(click());

        //Rejection finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showErrorLayoutWhenPaymentStatusDetailIsNull(){
        mPayment.setStatusDetail(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentStatusDetailIsEmpty(){
        mPayment.setStatusDetail("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodIdIsNull(){
        mPaymentMethod.setId(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodIdIsEmpty(){
        mPaymentMethod.setId("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodNameIsNull(){
        mPaymentMethod.setName(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodNameIsEmpty(){
        mPaymentMethod.setName("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodTypeIdIsNull(){
        mPaymentMethod.setPaymentTypeId(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodTypeIdIsEmpty(){
        mPaymentMethod.setPaymentTypeId("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenPaymentMethodIsDifferentToPaymentMethodOfPaymentObject(){
        mPaymentMethod.setId("visa");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenStartRejectionActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenStartRejectionActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenStartRejectionActivityWithNullPaymentMethod() {
        createIntentWithNullPaymentMethod();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void noFinishRejectionLayoutWhenClickOnBackButton(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //Rejection finish
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test (expected = NoActivityResumedException.class)
    public void finishRejectionLayoutWhenClickOnBackButtonTwoTimes(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();

        assertTrue(mTestRule.getActivity().isFinishing());
    }
}

