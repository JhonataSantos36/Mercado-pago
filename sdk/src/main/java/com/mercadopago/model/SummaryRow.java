package com.mercadopago.model;

import java.math.BigDecimal;

/**
 * Created by mromar on 9/11/17.
 */

public class SummaryRow {

    private String title;
    private String summaryItemType;
    private BigDecimal amount;
    private String currencyId;
    private Integer rowTextColor;

    public SummaryRow(String title, BigDecimal amount, String currencyId, String summaryItemType, Integer rowTextColor) {
        this.title = title;
        this.amount = amount;
        this.currencyId = currencyId;
        this.summaryItemType = summaryItemType;
        this.rowTextColor = rowTextColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getSummaryItemType() {
        return summaryItemType;
    }

    public void setSummaryItemType(String summaryItemType) {
        this.summaryItemType = summaryItemType;
    }

    public Integer getRowTextColor() {
        return rowTextColor;
    }

    public void setRowTextColor(Integer colorTextRow) {
        rowTextColor = colorTextRow;
    }
}
