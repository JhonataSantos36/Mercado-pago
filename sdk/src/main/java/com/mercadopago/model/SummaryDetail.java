package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 9/6/17.
 */

public class SummaryDetail {

    private String title;
    private final String summaryItemType;
    private Integer textColor;
    private List<SummaryItemDetail> summaryItemDetails;

    SummaryDetail(String title, String summaryItemType, Integer textColor) {
        this.title = title;
        this.summaryItemType = summaryItemType;
        this.textColor = textColor;
        summaryItemDetails = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SummaryItemDetail> getSummaryItemDetails() {
        return summaryItemDetails;
    }

    public void setSummaryItemDetails(List<SummaryItemDetail> summaryItemDetails) {
        this.summaryItemDetails = summaryItemDetails;
    }

    public void addAmountDetail(SummaryItemDetail summaryItemDetail) {
        summaryItemDetails.add(summaryItemDetail);
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public String getSummaryItemType() {
        return summaryItemType;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);

        for (SummaryItemDetail summaryItemDetail : summaryItemDetails) {
            totalAmount = totalAmount.add(summaryItemDetail.getAmount());
        }

        return totalAmount;
    }
}
