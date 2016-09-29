package com.mercadopago.model;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.util.ColorsUtil;

/**
 * Created by mreverter on 2/6/16.
 */
public class DecorationPreference {

    private Integer lighterColor;
    private Integer baseColor;
    private boolean darkFontEnabled;

    public void setBaseColor(@ColorInt int color) {
        this.baseColor = color;
        this.lighterColor = ColorsUtil.lighter(baseColor);
    }

    public void setBaseColor(String hexColor) {
        this.baseColor = Color.parseColor(hexColor);
        this.lighterColor = ColorsUtil.lighter(baseColor);
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
        return lighterColor;
    }

    public int getDarkFontColor(Context context) {
        return ContextCompat.getColor(context, R.color.mpsdk_dark_font_color);
    }
}
