package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.components.Renderer;

public class FooterContainerRenderer extends Renderer<FooterContainer> {
    @Override
    public View render(@NonNull final FooterContainer component,
                       @NonNull final Context context,
                       final ViewGroup parent) {

        return component.getFooter().render(parent);
    }
}