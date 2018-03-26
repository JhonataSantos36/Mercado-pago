package com.mercadopago.hooks.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.example.R;
import com.mercadopago.hooks.HookRenderer;

public class PaymentTypeConfirmRenderer extends HookRenderer<PaymentTypeConfirm> {

    @Override
    public View renderContents(final PaymentTypeConfirm component, final Context context) {

        final View view = LayoutInflater.from(context)
                .inflate(R.layout.hook_payment_type_confirm, null);

        final TextView label = view.findViewById(R.id.label);
        label.setText(component.props.paymentTypeId);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                component.onContinue();
            }
        });

        return view;
    }
}
