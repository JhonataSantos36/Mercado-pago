package com.mercadopago.util;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.lite.model.PaymentMethod;

/**
 * Created by marlanti on 7/14/17.
 */

public class MPCardUIUtils {

    public static final int NEUTRAL_CARD_COLOR = R.color.mpsdk_white;
    public static final int FULL_TEXT_VIEW_COLOR = R.color.mpsdk_base_text_alpha;
    public static final String NEUTRAL_CARD_COLOR_NAME = "mpsdk_white";
    public static final String FULL_TEXT_VIEW_COLOR_NAME = "mpsdk_base_text_alpha";

    public static int getCardColor(PaymentMethod paymentMethod, Context context) {
        String colorName = "mpsdk_" + paymentMethod.getId().toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(NEUTRAL_CARD_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }

    public static int getCardFontColor(PaymentMethod paymentMethod, Context context) {
        if (paymentMethod == null) {
            return FULL_TEXT_VIEW_COLOR;
        }
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        int color = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (color == 0) {
            color = context.getResources().getIdentifier(FULL_TEXT_VIEW_COLOR_NAME, "color", context.getPackageName());
        }
        return color;
    }
}
