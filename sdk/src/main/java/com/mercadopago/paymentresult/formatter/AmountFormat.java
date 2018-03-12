package com.mercadopago.paymentresult.formatter;

import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.mercadopago.model.Currency;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by vaserber on 10/27/17.
 */

public class AmountFormat {

    protected String currencyId;
    protected BigDecimal amount;
    protected String paymentMethodName;

    public AmountFormat(final String currencyId, final BigDecimal amount) {
        this.currencyId = currencyId;
        this.amount = amount;
    }

    public AmountFormat(final String currencyId, final BigDecimal amount, final String paymentMethodName) {
        this.currencyId = currencyId;
        this.amount = amount;
        this.paymentMethodName = paymentMethodName;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public String formatNumber(final boolean hasSpace) {
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

    protected String getDecimals(final String amountText) {
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        int decimalDivisionIndex = amountText.indexOf(currency.getDecimalSeparator());
        String decimals = null;
        if (decimalDivisionIndex != -1) {
            decimals = amountText.substring(decimalDivisionIndex + 1, amountText.length());
        }
        return decimals;
    }

    protected String getWholeNumber(final String amountText) {
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        int decimalDivisionIndex = amountText.indexOf(currency.getDecimalSeparator());
        String wholeNumber;
        if (decimalDivisionIndex == -1) {
            wholeNumber = amountText;
        } else {
            wholeNumber = amountText.substring(0, decimalDivisionIndex);
        }
        return wholeNumber;
    }

    protected int getSymbolLength() {
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        return currency.getSymbol().length();
    }

    protected CharSequence insertSpannedAmountInText(final String title, final SpannableStringBuilder spannedAmount) {
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);

        String formattedAmount = formatNumber(true);
        CharSequence result = title;

        if (title.contains(formattedAmount)) {
            String formattedText = title.replace(formattedAmount, "*");
            int index = formattedText.indexOf("*");
            String firstSubstring = formattedText.substring(0, index);
            String secondSubstring = formattedText.substring(index + 1, formattedText.length());

            CharSequence auxSubstring1 = TextUtils.concat(firstSubstring, "\n");
            CharSequence auxSubstring2 = TextUtils.concat(auxSubstring1, spannedAmount);
            result = TextUtils.concat(auxSubstring2, secondSubstring);
        }

        return result;
    }

}
