package com.mercadopago.paymentresult.formatter;

import com.mercadopago.lite.util.CurrenciesUtil;

import java.math.BigDecimal;

public class BodyAmountFormatter extends AmountFormat {

    public BodyAmountFormatter(String currencyId, BigDecimal amount) {
        super(currencyId, amount);
    }

    public String formatNumber(BigDecimal amount) {
        return CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currencyId, amount);
    }
}
