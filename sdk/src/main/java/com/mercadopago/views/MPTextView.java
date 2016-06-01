package com.mercadopago.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mercadopago.R;

public class MPTextView extends TextView {

    public static final String BLACK = "black";
    public static final String BOLD = "bold";
    public static final String BOLD_IT = "bold_it";
    public static final String EXTRA_BOLD = "extra_bold";
    public static final String DEFAULT_LIGHT = "default_light";
    public static final String ITALIC = "italic";
    public static final String REGULAR_ITALIC = "reg_italic";
    public static final String REGULAR = "regular";
    public static final String SEMI_BOLD = "semi_bold";
    public static final String SEMI_BOLD_ITALIC = "semi_bold_italic";
    public static final String COND_LIGHT = "cond_light";
    public static final String COND_LIGHT_ITALIC = "cond_light_italic";
    public static final String COND_REGULAR = "cond_regular";
    public static final String COND_REGULAR_ITALIC = "cond_regular_italic";
    public static final String COND_SEMI_BOLD = "cond_semi_bold";
    public static final String COND_SEMI_BOLD_ITALIC = "cond_semi_bold_italic";
    public static final String ROBOTO_REGULAR = "roboto_regular";

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
                mTypeName = "fonts/ProximaNova-Light.otf";
            }
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mTypeName);
            setTypeface(tf);
        }
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        String fontStyle = a.getString(R.styleable.MPTextView_fontStyle) ;
        if (fontStyle != null) {
            switch (fontStyle) {
                case BLACK:
                    mTypeName = "fonts/ProximaNova-Black.otf";
                    break;
                case BOLD:
                    mTypeName = "fonts/ProximaNova-Bold.otf";
                    break;
                case BOLD_IT:
                    mTypeName = "fonts/ProximaNova-BoldIt.otf";
                    break;
                case EXTRA_BOLD:
                    mTypeName = "fonts/ProximaNova-Extrabold.otf";
                    break;
                case DEFAULT_LIGHT:
                    mTypeName = "fonts/ProximaNova-Light.otf";
                    break;
                case ITALIC:
                    mTypeName = "fonts/ProximaNova-LightItalic.otf";
                    break;
                case REGULAR_ITALIC:
                    mTypeName = "fonts/ProximaNova-RegItalic.otf";
                    break;
                case REGULAR:
                    mTypeName = "fonts/ProximaNova-Regular.otf";
                    break;
                case SEMI_BOLD:
                    mTypeName = "fonts/ProximaNova-Semibold.otf";
                    break;
                case SEMI_BOLD_ITALIC:
                    mTypeName = "fonts/ProximaNova-SemiboldItalic.otf";
                    break;
                case COND_LIGHT:
                    mTypeName = "fonts/ProximaNovaCond-Light.otf";
                    break;
                case COND_LIGHT_ITALIC:
                    mTypeName = "fonts/ProximaNovaCond-LightIt.otf";
                    break;
                case COND_REGULAR:
                    mTypeName = "fonts/ProximaNovaCond-Regular.otf";
                    break;
                case COND_REGULAR_ITALIC:
                    mTypeName = "fonts/ProximaNovaCond-RegularIt.otf";
                    break;
                case COND_SEMI_BOLD:
                    mTypeName = "fonts/ProximaNovaCond-Semibold.otf";
                    break;
                case COND_SEMI_BOLD_ITALIC:
                    mTypeName = "fonts/ProximaNovaCond-SemiboldIt.otf";
                    break;
                case ROBOTO_REGULAR:
                    mTypeName = "fonts/RobotoMono-Regular.ttf";
                    break;
                default:
                    mTypeName = "fonts/ProximaNova-Light.otf";
            }
        }
        a.recycle();
    }
}
