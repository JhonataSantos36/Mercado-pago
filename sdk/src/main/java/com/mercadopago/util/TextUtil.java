package com.mercadopago.util;

/**
 * Created by mreverter on 1/31/17.
 */

public class TextUtil {
    private TextUtil() {}

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0;
    }
}
