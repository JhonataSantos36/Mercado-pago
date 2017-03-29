package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mercadopago.R;

public class MPTextView extends TextView {

    public static final String LIGHT = "default_light";
    public static final String REGULAR = "regular";
    public static final String MONO_REGULAR = "roboto_regular";
    public static final String DEFAULT_FONT = "fonts/Roboto-Regular.ttf";

    private String mTypeName;

    public MPTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttr(context, attrs);
        init();
    }

    public MPTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttr(context, attrs);
        init();
    }

    public MPTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            if (mTypeName == null) {
                mTypeName = DEFAULT_FONT;
            }
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mTypeName);
            setTypeface(tf);
        }
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        String fontStyle = a.getString(R.styleable.MPTextView_fontStyle);
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
