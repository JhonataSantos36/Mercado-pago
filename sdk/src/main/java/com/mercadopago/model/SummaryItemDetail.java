package com.mercadopago.model;

import java.math.BigDecimal;

/**
 * Created by mromar on 9/6/17.
 */

public class SummaryItemDetail {

    private String name;
    private BigDecimal amount;

    SummaryItemDetail(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
