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
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.plugins.model.BusinessPayment;
import com.mercadopago.plugins.model.GenericPayment;
import com.mercadopago.plugins.model.PluginPayment;
import com.mercadopago.plugins.model.Processor;

public final class PaymentProcessorPluginActivity extends AppCompatActivity implements ActionDispatcher, Processor {

    private static final String EXTRA_BUSINESS_PAYMENT = "extra_business_payment";

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, PaymentProcessorPluginActivity.class);
    }

    public static boolean isBusiness(@Nullable Intent intent) {
        return intent != null && intent.getExtras() != null && intent.getExtras().containsKey(EXTRA_BUSINESS_PAYMENT);
    }

    public static BusinessPayment getBusinessPayment(Intent intent) {
        return (BusinessPayment) intent.getExtras().get(EXTRA_BUSINESS_PAYMENT);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CheckoutStore store = CheckoutStore.getInstance();
        final PaymentProcessor paymentProcessor = store.doesPaymentProcessorSupportPaymentMethodSelected();

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

        component.setDispatcher(this);
        componentManager.render(component);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof PaymentPluginProcessorResultAction) {
            final PluginPayment pluginResult = ((PaymentPluginProcessorResultAction) action).getPluginPaymentResult();
            pluginResult.process(this);
        } else {
            throw new UnsupportedOperationException("Not action with payment processor plugin");
        }
    }

    @Override
    public void process(final BusinessPayment businessPayment) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BUSINESS_PAYMENT, businessPayment);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void process(final GenericPayment genericPayment) {
        final PaymentResult paymentResult = toPaymentResult(genericPayment);
        CheckoutStore.getInstance().setPaymentResult(paymentResult);
        setResult(RESULT_OK);
        finish();
    }

    private PaymentResult toPaymentResult(@NonNull final GenericPayment genericPayment) {

        final Payment payment = new Payment();
        payment.setId(genericPayment.paymentId);
        payment.setPaymentMethodId(genericPayment.paymentData.getPaymentMethod().getId());
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(genericPayment.status);
        payment.setStatusDetail(genericPayment.statusDetail);

        return new PaymentResult.Builder()
                .setPaymentData(genericPayment.paymentData)
                .setPaymentId(payment.getId())
                .setPaymentStatus(payment.getStatus())
                .setPaymentStatusDetail(payment.getStatusDetail())
                .build();
    }
}