package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.FontCache;

public class MPEditText extends AppCompatEditText {

    private String mTypeName;
    private int mErrorColor;

    public MPEditText(final Context context) {
        this(context, null);
    }

    public MPEditText(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MPEditText(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setErrorColor(context, attrs, defStyle);
        if (!isInEditMode()) {
            if (mTypeName == null) {
                mTypeName = "fonts/Roboto-Regular.ttf";
            }
            Typeface tf = FontCache.createTypeface(mTypeName, getContext());
            setTypeface(tf);
        }
    }

    private void setErrorColor(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MPEditText, defStyle, 0);
        String errorColor = typedArray.getString(R.styleable.MPEditText_errorColor);
        if (errorColor != null) mErrorColor = Color.parseColor(errorColor);
        typedArray.recycle();
    }

    public void toggleLineColorOnError(boolean error) {
        if (mErrorColor == 0) return;
        if (error) {
            getBackground().setColorFilter(mErrorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            getBackground().setColorFilter(null);
        }
    }

}
