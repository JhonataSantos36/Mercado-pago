package com.mercadopago.preferences;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.util.ColorsUtil;

/**
 * Created by mreverter on 2/6/16.
 */
public class DecorationPreference {

    private Integer lighterColor;
    private Integer baseColor;
    private boolean darkFontEnabled;
    private String fontPath;

    private DecorationPreference(Builder builder) {
        this.lighterColor = builder.lighterColor;
        this.baseColor = builder.baseColor;
        this.darkFontEnabled = builder.darkFontEnabled;
        this.fontPath = builder.fontPath;
    }

    public boolean hasColors() {
        return baseColor != null;
    }

    public Integer getBaseColor() {
        return baseColor;
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

    public void activateFont(Context context) {
        if (fontPath != null) {
            Typeface typeFace = null;
            if (!FontCache.hasTypeface(FontCache.CUSTOM_FONT)) {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
                FontCache.setTypeface(FontCache.CUSTOM_FONT, typeFace);
            }
        }
    }

    public static class Builder {
        private Integer lighterColor;
        private Integer baseColor;
        private boolean darkFontEnabled;
        private String fontPath;

        public Builder setBaseColor(@ColorInt int color) {
            this.baseColor = color;
            this.lighterColor = ColorsUtil.lighter(baseColor);
            return this;
        }

        public Builder setBaseColor(String hexColor) {
            this.baseColor = Color.parseColor(hexColor);
            this.lighterColor = ColorsUtil.lighter(baseColor);
            return this;
        }

        public Builder setCustomFont(String fontPath) {
            this.fontPath = fontPath;
            return this;
        }

        public Builder enableDarkFont() {
            this.darkFontEnabled = true;
            return this;
        }

        public DecorationPreference build() {
            return new DecorationPreference(this);
        }
    }
}