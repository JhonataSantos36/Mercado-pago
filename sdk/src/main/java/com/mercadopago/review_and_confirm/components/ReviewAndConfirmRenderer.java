package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.components.Renderer;

/**
 * Created by lbais on 27/2/18.
 */

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {
    @Override
    public View render(final ReviewAndConfirmContainer component, final Context context) {
        final TextView view = new TextView(context);
        return view;
    }
}
