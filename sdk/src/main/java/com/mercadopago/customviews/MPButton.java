package com.mercadopago.customviews;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.mercadopago.R;

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
            setTextStyle(getContext(), R.style.mpsdk_font_roboto_regular);
        }
    }

    private void setTextStyle(Context context, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            super.setTextAppearance(context, resId);
        } else {
            super.setTextAppearance(resId);
        }
    }

}
