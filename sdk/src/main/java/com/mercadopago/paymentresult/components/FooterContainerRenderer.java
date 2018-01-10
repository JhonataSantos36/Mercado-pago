package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;

import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

public class FooterContainerRenderer extends Renderer<FooterContainer> {
    @Override
    public View render(final FooterContainer component, final Context context) {
        return RendererFactory.create(context, component.getFooter()).render();
    }
}