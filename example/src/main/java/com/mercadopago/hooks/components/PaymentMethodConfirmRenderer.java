package com.mercadopago.hooks.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.examples.R;
import com.mercadopago.hooks.HookRenderer;

public class PaymentMethodConfirmRenderer extends HookRenderer<PaymentMethodConfirm> {

    @Override
    public View renderContents(final PaymentMethodConfirm component, final Context context) {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_example_hook_payment_method_confirm, null);
        final TextView label = view.findViewById(R.id.label);
        label.setText(component.props.paymentData.getPaymentMethod().getName());
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                component.onContinue();
            }
        });
        return view;
    }
}
