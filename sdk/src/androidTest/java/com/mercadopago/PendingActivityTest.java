package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.Card;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
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
 * Created by mromar on 7/7/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PendingActivityTest {

    @Rule
    public ActivityTestRule<PendingActivity> mTestRule = new ActivityTestRule<>(PendingActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;

    @Before
    public void validStartParameters() {
        mPayment = getPayment();
        mMerchantPublicKey = "1234";
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
        payment.setStatus("in_process");
        payment.setStatusDetail("pending_contingency");
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
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPayment(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
    }

    private void createIntentWithNullPublicKey(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    @Test
    public void showPendingLayoutWhenPaymentStatusDetailIsNull(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(null);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkPendingTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))));
        onView(withId(R.id.mpsdkPendingSubtitle)).check(matches(not(isDisplayed())));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));
    }

    @Test
    public void showPendingLayoutWhenPaymentStatusDetailIsEmpty(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail("");

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Title and subtitle
        onView(withId(R.id.mpsdkPendingTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_title_pending))));
        onView(withId(R.id.mpsdkPendingSubtitle)).check(matches(not(isDisplayed())));

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));
    }

    @Test
    public void showPendingLayoutWhenPaymentIsPendingForReviewManual(){
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

    @Test
    public void showPendingLayoutWhenPaymentIsPendingForContingency(){
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
    public void finishPendingLayoutWhenClickOnExitPending(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Exit button isDisplayed
        onView(withId(R.id.mpsdkExitPending)).check(matches(isDisplayed()));

        //Click on exit button
        onView(withId(R.id.mpsdkExitPending)).perform(click());

        //Congrats finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showErrorLayoutWhenStartPendingActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorLayoutWhenStartPendingActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(validStartIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void noFinishPendingLayoutWhenClickOnBackButton(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test (expected = NoActivityResumedException.class)
    public void finishPendingLayoutWhenClickOnBackButtonTwoTimes(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();

        assertTrue(mTestRule.getActivity().isFinishing());
    }
}
