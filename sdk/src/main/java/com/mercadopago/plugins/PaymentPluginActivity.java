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
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.plugins.model.PluginPaymentResult;

/**
 * Created by nfortuna on 12/13/17.
 */

public class PaymentPluginActivity extends AppCompatActivity implements ActionDispatcher {

    private static final String TAG = PaymentPluginActivity.class.getName();

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
        if (action instanceof PluginPaymentResultAction) {
            final PluginPaymentResult pluginResult = ((PluginPaymentResultAction) action).getPluginPaymentResult();
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

    private PaymentResult toPaymentResult(@NonNull final PluginPaymentResult pluginResult) {

        final Payment payment = new Payment();
        payment.setId(pluginResult.paymentId);
        payment.setPaymentMethodId(pluginResult.paymentMethodInfo.id);
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(pluginResult.status);
        payment.setStatusDetail(pluginResult.statusDetail);

        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(String.valueOf(pluginResult.paymentId));
        paymentMethod.setName(pluginResult.paymentMethodInfo.name);
        paymentMethod.setPaymentTypeId(PaymentTypes.PLUGIN);
        paymentMethod.setIcon(pluginResult.paymentMethodInfo.icon);

        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);

        return new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(payment.getId())
                .setPaymentStatus(payment.getStatus())
                .setPaymentStatusDetail(payment.getStatusDetail())
                .build();
    }
}