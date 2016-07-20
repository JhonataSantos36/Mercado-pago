package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.Spanned;

import com.mercadopago.model.Card;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;

import junit.framework.Assert;

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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 7/7/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CallForAuthorizeActivityTest {

    @Rule
    public ActivityTestRule<CallForAuthorizeActivity> mTestRule = new ActivityTestRule<>(CallForAuthorizeActivity.class, true, false);
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

    private PaymentMethod getPaymentMethodCard() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
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
        payment.setStatusDetail("cc_rejected_call_for_authorize");
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
    public void displayCallForAuthorizeTitleAndSubtitleWhenPaymentStatusIsRejectedAndPaymentStatusDetailIsForCallForAuthorize(){
        Spanned callForAuthorizeTitle;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        callForAuthorizeTitle = setCallForAuthorizeTitle();
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(callForAuthorizeTitle.toString())));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentPaymentMethodIdIsDifferenceToPaymentMethodId(){
        mPayment.setPaymentMethodId("visa");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodIdIsNull(){
        mPaymentMethod.setId(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodIdIsEmpty(){
        mPaymentMethod.setId("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodNameIsNull(){
        mPaymentMethod.setName(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodNameIsEmpty(){
        mPaymentMethod.setName("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodPaymentTypeIdIsNull(){
        mPaymentMethod.setPaymentTypeId(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodPaymentTypeIdIsEmpty(){
        mPaymentMethod.setPaymentTypeId("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenTotalAmountIsNegative(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(-1));

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
        mPayment.setTransactionDetails(transactionDetails);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenTotalAmountIsZero(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(0));

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
        mPayment.setTransactionDetails(transactionDetails);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentCurrencyIdIsNull(){
        mPayment.setCurrencyId(null);
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentCurrencyIdIsEmpty(){
        mPayment.setCurrencyId("");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenCurrencyIdIsInvalid(){
        mPayment.setCurrencyId("MLA");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnPaymentMethodAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).perform(click());

        //Congrats finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnSelectOtherPaymentMethod(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize)).perform(click());

        //Congrats finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnExitCallForAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitCallForAuthorize)).perform(click());

        //Congrats finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    private Spanned setCallForAuthorizeTitle() {
        StringBuilder callForAuthorizeTitle = new StringBuilder();

        callForAuthorizeTitle.append(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_call_for_authorize));
        callForAuthorizeTitle.append(" " + mPaymentMethod.getName() + " ");
        callForAuthorizeTitle.append(mTestRule.getActivity().getString(R.string.mpsdk_text_the_payment) + " ");
        callForAuthorizeTitle.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        callForAuthorizeTitle.append(" " + mTestRule.getActivity().getString(R.string.mpsdk_text_to_mercado_pago));

        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),mPayment.getCurrencyId(), callForAuthorizeTitle.toString(), true, true);
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(nullPaymentIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(nullPublicKeyIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPaymentMethod() {
        createIntentWithNullPaymentMethod();
        mTestRule.launchActivity(nullPaymentMethodIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void noFinishCallForAuthorizeActivityWhenClickOnBackButton(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //Congrats finish
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test (expected = NoActivityResumedException.class)
    public void finishCallForAuthorizeActivityWhenClickOnBackButtonTwoTimes(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();

        Assert.assertTrue(mTestRule.getActivity().isFinishing());
    }
}
