package com.mercadopago.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MPButton extends Button {

    private String mTypeName;

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
            if (mTypeName == null) {
                mTypeName = "fonts/Roboto-Regular.ttf";
            }
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mTypeName);
            setTypeface(tf);
        }
    }


}
