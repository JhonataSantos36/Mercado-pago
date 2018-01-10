package com.mercadopago.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.examples.R;
import com.mercadopago.plugins.components.SamplePaymentMethod;
import com.mercadopago.plugins.components.SampleResourcesProvider;
import com.mercadopago.plugins.model.PaymentMethodInfo;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentMethodPlugin extends PaymentMethodPlugin {

    public SamplePaymentMethodPlugin(@NonNull final Context context) {
        super(context);
    }

    @Override
    public PaymentMethodInfo getPaymentMethodInfo() {
        return new PaymentMethodInfo(
            "sample",
            "Sample Pago",
            R.drawable.mpsdk_sample,
            "Custom payment method"
        );
    }

    @Override
    public String displayOrder() {
        return PaymentMethodPlugin.POSIION_BOTTOM;
    }

    @Override
    public PluginComponent createConfigurationComponent(@NonNull final PluginComponent.Props props) {
        return new SamplePaymentMethod(
            props.toBuilder()
                .setToolbarTitle("Sample Pago")
                .setToolbarVisible(true)
                .build(),
            new SampleResourcesProvider(context)
        );
    }
}