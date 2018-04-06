package com.mercadopago.lite.util;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.RelativeSizeSpan;

import com.mercadopago.lite.model.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public final class CurrenciesUtil {

    private static final String CURRENCY_ARGENTINA = "ARS";
    private static final String CURRENCY_BRAZIL = "BRL";
    private static final String CURRENCY_CHILE = "CLP";
    private static final String CURRENCY_COLOMBIA = "COP";
    private static final String CURRENCY_MEXICO = "MXN";
    private static final String CURRENCY_VENEZUELA = "VEF";
    private static final String CURRENCY_USA = "USD";
    private static final String CURRENCY_PERU = "PEN";
    private static final String CURRENCY_URUGUAY = "UYU";
    public static final String ZERO_DECIMAL = "00";

    private CurrenciesUtil() {
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

    public static String getLocalizedAmountWithCurrencySymbol(@NonNull BigDecimal amount,
                                                              @NonNull String currencyId,
                                                              boolean shouldAddSpace) {
        // Get currency configuration
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        String formattedAmount = getLocalizedAmount(amount, currency);

        // return formatted string
        StringBuilder builder = new StringBuilder();
        builder.append(currency.getSymbol());
        if (shouldAddSpace) {
            builder.append(" ");
        }
        builder.append(formattedAmount);
        return builder.toString();
    }

    private static String getLocalizedAmount(final @NonNull BigDecimal amount, final Currency currency) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(currency.getDecimalSeparator());
        dfs.setGroupingSeparator(currency.getThousandsSeparator());
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(dfs);
        df.setMinimumFractionDigits(currency.getDecimalPlaces());
        df.setMaximumFractionDigits(currency.getDecimalPlaces());
        return df.format(amount);
    }

    public static String getLocalizedAmountWithCurrencySymbol(BigDecimal amount, String currencyId) {
        return getLocalizedAmountWithCurrencySymbol(amount, currencyId, true);
    }

    public static Spanned getSpannedAmountWithCurrencySymbol(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.getSpannedString(amount, currencyId, false, true);
    }

    public static String getSymbol(@NonNull final String currencyId) {
        Currency currency = currenciesList.get(currencyId);
        if (currency == null) throw new IllegalStateException("invalid currencyId");
        return currency.getSymbol();
    }

    public static Character getDecimalSeparator(@NonNull final String currencyId) {
        return CurrenciesUtil.getCurrency(currencyId).getDecimalSeparator();
    }

    public static String getDecimals(String currencyId, BigDecimal amount) {
        Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        String localizedAmount = getLocalizedAmount(amount, currency);
        int decimalDivisionIndex = localizedAmount.indexOf(currency.getDecimalSeparator());
        String decimals = null;
        if (decimalDivisionIndex != -1) {
            decimals = localizedAmount.substring(decimalDivisionIndex + 1, localizedAmount.length());
        }
        return decimals;
    }

    public static Spanned getSpannedString(BigDecimal amount, String currencyId, boolean symbolUp, boolean decimalsUp) {
        String localizedAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currencyId, amount);
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(localizedAmount);
        if (decimalsUp && !CurrenciesUtil.hasZeroDecimals(currencyId, amount)) {
            final int fromDecimals = localizedAmount.indexOf(CurrenciesUtil.getDecimalSeparator(currencyId)) + 1;
            localizedAmount = localizedAmount.replace(String.valueOf(CurrenciesUtil.getDecimalSeparator(currencyId)), " ");
            spannableAmount = new SpannableStringBuilder(localizedAmount);
            decimalsUp(currencyId, amount, spannableAmount, fromDecimals);
        }

        if (symbolUp) {
            symbolUp(currencyId, localizedAmount, spannableAmount);
        }

        return new SpannedString(spannableAmount);
    }


    public static boolean isValidCurrency(String currencyId) {
        return !TextUtil.isEmpty(currencyId) && currenciesList.containsKey(currencyId);
    }

    public static Currency getCurrency(String currencyKey) {
        return currenciesList.get(currencyKey);
    }

    public static String getLocalizedAmountWithoutZeroDecimals(@NonNull final String currencyId,
                                                               @NonNull final BigDecimal amount) {
        String localized = getLocalizedAmountWithCurrencySymbol(amount, currencyId);
        if (hasZeroDecimals(currencyId, amount)) {
            String decimals = getDecimals(currencyId, amount);
            Character decimalSeparator = currenciesList.get(currencyId).getDecimalSeparator();
            localized = localized.replace(String.valueOf(decimalSeparator.charValue()), "");
            localized = localized.replace(decimals, "");
        }

        return localized;
    }

    public static boolean hasZeroDecimals(final String currencyId, final BigDecimal amount) {
        String decimals = getDecimals(currencyId, amount);
        return ZERO_DECIMAL.equals(decimals);
    }

    @NonNull
    public static SpannableStringBuilder getSpannableAmountWithSymbolWithoutZeroDecimals(@NonNull final String currencyId,
                                                                                         @NonNull final BigDecimal amount) {
        String localizedAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currencyId, amount);
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(localizedAmount);

        if (!CurrenciesUtil.hasZeroDecimals(currencyId, amount)) {
            int fromDecimals = localizedAmount.indexOf(CurrenciesUtil.getDecimalSeparator(currencyId));
            localizedAmount = localizedAmount.replace(String.valueOf(CurrenciesUtil.getDecimalSeparator(currencyId)), "");
            spannableAmount = new SpannableStringBuilder(localizedAmount);
            decimalsUp(currencyId, amount, spannableAmount, fromDecimals);
        }

        symbolUp(currencyId, localizedAmount, spannableAmount);


        return spannableAmount;
    }

    private static void symbolUp(final @NonNull String currencyId, final String localizedAmount, final SpannableStringBuilder spannableAmount) {
        int fromSymbolPosition = localizedAmount.indexOf(CurrenciesUtil.getSymbol(currencyId));
        int toSymbolPosition = fromSymbolPosition + CurrenciesUtil.getSymbol(currencyId).length();
        spannableAmount.setSpan(new RelativeSizeSpan(0.5f), fromSymbolPosition, toSymbolPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SpanAdjuster(0.65f), fromSymbolPosition, toSymbolPosition, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void decimalsUp(final String currencyId, final BigDecimal amount, final SpannableStringBuilder spannableAmount, final int fromDecimals) {
        int toDecimals = fromDecimals + CurrenciesUtil.getDecimals(currencyId, amount).length();
        spannableAmount.setSpan(new RelativeSizeSpan(0.5f), fromDecimals, toDecimals, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SpanAdjuster(0.7f), fromDecimals, toDecimals, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
