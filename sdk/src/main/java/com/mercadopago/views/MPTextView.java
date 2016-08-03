package com.mercadopago.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mercadopago.R;

public class MPTextView extends TextView {

    public static final String BLACK = "black";
    public static final String BLACK_IT = "black_it";
    public static final String BOLD = "bold";
    public static final String BOLD_IT = "bold_it";
    public static final String LIGHT = "default_light";
    public static final String ITALIC = "italic";
    public static final String LIGHT_ITALIC = "light_italic";
    public static final String MEDIUM = "medium";
    public static final String MEDIUM_ITALIC = "medium_italic";
    public static final String REGULAR = "regular";
    public static final String THIN = "thin";
    public static final String THIN_ITALIC = "thin_italic";
    public static final String COND_BOLD = "cond_bold";
    public static final String COND_BOLD_ITALIC = "cond_bold_italic";
    public static final String COND_ITALIC = "cond_italic";
    public static final String COND_LIGHT = "cond_light";
    public static final String COND_LIGHT_ITALIC = "cond_light_italic";
    public static final String COND_REGULAR = "cond_regular";
    public static final String MONO_REGULAR = "roboto_regular";
    public static final String MONO_BOLD = "roboto_bold";

    public static final String DEFAULT_FONT = "fonts/Roboto-Light.ttf";

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
        String fontStyle = a.getString(R.styleable.MPTextView_fontStyle) ;
        if (fontStyle != null) {
            switch (fontStyle) {
                case BLACK:
                    mTypeName = "fonts/Roboto-Black.ttf";
                    break;
                case BLACK_IT:
                    mTypeName = "fonts/Roboto-BlackItalic.ttf";
                    break;
                case BOLD:
                    mTypeName = "fonts/Roboto-Bold.ttf";
                    break;
                case BOLD_IT:
                    mTypeName = "fonts/Roboto-BoldItalic.ttf";
                    break;
                case ITALIC:
                    mTypeName = "fonts/Roboto-Italic.ttf";
                    break;
                case LIGHT:
                    mTypeName = DEFAULT_FONT;
                    break;
                case LIGHT_ITALIC:
                    mTypeName = "fonts/Roboto-LightItalic.ttf";
                    break;
                case MEDIUM:
                    mTypeName = "fonts/Roboto-Medium.ttf";
                    break;
                case MEDIUM_ITALIC:
                    mTypeName = "fonts/Roboto-MediumItalic.ttf";
                    break;
                case REGULAR:
                    mTypeName = "fonts/Roboto-Regular.ttf";
                    break;
                case THIN:
                    mTypeName = "fonts/Roboto-Thin.ttf";
                    break;
                case THIN_ITALIC:
                    mTypeName = "fonts/Roboto-ThinItalic.ttf";
                    break;
                case COND_BOLD:
                    mTypeName = "fonts/RobotoCondensed-Bold.ttf";
                    break;
                case COND_BOLD_ITALIC:
                    mTypeName = "fonts/RobotoCondensed-BoldItalic.ttf";
                    break;
                case COND_ITALIC:
                    mTypeName = "fonts/RobotoCondensed-Italic.ttf";
                    break;
                case COND_LIGHT:
                    mTypeName = "fonts/RobotoCondensed-Light.ttf";
                    break;
                case COND_LIGHT_ITALIC:
                    mTypeName = "fonts/RobotoCondensed-LightItalic.ttf";
                    break;
                case COND_REGULAR:
                    mTypeName = "fonts/RobotoCondensed-Regular.ttf";
                    break;
                case MONO_REGULAR:
                    mTypeName = "fonts/RobotoMono-Regular.ttf";
                    break;
                case MONO_BOLD:
                    mTypeName = "fonts/RobotoMono-Bold.ttf";
                    break;
                default:
                    mTypeName = DEFAULT_FONT;
            }
        }
        a.recycle();
    }
}
