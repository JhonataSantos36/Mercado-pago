package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.PaymentResults;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.components.Receipt;
import com.mercadopago.paymentresult.props.ReceiptProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vaserber on 04/12/2017.
 */

public class ReceiptTest {

    public static final String RECEIPT_DESCRIPTION = "Número de operación 8228734";

    private ActionDispatcher dispatcher;
    private PaymentResultProvider paymentResultProvider;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        paymentResultProvider = mock(PaymentResultProvider.class);

        when(paymentResultProvider.getReceiptDescription(8228734L)).thenReturn(RECEIPT_DESCRIPTION);
    }

    @Test
    public void testReceiptPropsAreValid() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final ReceiptProps receiptProps = new ReceiptProps.Builder()
                .setReceiptId(paymentResult.getPaymentId())
                .build();
        final Receipt receipt = new Receipt(receiptProps, dispatcher, paymentResultProvider);
        Assert.assertNotNull(receipt.props.receiptId);
        Assert.assertEquals(receipt.props.receiptId, paymentResult.getPaymentId());
    }

    @Test
    public void testReceiptDescription() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final ReceiptProps receiptProps = new ReceiptProps.Builder()
                .setReceiptId(paymentResult.getPaymentId())
                .build();
        final Receipt receipt = new Receipt(receiptProps, dispatcher, paymentResultProvider);
        Assert.assertNotNull(receipt.getDescription());
        Assert.assertEquals(receipt.getDescription(), RECEIPT_DESCRIPTION);
    }
}
