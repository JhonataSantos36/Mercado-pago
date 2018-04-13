package com.mercadopago.components;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;

import javax.annotation.Nonnull;

public class HelpComponent extends CompactComponent<String, Void> {

    public HelpComponent(final String s) {
        super(s);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final ViewGroup bodyErrorView = (ViewGroup) inflate(parent, R.layout.mpsdk_payment_result_body_error);
        TextView errorTitle = bodyErrorView.findViewById(R.id.paymentResultBodyErrorTitle);
        TextView errorDescription = bodyErrorView.findViewById(R.id.paymentResultBodyErrorDescription);
        bodyErrorView.findViewById(R.id.paymentResultBodyErrorSecondDescription).setVisibility(View.GONE);
        errorTitle.setText(parent.getContext().getString(R.string.mpsdk_what_can_do));
        errorDescription.setText(props);
        return bodyErrorView;
    }
}
