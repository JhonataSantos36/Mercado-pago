package com.mercadopago.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.FontCache;

public class MPButton extends AppCompatButton {

    public MPButton(Context context) {
        super(context);
        init();
    }

    public MPButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MPButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = getCustomTypeface();

            if (tf == null) {
                setTextStyle(getContext(), R.style.mpsdk_font_roboto_regular);
            } else {
                setTypeface(tf);
            }
        }
    }

    private Typeface getCustomTypeface() {
        Typeface tf = null;
        Typeface customFont = null;

        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT);
        }
        if (customFont != null) {
            tf = customFont;
        }
        return tf;
    }

    private void setTextStyle(Context context, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            super.setTextAppearance(context, resId);
        } else {
            super.setTextAppearance(resId);
        }
    }

}
