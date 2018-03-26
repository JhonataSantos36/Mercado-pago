package com.mercadopago.plugins.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.components.Renderer;
import com.mercadopago.example.R;

public class MainPaymentRenderer extends Renderer<MainPayment> {

    @Override
    public View render(final MainPayment component, final Context context, final ViewGroup parent) {
        return inflate(R.layout.main_payment_procesor, parent);
    }
}