package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.TotalAmount;
import com.mercadopago.lite.util.CurrenciesUtil;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.model.PayerCost;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.mockito.Mockito.mock;

public class TotalAmountTest {

    private static final String CURRENCY_ID = "ARS";
    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void getAmountTitleWhenComponentHasPayerCostWithInstallments() {
        final TotalAmount component = getTotalAmountComponent(PayerCosts.getPayerCost());

        String expected = String.format(Locale.getDefault(),
                "%dx %s",
                component.props.payerCost.getInstallments(),
                CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(CURRENCY_ID,
                        component.props.payerCost.getInstallmentAmount()));

        Assert.assertEquals(expected, component.getAmountTitle());
    }

    @Test
    public void getAmountTitleWhenComponentHasPayerCostWithoutInstallments() {
        final PayerCost payerCost = PayerCosts.getPayerCostWithoutInstallments();
        final TotalAmount component = getTotalAmountComponent(payerCost);
        String expected = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(CURRENCY_ID, component.props.amount);
        Assert.assertEquals(expected, component.getAmountTitle());
    }

    @Test
    public void getEmptyAmountTitleWhenComponentHasNotPayerCost() {
        final PayerCost payerCost = null;
        final TotalAmount component = getTotalAmountComponent(payerCost);
        Assert.assertEquals("", component.getAmountDetail());
    }

    @Test
    public void getAmountDetailWhenComponentHasPayerCost() {

        final PayerCost payerCost = PayerCosts.getPayerCost();

        final TotalAmount component = getTotalAmountComponent(payerCost);

        String expected = String.format(Locale.getDefault(),
                "(%s)",
                CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(CURRENCY_ID, component.props.payerCost.getTotalAmount()));

        Assert.assertEquals(expected, component.getAmountDetail());
    }

    @Test
    public void getEmptyAmountDetailWhenComponentHasNotPayerCost() {
        final PayerCost payerCost = null;

        final TotalAmount component = getTotalAmountComponent(payerCost);

        Assert.assertTrue(component.getAmountDetail().equals(""));
    }

    private TotalAmount getTotalAmountComponent(PayerCost payerCost) {
        final TotalAmount.TotalAmountProps props = new TotalAmount.TotalAmountProps(CURRENCY_ID, new BigDecimal(1000), payerCost, null);
        return new TotalAmount(props);
    }
}
