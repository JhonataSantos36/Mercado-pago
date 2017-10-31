package com.mercadopago.lite.util;

import com.mercadopago.lite.model.Currency;

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
    public static final String CURRENCY_PERU = "PEN";
    public static final String CURRENCY_URUGUAY = "UYU";

    protected CurrenciesUtil() {
    }

    private static Map<String, Currency> currenciesList = new HashMap<String, Currency>() {{
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
}
