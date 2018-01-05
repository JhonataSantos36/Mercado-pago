package com.mercadopago.plugins.components;

import android.support.annotation.NonNull;

import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.components.NextAction;
import com.mercadopago.components.RendererFactory;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentMethod extends PluginComponent {

    static {
        RendererFactory.register(SamplePaymentMethod.class, SamplePaymentMethodRenderer.class);
    }

    public SamplePaymentMethod(@NonNull final Props props) {
        super(props);
    }

    public void next() {
        getDispatcher().dispatch(new NextAction());
    }

    public void setDocument(@NonNull final String document) {
        props.data.put("docu", document);
    }
}