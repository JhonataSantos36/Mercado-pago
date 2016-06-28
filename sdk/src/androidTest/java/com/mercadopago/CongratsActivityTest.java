package com.mercadopago;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.Spanned;
import android.widget.ImageView;

import com.mercadopago.model.Card;
import com.mercadopago.model.FeeDetail;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.CurrenciesUtil;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.not;

/**
 * Created by mromar on 6/21/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class CongratsActivityTest extends TestCase {

    @Rule
    public MockedApiTestRule<CongratsActivity> mTestRule = new MockedApiTestRule<>(CongratsActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;

    @Before
    public void validStartIntent() {
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
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);
    }

    private void createIntentWithoutPayment(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
    }

    private void createIntentWithoutPaymentMethod(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("payment", mPayment);
    }

    @Test
    public void showCongratsLayoutWithZeroRateWhenApprovedPaymentHasZeroRate(){
        String paymentIdDescription, lastFourDigitsDescription;
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with zero rate
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    private Spanned setTotalAmountDescription() {
        StringBuilder totalAmountDescription = new StringBuilder();

        totalAmountDescription.append("( ");
        totalAmountDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        totalAmountDescription.append(" )");

        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), totalAmountDescription.toString(), true, true);
    }

    private Spanned setInstallmentsDescription(){
        StringBuilder installmentsDescription = new StringBuilder();

        installmentsDescription.append(mPayment.getInstallments());
        installmentsDescription.append(" ");
        installmentsDescription.append(mTestRule.getActivity().getString(R.string.mpsdk_installments_of));
        installmentsDescription.append(" ");
        installmentsDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));

        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), installmentsDescription.toString(), true, true);
    }

    @Test
    public void showCongratsWithTotalAmountWhenApprovedPaymentHasRate(){
        String paymentIdDescription, lastFourDigitsDescription;
        Spanned installmentsDescription, totalAmountDescription;
        Bitmap bitmap, paymentBitmap;

        FeeDetail feeDetail = new FeeDetail();
        feeDetail.setType("financing_fee");
        feeDetail.setAmount(new BigDecimal(10));
        List<FeeDetail> feeDetails = new ArrayList<>();
        feeDetails.add(feeDetail);

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);
        mPayment.setFeeDetails(feeDetails);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with rate
        totalAmountDescription = setTotalAmountDescription();
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(totalAmountDescription.toString())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutEmailWhenPaymentIsApprovedWithNullEmail(){
        String paymentIdDescription, lastFourDigitsDescription;
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        Payer payer = new Payer();
        payer.setId("178101336");
        payer.setEmail(null);

        mPayment = getPayment();
        mPayment.setPayer(payer);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(not(isDisplayed())));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(not(isDisplayed())));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with zero rate
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutEmailWhenPaymentIsApprovedWithEmptyEmail(){
        String paymentIdDescription, lastFourDigitsDescription;
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        Payer payer = new Payer();
        payer.setId("178101336");
        payer.setEmail("");

        mPayment = getPayment();
        mPayment.setPayer(payer);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(not(isDisplayed())));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(not(isDisplayed())));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with zero rate
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutLastFourDigitsCardWhenPaymentIsApprovedWithNullLastFourDigitsCard(){
        String paymentIdDescription;
        Spanned installmentsDescription;

        mPayment = getPayment();
        mPayment.setCard(null);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(not(isDisplayed())));

        //PaymentMethod image
        onView(withId(R.id.mpsdkPaymentMethodImage)).check(matches(not(isDisplayed())));

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with zero rate
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveNegativeInstallmentsNumber(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        mPayment = getPayment();
        mPayment.setInstallments(-1);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //Total amount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithTotalAmountAndWithoutInstallmentsDescriptionWhenCongratsReceiveZeroInstallmentNumber(){
        String paymentIdDescription, lastFourDigitsDescription;
        StringBuilder installments = new StringBuilder();
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        mPayment = getPayment();
        mPayment.setInstallments(0);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description with total amount
        installments.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        installmentsDescription = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), installments.toString(), true, true);
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithTotalAmountAndWithoutInstallmentsDescriptionWhenCongratsReceiveOneInstallmentNumber(){
        String paymentIdDescription, lastFourDigitsDescription;
        StringBuilder installments = new StringBuilder();
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        mPayment = getPayment();
        mPayment.setInstallments(1);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installments.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        installmentsDescription = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), installments.toString(), true, true);
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveNegativeInstallmentAmount(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(-10));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        mPayment = getPayment();
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveZeroInstallmentAmount(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(0));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        mPayment = getPayment();
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveCurrencyNull(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(0));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        mPayment = getPayment();
        mPayment.setCurrencyId(null);
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveCurrencyEmpty(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(0));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        mPayment = getPayment();
        mPayment.setCurrencyId("");
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutInstallmentsDescriptionWhenCongratsReceiveCurrencyInvalid(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(0));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        mPayment = getPayment();
        mPayment.setCurrencyId("MLA");
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutTotalAmountDescriptionWhenCongratsReceiveNegativeTotalAmount(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(250));
        transactionDetails.setTotalPaidAmount(new BigDecimal(-1800));

        mPayment = getPayment();
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }


    @Test
    public void showCongratsLayoutWithoutTotalAmountDescriptionWhenCongratsReceiveZeroTotalAmount(){
        String paymentIdDescription, lastFourDigitsDescription;
        Bitmap bitmap, paymentBitmap;

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(250));
        transactionDetails.setTotalPaidAmount(new BigDecimal(0));

        mPayment = getPayment();
        mPayment.setInstallments(6);
        mPayment.setTransactionDetails(transactionDetails);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(not(isDisplayed())));

        //TotalAmount description
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(not(isDisplayed())));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showCongratsLayoutWithoutPaymentIdWhenCongratsReceiveNullPaymentId(){
        String lastFourDigitsDescription;
        Spanned installmentsDescription;
        Bitmap bitmap, paymentBitmap;

        mPayment = getPayment();
        mPayment.setId(null);
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Email description
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //LastFourDigits Card
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //PaymentMethod image
        ImageView paymentMethodImage = (ImageView) mTestRule.getActivity().findViewById(R.id.mpsdkPaymentMethodImage);
        if (paymentMethodImage != null && paymentMethodImage.getDrawable() != null){
            bitmap = ((BitmapDrawable) paymentMethodImage.getDrawable()).getBitmap();
            paymentBitmap = ((BitmapDrawable) ContextCompat.getDrawable(mTestRule.getActivity(), R.drawable.master)).getBitmap();
            assertTrue(bitmap == paymentBitmap);
        }

        //Installments description
        installmentsDescription = setInstallmentsDescription();
        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installmentsDescription.toString())));

        //Total amount description with zero rate
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //State description
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkPaymentIdSeparator)).check(matches(not(isDisplayed())));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    //TODO validar
    @Test(expected = NoActivityResumedException.class)
    public void finishCongratsLayoutWhenClickOnExitCongrats(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_APPROVED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitCongrats)).perform(click());
    }

    @Test
    public void showPendingLayoutWhenPaymentIsPendingForContingency(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkPendingTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))));
        onView(withId(R.id.mpsdkPendingSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_pending_contingency))));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));
    }

    @Test
    public void showPendingLayoutWhenPaymentIsPendingForReviewManual(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkPendingTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))));
        onView(withId(R.id.mpsdkPendingSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_pending_contingency))));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));
    }

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishPendingLayoutWhenClickOnExitPending(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitPending)).perform(click());
    }

    @Test
    public void showCallForAuthorizeLayoutWhenPaymentIsRejectedForCallForAuthorize(){
        Spanned callForAuthorizeTitle;

        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutPaymentMethodIdWhenPaymentMethodIsNull(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(null);
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        //createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutPaymentMethodIdWhenPaymentMethodIsEmpty(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("");
        //paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        //createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutPaymentMethodNameWhenPaymentMethodIsNull(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName(null);
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        //createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutPaymentMethodNameWhenPaymentMethodIsEmpty(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("");
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        //createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutTotalAmountWhenTotalAmountIsNegative(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(-1));

        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutTotalAmountWhenTotalAmountIsZero(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(0));

        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutTotalAmountWhenCurrencyIsNull(){
        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutTotalAmountWhenCurrencyIsEmpty(){
        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayoutWithoutTotalAmountWhenCurrencyIsInvalid(){
        mPayment = getPayment();
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
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Exit button is displayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishCallForAuthorizeLayoutWhenClickOnAuthorizedPaymentMethod(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).perform(click());
    }

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishCallForAuthorizeLayoutWhenClickOnSelectOtherPaymentMethod(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).perform(click());
    }

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishCallForAuthorizeLayoutWhenClickOnExitCallForAuthorize(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitCallForAuthorize)).perform(click());
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
    public void showRejectedLayoutWhenPaymentIsRejectedForHighRisk(){
        mPayment = getPayment();
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
    public void showRejectedLayoutWhenPaymentIsRejectedForInsufficientAmount(){
        String titleMessage;

        mPayment = getPayment();
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
    public void showRejectedLayoutWhenPaymentIsRejectedForInsufficientAmountWithPaymentMethodIdNull(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(null);
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForInsufficientAmountWithPaymentMethodIdEmpty(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("");
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForInsufficientAmountWithPaymentMethodNameNull(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName(null);
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForInsufficientAmountWithPaymentMethodNameEmpty(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("");
        paymentMethod.setPaymentTypeId("credit_card");
        mPaymentMethod = paymentMethod;

        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT);

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForOtherReason(){
        String titleMessage;

        mPayment = getPayment();
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
        mPayment = getPayment();
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

        mPayment = getPayment();
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

        mPayment = getPayment();
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

        mPayment = getPayment();
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

        mPayment = getPayment();
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
        mPayment = getPayment();
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
        mPayment = getPayment();
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

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishRejectionLayoutWhenClickOnSelectOtherPaymentMethod(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).perform(click());
    }

    //TODO resolver, falla
    @Test(expected = NoActivityResumedException.class)
    public void finishRejectionLayoutWhenClickOnExitRejection(){
        mPayment = getPayment();
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitRejection)).perform(click());
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForNullPayment(){
        createIntentWithoutPayment();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showRejectedLayoutWhenPaymentIsRejectedForNullPaymentMethod(){
        createIntentWithoutPaymentMethod();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_bad_filled_other_rejection))));
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(not(isDisplayed())));
    }
}
