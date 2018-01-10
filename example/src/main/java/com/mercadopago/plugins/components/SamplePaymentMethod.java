package com.mercadopago.plugins.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.NextAction;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.plugins.PluginComponent;
import com.mercadopago.util.TextUtils;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentMethod extends PluginComponent<SamplePaymentMethod.SampleState> {

    static {
        RendererFactory.register(SamplePaymentMethod.class, SamplePaymentMethodRenderer.class);
    }

    private SampleResources resources;

    public SamplePaymentMethod(@NonNull final Props props, final SampleResources resources) {
        super(props);
        this.resources = resources;
        this.state = new SamplePaymentMethod.SampleState(false, null);
    }

    public void authenticate(final String password) {

        if (TextUtils.isEmpty(password)) {

            setState(new SampleState(false, resources.getPasswordRequiredMessage()));

        } else {

            setState(new SampleState(true, null, password));

            // Simular llamada API....
            // En otro thread
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2000);
                    } catch (final InterruptedException e) {
                        //nada
                    }

                    if ("123".equals(password)) {
                        getDispatcher().dispatch(new NextAction());
                    } else {
                        setState(new SampleState(false, resources.getPasswordErrorMessage(), password));
                    }
                }
            }).start();
        }
    }

    public static class SampleState {

        public final boolean authenticating;
        public final String password;
        public final String errorMessage;

        public SampleState(final boolean authenticating) {
            this(authenticating, "", "");
        }

        public SampleState(final boolean authenticating, final String errorMessage) {
            this(authenticating, errorMessage, "");
        }

        public SampleState(final boolean authenticating, final String errorMessage, final String password) {
            this.authenticating = authenticating;
            this.errorMessage = errorMessage;
            this.password = password;
        }
    }
}