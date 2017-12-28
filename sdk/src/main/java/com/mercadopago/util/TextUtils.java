package com.mercadopago.util;

import android.support.annotation.Nullable;

/**
 * Created by mreverter on 1/17/17.
 */

public class TextUtils {
    public static boolean isEmpty(@Nullable final String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable final String text) {
        return !isEmpty(text);
    }
}
