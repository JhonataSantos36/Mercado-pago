package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.Tokens;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;
import com.mercadopago.paymentresult.props.PaymentMethodProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mromar on 11/30/2017.
 */

public class PaymentMethodTest {

    private ActionDispatcher dispatcher;
    private PaymentMethodProvider provider;

    private static final String DISCLAIMER_TEXT = "WWW.MERCADOPAGO.COM";
    private static final String STATE_ACCOUNT_TEXT = "En tu estado de cuenta verás el cargo como\n";

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        provider = mock(PaymentMethodProvider.class);

        when(provider.getDisclaimer(DISCLAIMER_TEXT)).thenReturn(STATE_ACCOUNT_TEXT + DISCLAIMER_TEXT);
    }

    @Test
    public void getDescriptionWhenComponentHasPaymentMethod() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        String cardPaymentMethod = component.props.paymentMethod.getName() + " " + provider.getLastDigitsText() + " " + component.props.token.getLastFourDigits();

        Assert.assertTrue(component.getDescription().equals(cardPaymentMethod));
    }

    @Test
    public void getEmptyDescriptionWhenComponentHasNotPaymentMethod() {
        final PaymentMethod paymentMethod = null;
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        Assert.assertTrue(component.getDescription().equals(""));
    }

    @Test
    public void getEmptyDescriptionWhenComponentHasNotToken() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = null;
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        Assert.assertTrue(component.getDescription().equals("Visa"));
    }

    @Test
    public void getDetailWhenComponentHasIssuer() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        Assert.assertTrue(component.getDetail().equals(issuer.getName()));
    }

    @Test
    public void getEmptyDetailWhenComponentHasNotIssuer() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = null;
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        Assert.assertTrue(component.getDetail().equals(""));
    }

    @Test
    public void getDisclaimerWhenComponentHasDisclaimer() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, DISCLAIMER_TEXT, amountFormatter);

        Assert.assertTrue(component.getDisclaimer().equals(STATE_ACCOUNT_TEXT + DISCLAIMER_TEXT));
    }

    @Test
    public void getNullDisclaimerWhenComponentHasNullDisclaimer() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));
        final String disclaimer = null;

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, disclaimer, amountFormatter);

        Assert.assertNull(component.getDisclaimer());
    }

    @Test
    public void getEmptyDisclaimerWhenComponentHasEmptyDisclaimer() {
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final Token token = Tokens.getToken();
        final Issuer issuer = Issuers.getIssuerMLA();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));
        final String disclaimer = "";

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                getPaymentMethodComponent(paymentMethod, payerCost, token, issuer, disclaimer, amountFormatter);

        Assert.assertNull(component.getDisclaimer());
    }

    private com.mercadopago.paymentresult.components.PaymentMethod getPaymentMethodComponent(PaymentMethod paymentMethod, PayerCost payerCost, Token token, Issuer issuer, String disclaimer, BodyAmountFormatter amountFormatter) {
        final PaymentMethodProps props = new PaymentMethodProps.Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setToken(token)
                .setIssuer(issuer)
                .setDisclaimer(disclaimer)
                .setAmountFormatter(amountFormatter)
                .build();

        final com.mercadopago.paymentresult.components.PaymentMethod component =
                new com.mercadopago.paymentresult.components.PaymentMethod(props, dispatcher, provider);

        return component;
    }
}
