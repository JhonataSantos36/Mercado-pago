package com.mercadopago.hooks.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.examples.R;
import com.mercadopago.hooks.HookRenderer;

public class PaymentConfirmRenderer extends HookRenderer {

    @Override
    public View render() {

        final View parent = super.render();
        final ViewGroup contents = (ViewGroup) parent.findViewById(R.id.contents);

        final View view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_example_hook_payment_confirm, null);

        final TextView amount = view.findViewById(R.id.amount);
        amount.setText(component.props.paymentData.getTransactionAmount().toString());

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                component.onContinue();
            }
        });

        contents.addView(view);
        return parent;
    }
}