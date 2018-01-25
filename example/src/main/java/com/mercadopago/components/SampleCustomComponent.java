package com.mercadopago.components;

import android.support.annotation.NonNull;

import com.mercadopago.plugins.components.SampleCustomRenderer;

/**
 * Created by nfortuna on 1/24/18.
 */

public class SampleCustomComponent extends CustomComponent {

    static {
        RendererFactory.register(SampleCustomComponent.class, SampleCustomRenderer.class);
    }

    public SampleCustomComponent(@NonNull Props props) {
        super(props);
    }
}
