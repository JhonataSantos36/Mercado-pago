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
    public void displayRejectedHighRiskTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForHighRisk(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_rejection_high_risk))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_high_risk))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedInsufficientAmountTitleAndSubtitleWhenCreditCardPaymentStatusDetailIsRejectedForInsufficientAmount(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_insufficient_amount), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedInsufficientAmountTitleAndSubtitleWhenOffLinePaymentStatusDetailIsRejectedForInsufficientAmount(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);
        mPayment.setPaymentMethodId("pagofacil");

        mPaymentMethod = getOffLinePaymentMethod();

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_insufficient_amount), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_insufficient_amount))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedOtherReasonTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForOtherReason(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_select_other_rejection))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedMaxAttemptsTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForMaxAttempts(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_rejection_max_attempts))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_max_attempts))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedDuplicatedPaymentTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForDuplicatedPayment(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_duplicated_payment))));
        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedCardDisabledTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForCardDisabled(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_active_card), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_card_disabled))));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayRejectedBadFilledOtherTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForBadFilledOther(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_enter_again))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_cancel_payment_and_continue))));
    }

    @Test
    public void displayRejectedBadFilledCardNumberTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForBadFilledCardNumber(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_enter_again))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_cancel_payment_and_continue))));
    }

    @Test
    public void displayRejectedBadFilledSecurityCodeTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForBadFilledSecurityCode(){
        String titleMessage;

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_enter_again))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_cancel_payment_and_continue))));
    }

    @Test
    public void displayRejectedBadFilledDateTitleAndSubtitleWhenPaymentStatusDetailIsRejectedForBadFilledDate(){
        String titleMessage;
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        titleMessage = String.format(mTestRule.getActivity().getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethod.getName());
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(titleMessage)));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_enter_again))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_cancel_payment_and_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenStatusDetailNotExist(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail("bad_fill_security_code");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //SomethingWentWrong text
        onView(withId(R.id.mpsdkSomethingWentWrong)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_rejection_title))));

        //Title
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));

        //SelectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkRejectionOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void finishRejectionActivityWhenClickOnSelectOtherPaymentMethod(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //SelectOtherPaymentMethod button isDisplayed
        onView(withId(R.id.mpsdkRejectionOptionButton)).check(matches(isDisplayed()));

        //Click on selectOtherPaymentMethod button
        onView(withId(R.id.mpsdkRejectionOptionButton)).perform(click());

        //Rejection finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishRejectionActivityWhenClickOnKeepBuyingRejection(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Keep buying button isDisplayed
        onView(withId(R.id.mpsdkKeepBuyingRejection)).check(matches(isDisplayed()));

        //Click on keep buying button
        onView(withId(R.id.mpsdkKeepBuyingRejection)).perform(click());

        //Rejection finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showErrorWhenPaymentStatusDetailIsNull(){
        mPayment.setStatusDetail(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentStatusDetailIsEmpty(){
        mPayment.setStatusDetail("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentMethodIdIsNull(){
        mPaymentMethod.setId(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentMethodIdIsEmpty(){
        mPaymentMethod.setId("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentMethodNameIsNull(){
        mPaymentMethod.setName(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentMethodNameIsEmpty(){
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
    public void showErrorWhenPaymentMethodTypeIdIsEmpty(){
        mPaymentMethod.setPaymentTypeId("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenPaymentPaymentMethodIdIsDifferentToPaymentMethodId(){
        mPaymentMethod.setId("visa");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartRejectionActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(nullPaymentIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartRejectionActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(nullPublicKeyIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartRejectionActivityWithNullPaymentMethod() {
        createIntentWithNullPaymentMethod();
        mTestRule.launchActivity(nullPaymentMethodIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void noFinishRejectionActivityWhenClickOnBackButton(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //Rejection finish
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test (expected = NoActivityResumedException.class)
    public void finishRejectionActivityWhenClickOnBackButtonTwoTimes(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();

        assertTrue(mTestRule.getActivity().isFinishing());
    }
}