package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.FontCache;

public class MPTextView extends TextView {

    public static final String LIGHT = "light";
    public static final String REGULAR = "regular";
    public static final String MONO_REGULAR = "mono_regular";
    public static final String DEFAULT_FONT = "fonts/Roboto-Regular.ttf";


    private String mTypeName;
    private String mFontStyle;
    private Boolean mAllowCustomFont;

    public MPTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAllowCustomFont = true;
        readAttr(context, attrs);
        init();
    }

    public MPTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MPTextView(Context context) {
        super(context);
        mAllowCustomFont = true;
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = getCustomTypeface();
            setTypeface(tf);
        }
    }

    private Typeface getCustomTypeface() {
        Typeface tf;
        Typeface customFont = null;

        if (isLightFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_LIGHT_FONT);
        } else if (isRegularFontStyle() && FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT);
        }

        if (mAllowCustomFont && customFont != null) {
            tf = customFont;
        } else {
            if (mTypeName == null) {
                mTypeName = DEFAULT_FONT;
            }
            tf = FontCache.createTypeface(mTypeName, getContext());
        }
        return tf;
    }


    private boolean isLightFontStyle() {
        return mFontStyle != null && mFontStyle.equals(LIGHT);
    }

    private boolean isRegularFontStyle() {
        return (mFontStyle != null && mFontStyle.equals(REGULAR));
    }


    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        this.mFontStyle = a.getString(R.styleable.MPTextView_font_style);
        this.mAllowCustomFont = a.getBoolean(R.styleable.MPTextView_allowCustomFont, true);
        if (this.mFontStyle == null) {
            this.mFontStyle = REGULAR;
        }
        switch (mFontStyle) {
            case LIGHT:
                mTypeName = "fonts/Roboto-Light.ttf";
                break;
            case REGULAR:
                mTypeName = DEFAULT_FONT;
                break;
            case MONO_REGULAR:
                mTypeName = "fonts/RobotoMono-Regular.ttf";
                break;
            default:
                mTypeName = DEFAULT_FONT;
        }
        a.recycle();
    }
}
