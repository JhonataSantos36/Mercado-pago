package com.mercadopago.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.BackAction;
import com.mercadopago.components.Component;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.components.NextAction;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.plugins.model.PaymentMethodInfo;

/**
 * Created by nfortuna on 12/13/17.
 */

public class PaymentMethodPluginActivity extends AppCompatActivity implements ActionDispatcher {

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, PaymentMethodPluginActivity.class);
    }

    private ComponentManager componentManager;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PaymentMethodInfo paymentMethodInfo =
                CheckoutStore.getInstance().getSelectedPaymentMethodInfo(this);
        final PaymentMethodPlugin plugin = CheckoutStore
                .getInstance().getPaymentMethodPluginById(paymentMethodInfo.id);

        if (plugin == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final PluginComponent.Props props = new PluginComponent.Props.Builder()
                .setData(CheckoutStore.getInstance().getData())
                .setCheckoutPreference(CheckoutStore.getInstance().getCheckoutPreference())
                .build();

        final Component component = plugin.createConfigurationComponent(props, this);
        componentManager = new ComponentManager(this);

        if (component == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        component.setDispatcher(this);
        componentManager.render(component);
    }

    @Override
    public void dispatch(final Action action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (action instanceof NextAction) {
                    setResult(RESULT_OK);
                    finish();
                } else if (action instanceof BackAction) {
                    onBackPressed();
                } else {
                    componentManager.dispatch(action);
                }
            }
        });
    }
}