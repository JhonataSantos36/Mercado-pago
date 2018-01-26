package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.FontCache;

public class MPTextView extends AppCompatTextView {

    public static final String LIGHT = "light";
    public static final String REGULAR = "regular";
    public static final String BOLD = "bold";
    public static final String MONO_REGULAR = "mono_regular";

    private String mFontStyle;
    private Boolean mAllowCustomFont;

    public MPTextView(Context context) {
        this(context, null);
    }

    public MPTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAllowCustomFont = true;
        readAttr(context, attrs);

        if (!isInEditMode()) {
            Typeface tf = getCustomTypeface();

            if (tf == null) {
                setDefaultTypeface();
            } else {
                setTypeface(tf);
            }
        }
    }

    private void setDefaultTypeface() {
        if (isLightFontStyle()) {
            setTextStyle(getContext(), R.style.mpsdk_font_roboto_light);
        } else if (isRegularFontStyle()) {
            setTextStyle(getContext(), R.style.mpsdk_font_roboto_regular);
        } else if (isBoldFontStyle()) {
            setTextStyle(getContext(), R.style.mpsdk_font_roboto_bold);
        } else if (isMonoRegularFontStyle()) {
            setTextStyle(getContext(), R.style.mpsdk_font_roboto_mono);
        }
    }

    private Typeface getCustomTypeface() {
        Typeface tf = null;
        Typeface customFont = null;

        if (isLightFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_LIGHT_FONT);
        } else if (isRegularFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT);
        }

        if (mAllowCustomFont && customFont != null) {
            tf = customFont;
        }
        return tf;
    }


    private boolean isLightFontStyle() {
        return LIGHT.equals(mFontStyle);
    }

    private boolean isRegularFontStyle() {
        return REGULAR.equals(mFontStyle);
    }

    private boolean isBoldFontStyle() {
        return BOLD.equals(mFontStyle);
    }

    private boolean isMonoRegularFontStyle() {
        return MONO_REGULAR.equals(mFontStyle);
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        this.mFontStyle = a.getString(R.styleable.MPTextView_font_style);
        this.mAllowCustomFont = a.getBoolean(R.styleable.MPTextView_allowCustomFont, true);
        if (this.mFontStyle == null) {
            this.mFontStyle = REGULAR;
        }
        a.recycle();
    }

    private void setTextStyle(Context context, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            super.setTextAppearance(context, resId);
        } else {
            super.setTextAppearance(resId);
        }
    }
}
