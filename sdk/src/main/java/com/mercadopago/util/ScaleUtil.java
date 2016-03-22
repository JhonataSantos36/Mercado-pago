package com.mercadopago.util;

import android.content.Context;

/**
 * Created by mreverter on 22/3/16.
 */
public class ScaleUtil {
    public static int getPxFromDp(int dpValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (dpValue*scale + 0.5f);
        return dpAsPixels;
    }
}
