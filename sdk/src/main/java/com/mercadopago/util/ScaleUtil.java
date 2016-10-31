package com.mercadopago.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by mreverter on 22/3/16.
 */
public class ScaleUtil {

    public static int getPxFromDp(int dpValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (dpValue * scale + 0.5f);
        return dpAsPixels;
    }

    //falta landscape? pensar para android tv

    public static boolean isLowRes(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        boolean dpiLowRes = metrics.densityDpi < DisplayMetrics.DENSITY_HIGH;
        boolean heightLowRes = metrics.heightPixels < 800;
        return dpiLowRes || heightLowRes;
    }
}
