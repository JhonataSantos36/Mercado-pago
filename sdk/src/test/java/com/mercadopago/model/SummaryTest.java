package com.mercadopago.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 9/14/17.
 */

public class SummaryTest {

    @Test
    public void getSummaryProductDetailWhenSetProduct() {
        BigDecimal amount = new BigDecimal(100);
        String productTitle = "Entradas de cine";
        Integer textColor = 16777215;

        Summary summary = new Summary.Builder()
                .addSummaryProductDetail(amount, productTitle, textColor)
                .build();

        List<SummaryDetail> summaryDetails = summary.getSummaryDetails();

        assertTrue(summaryDetails.get(0).getTitle().equals(productTitle));
        assertTrue(summaryDetails.get(0).getSummaryItemDetails().get(0).getAmount().equals(amount));
    }

    @Test
    public void getSummaryDetailsWhenSetAllDetails() {
        BigDecimal amount = new BigDecimal(100);
        String productTitle = "Entradas de cine";
        String testTitle = "Test Title";
        Integer textColor = 16777215;

        Summary summary = new Summary.Builder()
                .addSummaryProductDetail(amount, productTitle, textColor)
                .addSummaryDiscountDetail(amount, testTitle, textColor)
                .build();

        List<SummaryDetail> summaryDetails = summary.getSummaryDetails();

        assertTrue(summaryDetails.get(0).getTitle().equals(productTitle));
        assertTrue(summaryDetails.get(0).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(1).getTitle().equals(testTitle));
        assertTrue(summaryDetails.get(1).getSummaryItemDetails().get(0).getAmount().equals(amount));
    }

    @Test
    public void getCustomSummaryDetailOrderWhenSetCustomOrder() {
        BigDecimal amount = new BigDecimal(100);
        String productTitle = "Entradas de cine";
        String discountTitle = "Descuento";
        String chargeTitle = "Cargos";
        String arrearTitle = "Mora";
        String shippingTitle = "Envio";
        String taxesTitle = "Impuestos";


        Integer textColor = 16777215;

        Summary summary = new Summary.Builder()
                .addSummaryProductDetail(amount, productTitle, textColor)
                .addSummaryDiscountDetail(amount, discountTitle, textColor)
                .addSummaryChargeDetail(amount, chargeTitle, textColor)
                .addSummaryArrearsDetail(amount, arrearTitle, textColor)
                .addSummaryShippingDetail(amount, shippingTitle, textColor)
                .addSummaryTaxesDetail(amount, taxesTitle, textColor)
                .setSummaryDetailsOrder(getCustomSummaryDetailsOrder())
                .build();

        List<SummaryDetail> summaryDetails = summary.getSummaryDetails();

        assertTrue(summaryDetails.get(0).getTitle().equals(productTitle));
        assertTrue(summaryDetails.get(0).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(1).getTitle().equals(chargeTitle));
        assertTrue(summaryDetails.get(1).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(2).getTitle().equals(taxesTitle));
        assertTrue(summaryDetails.get(2).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(3).getTitle().equals(arrearTitle));
        assertTrue(summaryDetails.get(3).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(4).getTitle().equals(shippingTitle));
        assertTrue(summaryDetails.get(4).getSummaryItemDetails().get(0).getAmount().equals(amount));

        assertTrue(summaryDetails.get(5).getTitle().equals(discountTitle));
        assertTrue(summaryDetails.get(5).getSummaryItemDetails().get(0).getAmount().equals(amount));
    }

    private List<String> getCustomSummaryDetailsOrder() {
        List<String> defaultSummaryDetailsOrder = new ArrayList<>();

        defaultSummaryDetailsOrder.add(SummaryItemType.PRODUCT);
        defaultSummaryDetailsOrder.add(SummaryItemType.CHARGE);
        defaultSummaryDetailsOrder.add(SummaryItemType.TAX);
        defaultSummaryDetailsOrder.add(SummaryItemType.ARREAR);
        defaultSummaryDetailsOrder.add(SummaryItemType.SHIPPING);
        defaultSummaryDetailsOrder.add(SummaryItemType.DISCOUNT);

        return defaultSummaryDetailsOrder;
    }
}
