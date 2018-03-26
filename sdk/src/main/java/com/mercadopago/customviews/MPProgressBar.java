package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;

/**
 * Credit: MercadoLibre's Spinner class.
 *
 * @author Pablo Diaz
 * @since 28/4/16
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class MPProgressBar extends FrameLayout {

    private final boolean autostart;

    private final MPLoadingSpinner spinner;

    public MPProgressBar(final Context context) {
        this(context, null);
    }

    public MPProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPProgressBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.mpsdk_mp_progress_bar, this);

        spinner = findViewById(R.id.ui_spinner);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPProgressBar, defStyleAttr, 0);

        autostart = typedArray.getBoolean(R.styleable.MPProgressBar_mpsdk_autostart, true);

        final int size = typedArray.getDimensionPixelSize(R.styleable.MPProgressBar_mpsdk_size, getResources().getDimensionPixelSize(R.dimen.mpsdk_progress_size));

        configureSpinnerSize(size);

        typedArray.recycle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (autostart) {
            start();
        }
    }

    /**
     * Starts the spinner.
     */
    public void start() {
        spinner.onStart();
    }

    /**
     * Stops the spinner.
     */
    public void stop() {
        spinner.onStop();
    }

    private void configureSpinnerSize(int size) {
        final ViewGroup.LayoutParams params = spinner.getLayoutParams();
        params.height = size;
        params.width = size;
        spinner.setLayoutParams(params);
    }
}
