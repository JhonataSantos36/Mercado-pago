package com.mercadopago.review_and_confirm.components.payment_method;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.model.Summary;
import com.mercadopago.review_and_confirm.SummaryProviderImpl;
import com.mercadopago.review_and_confirm.components.FullSummary;
import com.mercadopago.review_and_confirm.models.SummaryModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

/**
 * Created by mromar on 3/12/18.
 */

//@RunWith(MockitoJUnitRunner.class)
public class FullSummaryTest {

    private final static String CURRENCY_ID = "ARS";
    private final static Long DISCOUNT_ID = 77L;
    private final static BigDecimal DISCOUNT_COUPON_AMOUNT = new BigDecimal(20);
    private final static BigDecimal DISCOUNT_PERCENT_OFF = new BigDecimal(20);

    private final static BigDecimal TOTAL_AMOUNT = new BigDecimal(1000);
    private final static BigDecimal SHIPPING_AMOUNT = new BigDecimal(100);
    private final static BigDecimal ARREAR_AMOUNT = new BigDecimal(50);
    private final static BigDecimal TAXES_AMOUNT = new BigDecimal(10);
    private final static BigDecimal DISCOUNT_AMOUNT = new BigDecimal(20);
    private final static BigDecimal CHARGES_AMOUNT = new BigDecimal(20);
    private final static String DISCLAIMER = "No incluye intereses bancarios";
    private final static int DISCLAIMER_COLOR = 0;

    private final static Site SITE = new Site("MLA", CURRENCY_ID);

    private final static String PAYMENT_TYPE_ID_CARD = PaymentTypes.CREDIT_CARD;
/*
    @Mock
    SummaryProviderImpl provider;

    /*
    @Test
    public void whenHasDiscountAndIsCardPaymentMethodThenGetTotalAmountWithDiscount() throws Exception {
        SummaryModel model = new SummaryModel(TOTAL_AMOUNT, getCardPaymentMethod(), SITE, getPayerCostWithDiscount(), getDiscount(), null);

        FullSummary component = new FullSummary(model, provider);

        Assert.assertEquals(component.getTotalAmount(), getTotalAmountWithDiscount());
    }

    @Test
    public void whenHasInstallmentsThenGetPayerCostTotalAmount() throws Exception {
        SummaryModel model = new SummaryModel(TOTAL_AMOUNT, getCardPaymentMethod(), SITE, getPayerCostWithInstallments(), getDiscount(), null);

        FullSummary component = new FullSummary(model, provider);

        Assert.assertEquals(component.getTotalAmount(), TOTAL_AMOUNT);
    }

    /*
    @Test
    public void whenReviewScreenPreferenceIsNotNullThenGetSummary() throws Exception {
        ReviewScreenPreference reviewScreenPreference = new ReviewScreenPreference.Builder()
                .addSummaryProductDetail(TOTAL_AMOUNT)
                .addSummaryShippingDetail(SHIPPING_AMOUNT)
                .addSummaryArrearsDetail(ARREAR_AMOUNT)
                .addSummaryTaxesDetail(TAXES_AMOUNT)
                .addSummaryDiscountDetail(DISCOUNT_AMOUNT)
                .addSummaryChargeDetail(CHARGES_AMOUNT)
                .setDisclaimer(DISCLAIMER)
                .setDisclaimerTextColor("#000000")
                .build();

        BigDecimal totalAmount = new BigDecimal(0);
        totalAmount = totalAmount.add(TOTAL_AMOUNT);
        totalAmount = totalAmount.add(CHARGES_AMOUNT);
        totalAmount = totalAmount.add(TAXES_AMOUNT);
        totalAmount = totalAmount.add(SHIPPING_AMOUNT);
        totalAmount = totalAmount.add(ARREAR_AMOUNT);
        totalAmount = totalAmount.subtract(DISCOUNT_AMOUNT);

        CheckoutStore.getInstance().setReviewScreenPreference(reviewScreenPreference);

        SummaryModel model = new SummaryModel(totalAmount, getCardPaymentMethod(), SITE, getPayerCostWithInstallments(), null, null);

        FullSummary component = new FullSummary(model, provider);

        Assert.assertEquals(component.getSummary().getSummaryDetails().get(0).getSummaryItemDetails().get(0).getAmount(), getSummary().getSummaryDetails().get(0).getSummaryItemDetails().get(0).getAmount());
        //Assert.assertEquals(component.getSummary().getSummaryDetails().get(0).getSummaryItemDetails().get(0).getAmount(), getSummary().getSummaryDetails().get(1).getSummaryItemDetails().get(0).getAmount());
    }
    */

    private PaymentMethod getCardPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PAYMENT_TYPE_ID_CARD);

        return paymentMethod;
    }

    private PayerCost getPayerCostWithDiscount() {
        PayerCost payerCost = new PayerCost();

        payerCost.setTotalAmount(getTotalAmountWithDiscount());
        payerCost.setInstallments(1);
        payerCost.setInstallmentRate(new BigDecimal(0));

        return payerCost;
    }

    private PayerCost getPayerCostWithInstallments() {
        PayerCost payerCost = new PayerCost();

        payerCost.setTotalAmount(TOTAL_AMOUNT);
        payerCost.setInstallments(3);
        payerCost.setInstallmentRate(new BigDecimal(0));

        return payerCost;
    }

    private Discount getDiscount() {
        Discount discount = new Discount();

        discount.setId(DISCOUNT_ID);
        discount.setCouponAmount(DISCOUNT_COUPON_AMOUNT);
        discount.setCurrencyId(CURRENCY_ID);
        discount.setPercentOff(DISCOUNT_PERCENT_OFF);

        return discount;
    }

    private BigDecimal getTotalAmountWithDiscount() {
        return TOTAL_AMOUNT.subtract(getDiscount().getCouponAmount());
    }

    private Summary getSummary() {
        Summary.Builder summaryBuilder = new com.mercadopago.model.Summary.Builder();

        return summaryBuilder.addSummaryProductDetail(TOTAL_AMOUNT, null, null)
                .addSummaryShippingDetail(SHIPPING_AMOUNT, null, null)
                .addSummaryArrearsDetail(ARREAR_AMOUNT, null, null)
                .addSummaryTaxesDetail(TAXES_AMOUNT, null, null)
                .addSummaryDiscountDetail(DISCOUNT_AMOUNT, null, null)
                .addSummaryChargeDetail(CHARGES_AMOUNT, null, null)
                .setDisclaimerText(DISCLAIMER)
                .setDisclaimerColor(DISCLAIMER_COLOR)
                .build();
    }
}
