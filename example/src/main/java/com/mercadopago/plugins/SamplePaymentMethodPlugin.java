package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.example.R;
import com.mercadopago.plugins.components.SamplePaymentMethod;
import com.mercadopago.plugins.components.SampleResourcesProvider;
import com.mercadopago.plugins.model.PaymentMethodInfo;

public class SamplePaymentMethodPlugin extends PaymentMethodPlugin {


    public SamplePaymentMethodPlugin() {
        super("account_money");
    }

    @Override
    @NonNull
    public PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context) {
        return new PaymentMethodInfo(
                getId(),
                "Dinero en cuenta",
                R.drawable.mpsdk_sample,
                "Custom payment method"
        );
    }

    @Override
    public String displayOrder() {
        return PaymentMethodPlugin.POSIION_BOTTOM;
    }

    @Override
    public PluginComponent createConfigurationComponent(@NonNull final PluginComponent.Props props,
                                                        @NonNull final Context context) {
        return new SamplePaymentMethod(
                props.toBuilder()
                        .setToolbarTitle("Sample Pago")
                        .setToolbarVisible(true)
                        .build(),
                new SampleResourcesProvider(context)
        );
    }
}
