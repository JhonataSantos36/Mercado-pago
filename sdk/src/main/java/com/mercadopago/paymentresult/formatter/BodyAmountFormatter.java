package com.mercadopago.paymentresult.formatter;

import com.mercadopago.model.Currency;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by mromar on 11/30/17.
 */

public class BodyAmountFormatter extends AmountFormat {

    public BodyAmountFormatter(String currencyId, BigDecimal amount) {
        super(currencyId, amount);
    }

    public String formatNumber(BigDecimal amount, String currencyId) {
        String formattedAmount = formatNumber( amount, currencyId, true);
        String decimals = getDecimals(formattedAmount);
        String wholeNumber = getWholeNumber(formattedAmount);
        String totalAmount;

        if (decimals != null && decimals.equals("00")) {
            totalAmount = wholeNumber;
        } else {
            totalAmount = formattedAmount;
        }

        return totalAmount;
    }

    public String formatNumber(BigDecimal amount, String currencyId, boolean hasSpace) {
        // Get currency configuration
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);

        if (currency != null) {

            // Set formatters
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator(currency.getDecimalSeparator());
            dfs.setGroupingSeparator(currency.getThousandsSeparator());
            DecimalFormat df = new DecimalFormat();
            df.setDecimalFormatSymbols(dfs);
            df.setMinimumFractionDigits(currency.getDecimalPlaces());
            df.setMaximumFractionDigits(currency.getDecimalPlaces());

            // return formatted string
            StringBuilder builder = new StringBuilder();
            builder.append(currency.getSymbol());
            if (hasSpace) {
                builder.append(" ");
            }
            builder.append(df.format(amount));
            return builder.toString();

        } else {
            return null;
        }
    }
}
