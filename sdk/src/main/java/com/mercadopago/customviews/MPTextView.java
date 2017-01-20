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
        if (FontCache.hasTypeface(FontCache.CUSTOM_FONT)) {
            customFont = FontCache.getTypeface(FontCache.CUSTOM_FONT);
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

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        String fontStyle = a.getString(R.styleable.MPTextView_fontStyle);
        mAllowCustomFont = a.getBoolean(R.styleable.MPTextView_allowCustomFont, true);
        if (fontStyle != null) {
            switch (fontStyle) {
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
        }
        a.recycle();
    }
}
