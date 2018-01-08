package com.mercadopago.paymentresult;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ChangePaymentMethodAction;
import com.mercadopago.components.NextAction;
import com.mercadopago.components.RecoverPaymentAction;
import com.mercadopago.mocks.PaymentResults;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.components.Footer;
import com.mercadopago.paymentresult.components.FooterContainer;
import com.mercadopago.preferences.PaymentResultScreenPreference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FooterContainerTest {

    private static final String LABEL_REVIEW_TC_INFO = "Revisar los datos de tarjeta ";
    private static final String LABEL_CHANGE_PAYMENT_METHOD = "Pagar con otro medio";
    private static final String LABEL_KEEP_SHOPPING = "Seguir comprando";
    private static final String LABEL_CANCEL_PAYMENT = "Cancelar pago";

    private static final String EXIT_TITLE = "Exit sample title";

    private ActionDispatcher dispatcher;
    private PaymentResultProvider provider;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        provider = mock(PaymentResultProvider.class);

        when(provider.getContinueShopping()).thenReturn(LABEL_KEEP_SHOPPING);
        when(provider.getRejectedBadFilledCardTitle()).thenReturn(LABEL_REVIEW_TC_INFO);
        when(provider.getChangePaymentMethodLabel()).thenReturn(LABEL_CHANGE_PAYMENT_METHOD);
        when(provider.getCancelPayment()).thenReturn(LABEL_CANCEL_PAYMENT);

        new PaymentResultScreenPreference.Builder().build();
    }

    @Test
    public void testApproved() {

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);
        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNull(footer.props.buttonAction);
        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, LABEL_KEEP_SHOPPING);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, is(instanceOf(NextAction.class)));
    }

    @Test
    public void testApprovedExitButtonTitle() {

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setExitButtonTitle(EXIT_TITLE)
                .build();

        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);

        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNull(footer.props.buttonAction);
        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, EXIT_TITLE);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, is(instanceOf(NextAction.class)));
    }

    @Test
    public void testRejectedBadFilledDatePaymentResult() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);
        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNotNull(footer.props.buttonAction);
        Assert.assertEquals(footer.props.buttonAction.label, LABEL_REVIEW_TC_INFO);
        Assert.assertNotNull(footer.props.buttonAction.action);
        assertThat(footer.props.buttonAction.action, instanceOf(RecoverPaymentAction.class));

        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, LABEL_CHANGE_PAYMENT_METHOD);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, instanceOf(ChangePaymentMethodAction.class));
    }

    @Test
    public void testRejectedCallForAuth() {

        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);
        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNotNull(footer.props.buttonAction);
        Assert.assertEquals(footer.props.buttonAction.label, LABEL_CHANGE_PAYMENT_METHOD);
        Assert.assertNotNull(footer.props.buttonAction.action);
        assertThat(footer.props.buttonAction.action, instanceOf(ChangePaymentMethodAction.class));

        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, LABEL_CANCEL_PAYMENT);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, is(instanceOf(NextAction.class)));
    }

    @Test
    public void testRejected() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);
        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNotNull(footer.props.buttonAction);
        Assert.assertEquals(footer.props.buttonAction.label, LABEL_CHANGE_PAYMENT_METHOD);
        Assert.assertNotNull(footer.props.buttonAction.action);
        assertThat(footer.props.buttonAction.action, instanceOf(ChangePaymentMethodAction.class));

        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, LABEL_CANCEL_PAYMENT);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, is(instanceOf(NextAction.class)));
    }

    @Test
    public void testRejectedDisableSecondaryExitButton() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .disableRejectedSecondaryExitButton()
                .build();
        final FooterContainer footerContainer = new FooterContainer(
                new FooterContainer.Props(paymentResult), dispatcher, provider);
        final Footer footer = footerContainer.getFooter();

        Assert.assertNotNull(footer);
        Assert.assertNull(footer.props.buttonAction);

        Assert.assertNotNull(footer.props.linkAction);
        Assert.assertEquals(footer.props.linkAction.label, LABEL_CANCEL_PAYMENT);
        Assert.assertNotNull(footer.props.linkAction.action);
        assertThat(footer.props.linkAction.action, is(instanceOf(NextAction.class)));
    }
}
