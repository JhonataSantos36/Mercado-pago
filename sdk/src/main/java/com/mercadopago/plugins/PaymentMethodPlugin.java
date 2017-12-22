package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.plugins.model.PaymentMethodInfo;

/**
 * Created by nfortuna on 12/11/17.
 */

public abstract class PaymentMethodPlugin {

    public static final String POSIION_TOP = "position_up";
    public static final String POSIION_BOTTOM = "position_down";

    protected Context context;

    public PaymentMethodPlugin(@NonNull final Context context) {
        this.context = context;
    }

    public String displayOrder() {
        return POSIION_TOP;
    }

    public boolean hasConfigurationComponent() {
        final PluginComponent.Props props = new PluginComponent.Props.Builder().build();
        return createConfigurationComponent(props) != null;
    }

    public abstract PaymentMethodInfo getPaymentMethodInfo();

    public abstract PluginComponent createConfigurationComponent(@NonNull final PluginComponent.Props props);

}
