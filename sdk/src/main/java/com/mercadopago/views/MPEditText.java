package com.mercadopago.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mercadopago.R;

public class MPEditText extends EditText {

    private String mTypeName;
    private int mErrorColor;

    public MPEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setErrorColor(context, attrs, defStyle);
    }

    public MPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setErrorColor(context, attrs, 0);
    }

    public MPEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            if (mTypeName == null) {
                mTypeName = "fonts/ProximaNova-Light.otf";
            }
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mTypeName);
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
        if (mErrorColor == 0 ) return;
        if (error) {
            getBackground().setColorFilter(mErrorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            getBackground().setColorFilter(null);
        }
    }

}
