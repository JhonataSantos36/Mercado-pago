package com.mercadopago.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;

/**
 * Created by mreverter on 2/6/16.
 */
public class ColorsUtil {

    public static int darker(int color) {

        double factor = 0.75;

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static int lighter(int color) {

        double factor = 0.25;

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r + ((255 - r) * factor)), 0),
                Math.max((int) (g + ((255 - g) * factor)), 0),
                Math.max((int) (b + ((255 - b) * factor)), 0));
    }

    @TargetApi(value = 21)
    public static void tintStatusBar(Activity activity, Integer color) {
        activity.getWindow().setStatusBarColor(color);
    }
}