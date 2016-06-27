package com.mercadopago.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.util.ColorsUtil;

/**
 * Created by mreverter on 2/6/16.
 */
public class DecorationPreference{

    private Integer baseColor;
    private boolean darkFontEnabled;

    public void setBaseColor(int color) {
        this.baseColor = color;
    }

    public boolean hasColors() {
        return baseColor != null;
    }

    public Integer getBaseColor() {
        return baseColor;
    }

    public void enableDarkFont() {
        this.darkFontEnabled = true;
    }

    public boolean isDarkFontEnabled() {
        return darkFontEnabled;
    }

    public int getLighterColor() {
        return ColorsUtil.lighter(baseColor);
    }

    public int getDarkFontColor(Context context) {
        return ContextCompat.getColor(context, R.color.mpsdk_dark_font_color);
    }
}
