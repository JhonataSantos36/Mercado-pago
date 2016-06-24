package com.mercadopago;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.Spanned;

import com.mercadopago.model.Card;
import com.mercadopago.model.FeeDetail;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by mromar on 6/21/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class CongratsActivityTest {

    @Rule
    public MockedApiTestRule<CongratsActivity> mTestRule = new MockedApiTestRule<>(CongratsActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;

    @Before
    public void validStartIntent() {
        mPayment = StaticMock.getPayment();
        mMerchantPublicKey = "1234";
        mPaymentMethod = getPaymentMethodCard();
        mPaymentMethod.setId("visa");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);
    }

    private PaymentMethod getPaymentMethodCard() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        return paymentMethod;
    }

    @Test
    public void paymentApprovedWithZeroRate(){
        String paymentIdDescription, lastFourDigitsDescription;
        StringBuffer installmentsDescription = new StringBuffer();
        StringBuffer totalAmountDescription = new StringBuffer();
        mPayment = getApprovedPayment();

        mPaymentMethod = new PaymentMethod();
        mPaymentMethod.setId("master");
        mPaymentMethod.setName("Master");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        mTestRule.launchActivity(validStartIntent);

        //Correct title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Correct email
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //Correct lastFourDigits
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //Correct paymentMethod image
        //TODO
        //onView(withId(R.id.mpsdkPaymentMethodImage)).check(matches(withId(MercadoPagoUtil.getPaymentMethodIcon(mTestRule.getActivity(), mPaymentMethod.getId()))));

        //Correct installments
        installmentsDescription.append(mPayment.getInstallments());
        installmentsDescription.append(" ");
        installmentsDescription.append(mTestRule.getActivity().getString(R.string.mpsdk_installments_of));
        installmentsDescription.append(" ");
        installmentsDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        Spanned installments = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), installmentsDescription.toString(), true, true);

        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installments.toString())));

        //Correct total amount description
        totalAmountDescription.append("( ");
        totalAmountDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        totalAmountDescription.append(" )");
        Spanned totalAmount = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), totalAmountDescription.toString(), true, true);
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_zero_rate))));

        //Correct state
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    //TODO no corre
    @Test
    public void paymentApprovedWithRate(){
        String paymentIdDescription, lastFourDigitsDescription;
        StringBuffer installmentsDescription = new StringBuffer();
        StringBuffer totalAmountDescription = new StringBuffer();
        mPayment = getApprovedPayment();

        FeeDetail feeDetail = new FeeDetail();
        feeDetail.setType("financing_fee");
        List<FeeDetail> feeDetails = new List<FeeDetail>() {
            @Override
            public void add(int location, FeeDetail object) {

            }

            @Override
            public boolean add(FeeDetail object) {
                return false;
            }

            @Override
            public boolean addAll(int location, Collection<? extends FeeDetail> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends FeeDetail> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object object) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public FeeDetail get(int location) {
                return null;
            }

            @Override
            public int indexOf(Object object) {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Iterator<FeeDetail> iterator() {
                return null;
            }

            @Override
            public int lastIndexOf(Object object) {
                return 0;
            }

            @Override
            public ListIterator<FeeDetail> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<FeeDetail> listIterator(int location) {
                return null;
            }

            @Override
            public FeeDetail remove(int location) {
                return null;
            }

            @Override
            public boolean remove(Object object) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public FeeDetail set(int location, FeeDetail object) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public List<FeeDetail> subList(int start, int end) {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] array) {
                return null;
            }
        };
        feeDetail.isFinancialFree();
        mPayment.setFeeDetails(feeDetails);

        mPaymentMethod = new PaymentMethod();
        mPaymentMethod.setId("master");
        mPaymentMethod.setName("Master");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        mTestRule.launchActivity(validStartIntent);

        //Correct title and subtitle
        onView(withId(R.id.mpsdkCongratulationsTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_congrat))));
        onView(withId(R.id.mpsdkCongratulationSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_action_activity_congrat))));

        //Correct email
        onView(withId(R.id.mpsdkPayerEmailDescription)).check(matches(withText("juan.perez@email.com")));

        //Correct lastFourDigits
        lastFourDigitsDescription = mTestRule.getActivity().getString(R.string.mpsdk_last_digits_label) + " " + "1234";
        onView(withId(R.id.mpsdkLastFourDigitsDescription)).check(matches(withText(lastFourDigitsDescription)));

        //Correct paymentMethod image
        //TODO
        //onView(withId(R.id.mpsdkPaymentMethodImage)).check(matches(withId(MercadoPagoUtil.getPaymentMethodIcon(mTestRule.getActivity(), mPaymentMethod.getId()))));

        //Correct installments
        installmentsDescription.append(mPayment.getInstallments());
        installmentsDescription.append(" ");
        installmentsDescription.append(mTestRule.getActivity().getString(R.string.mpsdk_installments_of));
        installmentsDescription.append(" ");
        installmentsDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        Spanned installments = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), installmentsDescription.toString(), true, true);

        onView(withId(R.id.mpsdkInstallmentsDescription)).check(matches(withText(installments.toString())));

        //Correct total amount description
        totalAmountDescription.append("( ");
        totalAmountDescription.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        totalAmountDescription.append(" )");
        Spanned totalAmount = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), totalAmountDescription.toString(), true, true);
        onView(withId(R.id.mpsdkInterestAmountDescription)).check(matches(withText(totalAmount.toString())));

        //Correct state
        onView(withId(R.id.mpsdkStateDescription)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_state_acount_activity_congrat))));

        //Correct paymentId
        paymentIdDescription = mTestRule.getActivity().getString(R.string.mpsdk_payment_id_description) + " " + "123456789";
        onView(withId(R.id.mpsdkPaymentIdDescription)).check(matches(withText(paymentIdDescription)));

        //Button isDisplayed
        onView(withId(R.id.mpsdkExitCongrats)).check(matches(isDisplayed()));
    }

    @Test
    public void showPendingLayout(){
        mPayment = getPendingPayment();

        mPaymentMethod = new PaymentMethod();
        mPaymentMethod.setId("master");
        mPaymentMethod.setName("Master");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPendingTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))));
        onView(withId(R.id.mpsdkPendingSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_pending_contingency))));

        //Button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));
    }

    @Test
    public void showCallForAuthorizeLayout(){
        StringBuilder callForAuthorizeTitle = new StringBuilder();

        mPayment = getCallForAuthorizePayment();

        mPaymentMethod = new PaymentMethod();
        mPaymentMethod.setId("master");
        mPaymentMethod.setName("Master");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        mTestRule.launchActivity(validStartIntent);

        //Correct title
        callForAuthorizeTitle.append(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_call_for_authorize));
        callForAuthorizeTitle.append(" " + mPaymentMethod.getName() + " ");
        callForAuthorizeTitle.append(mTestRule.getActivity().getString(R.string.mpsdk_text_the_payment) + " ");
        callForAuthorizeTitle.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        callForAuthorizeTitle.append(" " + mTestRule.getActivity().getString(R.string.mpsdk_text_to_mercado_pago));
        Spanned callForAuthorizeAmount = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),mPayment.getCurrencyId(), callForAuthorizeTitle.toString(), true, true);

        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(callForAuthorizeAmount.toString())));

        //Correct subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Button isDisplayed
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //Correct PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //Button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethod)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkExitCallForAuthorize)).check(matches(isDisplayed()));
    }

    @Test
    public void paymentRejectedForHighRisk(){
        mPayment = getRejectedPayment();

        mPaymentMethod = new PaymentMethod();
        mPaymentMethod.setId("master");
        mPaymentMethod.setName("Master");

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", mPaymentMethod);
        validStartIntent.putExtra("payment", mPayment);

        mTestRule.launchActivity(validStartIntent);

        //Correct title
        onView(withId(R.id.mpsdkRejectionTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_rejection_high_risk))));

        //Correct subtitle
        onView(withId(R.id.mpsdkRejectionSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_subtitle_rejection_high_risk))));

        //Button isDisplayed
        onView(withId(R.id.mpsdkSelectOtherPaymentMethodByRejection)).check(matches(isDisplayed()));

        //Button isDisplayed
        onView(withId(R.id.mpsdkExitRejection)).check(matches(isDisplayed()));
    }

    public Payment getApprovedPayment() {
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

    public Payment getPendingPayment() {
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
        payment.setStatus("in_process");
        payment.setStatusDetail("pending_review_manual");
        payment.setCard(card);
        payment.setId(123456789L);
        payment.setPaymentMethodId("master");
        payment.setInstallments(6);
        payment.setTransactionDetails(transactionDetails);
        payment.setCurrencyId("ARS");

        return payment;
    }

    public Payment getCallForAuthorizePayment(){
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

    public Payment getRejectedPayment(){
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
}
