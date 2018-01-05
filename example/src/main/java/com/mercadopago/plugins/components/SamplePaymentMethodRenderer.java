package com.mercadopago.plugins.components;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mercadopago.examples.R;
import com.mercadopago.plugins.PluginRenderer;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentMethodRenderer extends PluginRenderer<SamplePaymentMethod> {

    @Override
    public View renderContents() {

        final View view = LayoutInflater.from(context).inflate(R.layout.mpsdk_pmplugin_sample_config, null);

        view.findViewById(R.id.button_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                component.next();
            }
        });

        final EditText docu = view.findViewById(R.id.docu);

        docu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                component.setDocument(s.toString());
            }
        });

        return view;
    }
}