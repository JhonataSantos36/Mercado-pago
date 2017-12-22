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
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.plugins.model.PaymentMethodInfo;

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

        final PaymentMethodInfo paymentMethodInfo =
                CheckoutStore.getInstance().getSelectedPaymentMethod();

        if (paymentMethodInfo == null) {
            finish();
            return;
        }

        final PaymentPlugin paymentPlugin = CheckoutStore.getInstance().getPaymentPluginByMethod(paymentMethodInfo.id);
        if (paymentPlugin == null) {
            finish();
            return;
        }

        final PluginComponent.Props props = new PluginComponent.Props.Builder()
                .setData(CheckoutStore.getInstance().getData()).build();

        final PluginComponent component = paymentPlugin.createPaymentComponent(props);
        final ComponentManager componentManager = new ComponentManager(this);

        if (component == null) {
            finish();
            return;
        }

        component.setDispatcher(this);
        componentManager.render(component);
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof PaymentResultAction) {
            CheckoutStore.getInstance()
                    .setPaymentResult(((PaymentResultAction) action).getPaymentResult());
            setResult(RESULT_OK);
            finish();
        }
    }
}