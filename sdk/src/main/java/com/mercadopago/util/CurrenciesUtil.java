package com.mercadopago.util;

import android.text.Html;
import android.text.Spanned;

import com.mercadopago.model.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrenciesUtil {

    public static final String CURRENCY_ARGENTINA = "ARS";
    public static final String CURRENCY_BRAZIL = "BRL";
    public static final String CURRENCY_CHILE = "CLP";
    public static final String CURRENCY_COLOMBIA = "COP";
    public static final String CURRENCY_MEXICO = "MXN";
    public static final String CURRENCY_VENEZUELA = "VEF";
    public static final String CURRENCY_USA = "USD";
    public static final String CURRENCY_PERU = "PEN";
    public static final String CURRENCY_URUGUAY = "UYU";

    protected CurrenciesUtil() {

    }

    public static Map<String, Currency> currenciesList = new HashMap<String, Currency>() {{
        put(CURRENCY_ARGENTINA, new Currency(CURRENCY_ARGENTINA, "Peso argentino", "$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_BRAZIL, new Currency(CURRENCY_BRAZIL, "Real", "R$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_CHILE, new Currency(CURRENCY_CHILE, "Peso chileno", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_COLOMBIA, new Currency(CURRENCY_COLOMBIA, "Peso colombiano", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_MEXICO, new Currency(CURRENCY_MEXICO, "Peso mexicano", "$", 2, ".".charAt(0), ",".charAt(0)));
        put(CURRENCY_VENEZUELA, new Currency(CURRENCY_VENEZUELA, "Bolivar fuerte", "BsF", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_USA, new Currency(CURRENCY_USA, "Dolar americano", "US$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_PERU, new Currency(CURRENCY_PERU, "Soles", "S/.", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_URUGUAY, new Currency(CURRENCY_URUGUAY, "Peso Uruguayo", "$", 2, ",".charAt(0), ".".charAt(0)));
    }};

    public static String formatNumber(BigDecimal amount, String currencyId) {
        // Get currency configuration
        Currency currency = currenciesList.get(currencyId);

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
            builder.append(" ");
            builder.append(df.format(amount));
            return builder.toString();

        } else {
            return null;
        }
    }

    public static Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, currencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, currencyId, originalNumber, false, true);
        return amountText;
    }

    public static Spanned formatNumber(BigDecimal amount, String currencyId, boolean symbolUp, boolean decimalsUp) {

        // Get currency configuration
        Currency currency = currenciesList.get(currencyId);

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
            String formatedAmount = df.format(amount);
            String spannedString = getSpannedString(currency, formatedAmount, symbolUp, decimalsUp);

            return Html.fromHtml(spannedString);
        } else {
            return null;
        }
    }

    public static String getDecimals(String currencyId, String amount) {
        Currency currency = currenciesList.get(currencyId);
        int decimalDivisionIndex = amount.indexOf(currency.getDecimalSeparator());
        String decimals = null;
        if (decimalDivisionIndex != -1) {
            decimals = amount.substring(decimalDivisionIndex + 1, amount.length());
        }
        return decimals;
    }

    public static String getWholeNumber(String currencyId, String amount) {
        Currency currency = currenciesList.get(currencyId);
        int decimalDivisionIndex = amount.indexOf(currency.getDecimalSeparator());
        String wholeNumber;
        if (decimalDivisionIndex == -1) {
            wholeNumber = amount;
        } else {
            wholeNumber = amount.substring(0, decimalDivisionIndex);
        }
        return wholeNumber;
    }

    private static String getSpannedString(Currency currency, String formattedAmount, boolean symbolUp, boolean decimalsUp) {

        if (formattedAmount.contains(currency.getSymbol())) {
            formattedAmount = formattedAmount.replace(currency.getSymbol(), "");
        }

        String wholeNumber = getWholeNumber(currency.getId(), formattedAmount);
        String decimals = getDecimals(currency.getId(), formattedAmount);

        StringBuilder htmlFormatBuilder = new StringBuilder();

        if (symbolUp) {
            htmlFormatBuilder.append("<sup><small><small>" + currency.getSymbol() + " </small></small></sup>");
        } else {
            htmlFormatBuilder.append(currency.getSymbol());
        }
        htmlFormatBuilder.append(wholeNumber);

        if (decimals != null) {
            if (decimalsUp) {
                htmlFormatBuilder.append("<sup><small><small> " + decimals + "</small></small></sup>");
            } else {
                htmlFormatBuilder.append(decimals);
            }
        }
        return htmlFormatBuilder.toString();
    }

    public static Spanned formatCurrencyInText(BigDecimal amount, String currencyId, String originalText,
                                               boolean symbolUp, boolean decimalsUp) {
        return formatCurrencyInText("", amount, currencyId, originalText, symbolUp, decimalsUp);
    }


    public static Spanned formatCurrencyInText(String amountPrefix, BigDecimal amount, String currencyId, String originalText,
                                               boolean symbolUp, boolean decimalsUp) {
        Spanned spannedAmount;
        Currency currency = currenciesList.get(currencyId);
        String formattedAmount = formatNumber(amount, currencyId);
        if (formattedAmount != null) {
            String spannedString = getSpannedString(currency, formattedAmount, symbolUp, decimalsUp);

            String formattedText = originalText;
            if (originalText.contains(formattedAmount)) {
                formattedText = originalText.replace(formattedAmount, amountPrefix + spannedString);
            }
            spannedAmount = Html.fromHtml(formattedText);
        } else {
            spannedAmount = Html.fromHtml(originalText);
        }
        return spannedAmount;
    }


    public static boolean isValidCurrency(String currencyId) {
        return !TextUtil.isEmpty(currencyId) && currenciesList.containsKey(currencyId);
    }

    public static List<Currency> getAllCurrencies() {
        return new ArrayList<>(currenciesList.values());
    }

    public static Currency getCurrency(String currencyKey) {
        return currenciesList.get(currencyKey);
    }
}
