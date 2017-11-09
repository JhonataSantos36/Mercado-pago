package com.mercadopago.lite.model;

import java.math.BigDecimal;

/**
 * Created by mromar on 10/23/17.
 */

public class AmountInfo {

    private BigDecimal amount;
    private Currency currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
