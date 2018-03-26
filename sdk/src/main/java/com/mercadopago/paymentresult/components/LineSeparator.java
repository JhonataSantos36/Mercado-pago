package com.mercadopago.paymentresult.components;


import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.CompactComponent;

import javax.annotation.Nonnull;

public class LineSeparator extends CompactComponent<LineSeparator.Props, Void> {

    public LineSeparator(final Props props) {
        super(props);
    }

    public static class Props {
        @ColorRes
        private final int color;

        public Props(@ColorRes int color) {
            this.color = color;
        }
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        View view = inflate(parent, R.layout.mpsdk_view_separator);
        view.setBackgroundColor(parent.getContext().getResources().getColor(props.color));
        return view;
    }
}
