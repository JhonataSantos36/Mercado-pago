package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mromar on 9/6/17.
 */

public class Summary {

    private final String disclaimer;
    private final int disclaimerColor;
    private final boolean showSubtotal;
    private final List<String> summaryDetailsOrder;
    private final HashMap<String, SummaryDetail> summaryDetails;

    public Summary(Builder builder) {
        summaryDetails = builder.summaryDetails;
        summaryDetailsOrder = getSummaryDetailsOrder(builder);
        disclaimer = builder.disclaimer;
        disclaimerColor = builder.disclaimerColor;
        showSubtotal = builder.showSubtotal;
    }

    private List<String> getSummaryDetailsOrder(Builder builder) {
        List<String> summaryDetailsOrder = builder.summaryDetailsOrder;

        if (summaryDetailsOrder == null) {
            summaryDetailsOrder = getDefaultSummaryDetailsOrder();
        }

        return summaryDetailsOrder;
    }

    private List<String> getDefaultSummaryDetailsOrder() {
        List<String> defaultSummaryDetailsOrder = new ArrayList<>();

        defaultSummaryDetailsOrder.add(SummaryItemType.PRODUCT);
        defaultSummaryDetailsOrder.add(SummaryItemType.DISCOUNT);
        defaultSummaryDetailsOrder.add(SummaryItemType.CHARGE);
        defaultSummaryDetailsOrder.add(SummaryItemType.TAXES);
        defaultSummaryDetailsOrder.add(SummaryItemType.ARREARS);
        defaultSummaryDetailsOrder.add(SummaryItemType.SHIPPING);

        return defaultSummaryDetailsOrder;
    }

    public List<SummaryDetail> getSummaryDetails() {
        List<SummaryDetail> summaryDetails = new ArrayList<>();

        for (String key : summaryDetailsOrder) {
            if (this.summaryDetails.get(key) != null) {
                summaryDetails.add(this.summaryDetails.get(key));
            }
        }
        return summaryDetails;
    }

    public String getDisclaimerText() {
        return disclaimer;
    }

    public int getDisclaimerColor() {
        return disclaimerColor;
    }

    public boolean showSubtotal() {
        return showSubtotal;
    }

    public static class Builder {
        private String disclaimer;
        private int disclaimerColor;
        private List<String> summaryDetailsOrder;
        private final HashMap<String, SummaryDetail> summaryDetails = new HashMap<>();
        private final boolean showSubtotal = false;

        public Builder addSummaryProductDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.PRODUCT, textColor);
            return this;
        }

        public Builder addSummaryDiscountDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.DISCOUNT, textColor);
            return this;
        }

        public Builder addSummaryChargeDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.CHARGE, textColor);
            return this;
        }

        public Builder addSummaryTaxesDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.TAXES, textColor);
            return this;
        }

        public Builder addSummaryShippingDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.SHIPPING, textColor);
            return this;
        }

        public Builder addSummaryArrearsDetail(BigDecimal amount, String title, Integer textColor) {
            putSummaryDetail(amount, title, SummaryItemType.ARREARS, textColor);
            return this;
        }

        public Builder setDisclaimerText(String disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        public Builder setDisclaimerColor(int disclaimerColor) {
            this.disclaimerColor = disclaimerColor;
            return this;
        }

        public Builder setSummaryDetailsOrder(List<String> summaryDetailsOrder) {
            this.summaryDetailsOrder = summaryDetailsOrder;
            return this;
        }

        private void putSummaryDetail(BigDecimal amount, String title, String summaryItemType, Integer textColor) {
            String name = "";
            SummaryItemDetail summaryItemDetail = new SummaryItemDetail(name, amount);

            if (amount != null) {
                SummaryDetail summaryDetail = new SummaryDetail(title, summaryItemType, textColor);
                summaryDetail.addAmountDetail(summaryItemDetail);

                summaryDetails.put(summaryItemType, summaryDetail);
            }
        }

        public Summary build() {
            return new Summary(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Summary){
            Summary other = (Summary) obj;
            return disclaimer.equals(other.disclaimer);
        }

        return false;
    }
}
