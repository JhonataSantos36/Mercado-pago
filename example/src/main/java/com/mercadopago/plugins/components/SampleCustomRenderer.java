package com.mercadopago.plugins.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.components.Renderer;
import com.mercadopago.components.SampleCustomComponent;
import com.mercadopago.examples.R;
import com.mercadopago.plugins.model.ExitAction;

/**
 * Created by nfortuna on 1/24/18.
 */
public class SampleCustomRenderer extends Renderer<SampleCustomComponent> {
    @Override
    public View render(@NonNull final SampleCustomComponent component, @NonNull final Context context, final ViewGroup parent) {
        View view = inflate(R.layout.mpsdk_sample_custom_component, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                component.getDispatcher().dispatch(new ExitAction("Custom Exit Action", 999));
            }
        });
        return view;
    }
}