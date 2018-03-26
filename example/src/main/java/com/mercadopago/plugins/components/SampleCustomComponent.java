package com.mercadopago.plugins.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.CustomComponent;
import com.mercadopago.components.RendererFactory;

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
