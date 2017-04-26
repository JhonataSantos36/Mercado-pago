package com.mercadopago.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PayerCostTest {

    @Test
    public void substringTEARateWhenGetTea() {
        PayerCost payerCost = getPayerCostWithLabels();

        String tea = "69,73%";
        assertTrue(payerCost.getTEAPercent().equals(tea));
    }

    @Test
    public void substringCFTRateWhenGetCft() {
        PayerCost payerCost = getPayerCostWithLabels();

        String cft = "88,33%";
        assertTrue(payerCost.getCFTPercent().equals(cft));
    }

    @Test
    public void hasRatesTrueWhenPayerCostHasLabelRates() {
        PayerCost payerCost = getPayerCostWithLabels();

        assertTrue(payerCost.hasRates());
    }

    @Test
    public void hasRatesFalseWhenPayerCostHasNotLabelRates() {
        PayerCost payerCost = getPayerCostWithoutLabels();

        assertFalse(payerCost.hasRates());
    }

    @Test
    public void hasTEATrueWhenPayerCostHasTEALabel() {
        PayerCost payerCost = getPayerCostWithLabels();

        assertTrue(payerCost.hasTEA());
    }

    @Test
    public void hasCFTTrueWhenPayerCostHasCFTLabel() {
        PayerCost payerCost = getPayerCostWithLabels();

        assertTrue(payerCost.hasCFT());
    }

    @Test
    public void hasTEAFalseWhenPayerCostHasNotTEALabel() {
        PayerCost payerCost = getPayerCostWithoutLabels();

        assertFalse(payerCost.hasTEA());
    }

    @Test
    public void hasCFTFalseWhenPayerCostHasNotCFTLabel() {
        PayerCost payerCost = getPayerCostWithoutLabels();

        assertFalse(payerCost.hasCFT());
    }

    private PayerCost getPayerCostWithLabels() {
        String label = "CFT_88,33%|TEA_69,73%";
        PayerCost payerCost = new PayerCost();
        List<String> labels = new ArrayList<String>();

        payerCost.setInstallments(3);
        payerCost.setInstallmentRate(new BigDecimal(10.97));

        labels.add(label);
        payerCost.setLabels(labels);

        return payerCost;
    }

    private PayerCost getPayerCostWithoutLabels() {
        PayerCost payerCost = new PayerCost();

        payerCost.setInstallments(3);
        payerCost.setInstallmentRate(new BigDecimal(10.97));

        return payerCost;
    }
}
