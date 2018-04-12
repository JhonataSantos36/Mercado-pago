package com.mercadopago.model;

import java.math.BigDecimal;

public class FeeDetail {

    private static final String FINANCING_FEE = "financing_fee";

    private BigDecimal amount;
    private String feePayer;
    private String type;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFeePayer() {
        return feePayer;
    }

    public void setFeePayer(String feePayer) {
        this.feePayer = feePayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFinancialFree() {
        return FINANCING_FEE.equals(type);
    }
}
