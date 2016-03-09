package com.mercadopago.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class MPEditText extends EditText {

    private String mTypeName;

    public MPEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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
}
