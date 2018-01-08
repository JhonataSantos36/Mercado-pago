package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;

public class FooterRenderer extends Renderer<Footer> {
    @Override
    public View render() {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_payment_result_footer, null);

        final Button button = view.findViewById(R.id.button);
        final TextView link = view.findViewById(R.id.link);

        if (component.props.buttonAction == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setText(component.props.buttonAction.label);
            button.setVisibility(View.VISIBLE);
        }

        if (component.props.linkAction == null) {
            link.setVisibility(View.GONE);
        } else {
            link.setText(component.props.linkAction.label);
            link.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                component.getDispatcher().dispatch(component.props.buttonAction.action);
            }
        });

        view.findViewById(R.id.link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                component.getDispatcher().dispatch(component.props.linkAction.action);
            }
        });

        return view;
    }
}