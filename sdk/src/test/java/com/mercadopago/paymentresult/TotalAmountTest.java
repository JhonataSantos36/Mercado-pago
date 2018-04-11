package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.model.PayerCost;
import com.mercadopago.paymentresult.components.TotalAmount;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;
import com.mercadopago.paymentresult.props.TotalAmountProps;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.mockito.Mockito.mock;

/**
 * Created by mromar on 11/30/2017.
 */

public class TotalAmountTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void getAmountTitleWhenComponentHasPayerCostWithInstallments() {
        String amountTitle;
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));
        final TotalAmount component = getTotalAmountComponent(payerCost, amountFormatter);

        amountTitle = String.format(Locale.getDefault(),
                "%dx %s",
                component.props.payerCost.getInstallments(),
                component.props.amountFormatter.formatNumber(component.props.payerCost.getInstallmentAmount()));
        Assert.assertTrue(component.getAmountTitle().equals(amountTitle));
    }

    @Test
    public void getAmountTitleWhenComponentHasPayerCostWithoutInstallments() {
        String amountTitle;
        final PayerCost payerCost = PayerCosts.getPayerCostWithoutInstallments();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));
        final TotalAmount component = getTotalAmountComponent(payerCost, amountFormatter);

        amountTitle = component.props.amountFormatter.formatNumber(component.props.amountFormatter.getAmount());
        Assert.assertTrue(component.getAmountTitle().equals(amountTitle));
    }

    @Test
    public void getEmptyAmountTitleWhenComponentHasNotPayerCost() {
        final PayerCost payerCost = null;
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));
        final TotalAmount component = getTotalAmountComponent(payerCost, amountFormatter);

        Assert.assertTrue(component.getAmountDetail().equals(""));
    }

    @Test
    public void getAmountDetailWhenComponentHasPayerCost() {
        String amountDetail;
        final PayerCost payerCost = PayerCosts.getPayerCost();
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final TotalAmount component =
                getTotalAmountComponent(payerCost, amountFormatter);

        amountDetail = String.format(Locale.getDefault(),
                "(%s)",
                component.props.amountFormatter.formatNumber(component.props.payerCost.getTotalAmount()));

        Assert.assertTrue(component.getAmountDetail().equals(amountDetail));
    }

    @Test
    public void getEmptyAmountDetailWhenComponentHasNotPayerCost() {
        final PayerCost payerCost = null;
        final BodyAmountFormatter amountFormatter = new BodyAmountFormatter("ARS", new BigDecimal(1000));

        final TotalAmount component =
                getTotalAmountComponent(payerCost, amountFormatter);

        Assert.assertTrue(component.getAmountDetail().equals(""));
    }

    private TotalAmount getTotalAmountComponent(PayerCost payerCost, BodyAmountFormatter amountFormatter) {
        final TotalAmountProps props = new TotalAmountProps.Builder()
                .setPayerCost(payerCost)
                .setAmountFormatter(amountFormatter)
                .build();

        return new TotalAmount(props, dispatcher);
    }
}
