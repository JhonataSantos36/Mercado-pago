package com.mercadopago.util;

import android.text.Html;
import android.text.Spanned;

import com.mercadopago.model.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class CurrenciesUtil {

    public static final String CURRENCY_ARGENTINA = "ARS";
    public static final String CURRENCY_BRAZIL = "BRL";
    public static final String CURRENCY_CHILE = "CLP";
    public static final String CURRENCY_COLOMBIA = "COP";
    public static final String CURRENCY_MEXICO = "MXN";
    public static final String CURRENCY_VENEZUELA = "VEF";
    public static final String CURRENCY_USA = "USD";


    private static Map<String, Currency> currenciesList = new HashMap<String, Currency>(){{
        put(CURRENCY_ARGENTINA, new Currency(CURRENCY_ARGENTINA, "Peso argentino", "$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_BRAZIL, new Currency(CURRENCY_BRAZIL, "Real", "R$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_CHILE, new Currency(CURRENCY_CHILE, "Peso chileno", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_COLOMBIA, new Currency(CURRENCY_COLOMBIA, "Peso colombiano", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_MEXICO, new Currency(CURRENCY_MEXICO, "Peso mexicano", "$", 2, ".".charAt(0), ",".charAt(0)));
        put(CURRENCY_VENEZUELA, new Currency(CURRENCY_VENEZUELA, "Bolivar fuerte", "BsF", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_USA, new Currency(CURRENCY_USA, "Dolar americano", "US$", 2, ",".charAt(0), ".".charAt(0)));
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
            return currency.getSymbol() + " " + df.format(amount);

        } else {
            return null;
        }
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

            int decimalDivisionIndex = formatedAmount.indexOf(currency.getDecimalSeparator());
            String wholeNumber = formatedAmount.substring(0, decimalDivisionIndex);
            String decimals = formatedAmount.substring(decimalDivisionIndex+1, formatedAmount.length());

            StringBuilder htmlFormatBuilder = new StringBuilder();
            if(symbolUp) {
                htmlFormatBuilder.append("<sup><small>" + currency.getSymbol() + "</small></sup>");
            } else {
                htmlFormatBuilder.append(currency.getSymbol());
            }
            htmlFormatBuilder.append(wholeNumber);

            if(decimalsUp) {
                htmlFormatBuilder.append("<sup><small>" + decimals + "</small></sup>");
            } else {
                htmlFormatBuilder.append(decimals);
            }
            return Html.fromHtml(htmlFormatBuilder.toString());
        } else {
            return null;
        }
    }

    public static Spanned formatCurrencyInText(String originalTitle) {
        //TODO formatear como pide UX cuando desde el servicio venga formateado, crear amount formateado, reemplazar en
        //TODO el título con los tags html
        return Html.fromHtml(originalTitle);
    }
}
