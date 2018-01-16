package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.PaymentResults;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.components.BodyError;
import com.mercadopago.paymentresult.props.BodyErrorProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vaserber on 29/11/2017.
 */

public class BodyErrorTest {

    private static final String ERROR_TITLE = "error_title";
    private static final String CONTINGENCY_DESCRIPTION = "contingency_description";
    private static final String REVIEW_MANUAL_DESCRIPTION = "review_manual_description";
    private static final String CALL_FOR_AUTH_DESCRIPTION = "call_for_auth_description";
    private static final String INSUFFICIENT_AMOUNT_DESCRIPTION = "insufficient_amount_description";
    private static final String INSUFFICIENT_AMOUNT_SECOND_DESCRIPTION = "insufficient_amount_second_description";
    private static final String REJECTED_OTHER_REASON_DESCRIPTION = "rejected_other_reason_description";
    private static final String REJECTED_INSUFFICIENT_DATA = "insufficient_data_description";
    private static final String DUPLICATED_DESCRIPTION = "duplicated_description";
    private static final String CALL_FOR_AUTH_ACTION = "call_for_auth_action";
    private static final String CALL_FOR_AUTH_SECOND_TITLE = "call_for_auth_second_title";

    private ActionDispatcher dispatcher;
    private PaymentResultProvider provider;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        provider = mock(PaymentResultProvider.class);

        when(provider.getErrorTitle()).thenReturn(ERROR_TITLE);
        when(provider.getPendingContingencyBodyErrorDescription()).thenReturn(CONTINGENCY_DESCRIPTION);
        when(provider.getPendingReviewManualBodyErrorDescription()).thenReturn(REVIEW_MANUAL_DESCRIPTION);
        when(provider.getRejectedCallForAuthBodyErrorDescription()).thenReturn(CALL_FOR_AUTH_DESCRIPTION);
        when(provider.getRejectedInsufficientAmountBodyErrorDescription()).thenReturn(INSUFFICIENT_AMOUNT_DESCRIPTION);
        when(provider.getRejectedInsufficientAmountBodyErrorSecondDescription()).thenReturn(INSUFFICIENT_AMOUNT_SECOND_DESCRIPTION);
        when(provider.getRejectedOtherReasonBodyErrorDescription()).thenReturn(REJECTED_OTHER_REASON_DESCRIPTION);
        when(provider.getRejectedInsufficientDataBodyErrorDescription()).thenReturn(REJECTED_INSUFFICIENT_DATA);
        when(provider.getRejectedDuplicatedPaymentBodyErrorDescription()).thenReturn(DUPLICATED_DESCRIPTION);
        when(provider.getRejectedCallForAuthBodyActionText("Mastercard")).thenReturn(CALL_FOR_AUTH_ACTION);
        when(provider.getRejectedCallForAuthBodySecondaryTitle()).thenReturn(CALL_FOR_AUTH_SECOND_TITLE);

    }

    @Test
    public void testBodyErrorTitleForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), ERROR_TITLE);
    }

    @Test
    public void testBodyErrorTitleForInsufficientAmount() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), ERROR_TITLE);
    }

    @Test
    public void testBodyErrorTitleForRejectedOther() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), ERROR_TITLE);
    }

    @Test
    public void testBodyErrorTitleForInsufficientData() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), ERROR_TITLE);
    }

    @Test
    public void testBodyErrorTitleForDuplicatedPayment() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedDuplicatedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), "");
    }

    @Test
    public void testBodyErrorTitleForPendingContingency() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getTitle(), ERROR_TITLE);
    }

    @Test
    public void testBodyErrorTitleOnEmptyCase() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), "");
    }

    @Test
    public void testBodyErrorDescriptionForPendingContingency() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), CONTINGENCY_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionForPendingReviewManual() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessReviewManualPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), REVIEW_MANUAL_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionForRejectedCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), CALL_FOR_AUTH_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionForRejectedInsufficientAmount() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), INSUFFICIENT_AMOUNT_DESCRIPTION);
    }

    @Test
    public void testBodyErrorSecondDescriptionForRejectedInsufficientAmount() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getSecondDescription(), INSUFFICIENT_AMOUNT_SECOND_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionForRejectedOtherReason() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), REJECTED_OTHER_REASON_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionForRejectedInsufficientData() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), REJECTED_INSUFFICIENT_DATA);
    }

    @Test
    public void testBodyErrorDescriptionForRejectedDuplicatedPayment() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedDuplicatedPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), DUPLICATED_DESCRIPTION);
    }

    @Test
    public void testBodyErrorDescriptionOnEmptyCase() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getDescription(), "");
    }

    @Test
    public void testBodyErrorSecondDescriptionOnEmptyCase() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getSecondDescription(), "");
    }

    @Test
    public void testBodyErrorActionTextForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getActionText(), CALL_FOR_AUTH_ACTION);
    }

    @Test
    public void testBodyErrorHasActionForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertTrue(bodyError.hasActionForCallForAuth());
    }

    @Test
    public void testBodyErrorDoestHaveActionForOtherRejected() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertFalse(bodyError.hasActionForCallForAuth());
    }

    @Test
    public void testBodyErrorSecondaryTitleForCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final BodyError bodyError = new BodyError(getBodyErrorProps(paymentResult), dispatcher, provider);

        Assert.assertEquals(bodyError.getSecondaryTitleForCallForAuth(), CALL_FOR_AUTH_SECOND_TITLE);
    }


    private BodyErrorProps getBodyErrorProps(PaymentResult paymentResult) {
        return new BodyErrorProps.Builder()
                .setStatus(paymentResult.getPaymentStatus())
                .setStatusDetail(paymentResult.getPaymentStatusDetail())
                .setPaymentMethodName(paymentResult.getPaymentData().getPaymentMethod().getName())
                .build();
    }
}
