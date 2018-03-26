package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;

/**
 * Created by mromar on 2/28/18.
 */

public class DisclaimerRenderer extends Renderer<DisclaimerComponent> {

    @Override
    protected View render(@NonNull DisclaimerComponent component, @NonNull Context context, @Nullable ViewGroup parent) {
        final View disclaimerView = inflate(R.layout.mpsdk_disclaimer, parent);
        final MPTextView disclaimerTextView = disclaimerView.findViewById(R.id.disclaimer);

        setText(disclaimerTextView, component.props.disclaimer);

        return disclaimerView;
    }
}
