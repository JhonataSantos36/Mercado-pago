package com.mercadopago.plugins.components;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.components.Renderer;
import com.mercadopago.examples.R;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentRenderer extends Renderer<SamplePayment> {

    @Override
    public View render() {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_pmplugin_sample_payment, null);
        final TextView docu = view.findViewById(R.id.docu);
        docu.setText(component.getDocument());
        return view;
    }
}