package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.plugins.model.PaymentMethodInfo;

import java.util.Map;


public abstract class PaymentMethodPlugin {

    public static final String POSIION_TOP = "position_up";
    public static final String POSIION_BOTTOM = "position_down";

    private final String id;

    public PaymentMethodPlugin(final String id) {
        this.id = id;
    }

    public String displayOrder() {
        return POSIION_TOP;
    }

    public boolean isEnabled(@NonNull final Map<String, Object> data) {
        return (boolean) data.get(DataInitializationTask.KEY_INIT_SUCCESS);
    }

    public String getId() {
        return id;
    }

    @NonNull
    public abstract
    PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context);

    @Nullable
    public abstract
    PluginComponent createConfigurationComponent(@NonNull final PluginComponent.Props props,
                                                 @NonNull final Context context);

    public boolean isConfigurationComponentEnabled(@NonNull final Map<String, Object> data) {
        return true;
    }
}