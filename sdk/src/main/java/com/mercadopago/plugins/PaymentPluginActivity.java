package com.mercadopago.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.plugins.model.ProcessorPaymentResult;

/**
 * Created by nfortuna on 12/13/17.
 */

public class PaymentPluginActivity extends AppCompatActivity implements ActionDispatcher {

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, PaymentPluginActivity.class);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CheckoutStore store = CheckoutStore.getInstance();
        final PaymentProcessor paymentProcessor = store.getPaymentProcessor();

        if (paymentProcessor == null) {
            cancel();
            return;
        }

        final PluginComponent.Props props = new PluginComponent.Props.Builder()
                .setData(store.getData())
                .setPaymentData(store.getPaymentData())
                .setCheckoutPreference(store.getCheckoutPreference())
                .build();

        final PluginComponent component = paymentProcessor.createPaymentComponent(props, this);
        final ComponentManager componentManager = new ComponentManager(this);

        if (component == null) {
            cancel();
            return;
        }

        component.setDispatcher(this);
        componentManager.render(component);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof PluginPaymentResultAction) {
            final ProcessorPaymentResult pluginResult = ((PluginPaymentResultAction) action).getPluginPaymentResult();
            if (pluginResult != null) {
                try {
                    final PaymentResult paymentResult = toPaymentResult(pluginResult);
                    CheckoutStore.getInstance().setPaymentResult(paymentResult);
                    setResult(RESULT_OK);
                } catch (final Exception e) {
                    setResult(RESULT_CANCELED);
                }
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }

    private PaymentResult toPaymentResult(@NonNull final ProcessorPaymentResult pluginResult) {

        final Payment payment = new Payment();
        payment.setId(pluginResult.paymentId);
        payment.setPaymentMethodId(pluginResult.paymentData.getPaymentMethod().getId());
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(pluginResult.status);
        payment.setStatusDetail(pluginResult.statusDetail);

        return new PaymentResult.Builder()
                .setPaymentData(pluginResult.paymentData)
                .setPaymentId(payment.getId())
                .setPaymentStatus(payment.getStatus())
                .setPaymentStatusDetail(payment.getStatusDetail())
                .build();
    }
}