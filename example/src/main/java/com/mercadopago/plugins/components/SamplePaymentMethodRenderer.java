package com.mercadopago.plugins.components;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mercadopago.example.R;
import com.mercadopago.plugins.PluginRenderer;
import com.mercadopago.util.TextUtil;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentMethodRenderer extends PluginRenderer<SamplePaymentMethod> {

    @Override
    public View renderContents(final SamplePaymentMethod component, final Context context) {

        final View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_second_factor_auth, null);
        final View continueButton = view.findViewById(R.id.button_continue);
        final EditText passwordView = view.findViewById(R.id.password);
        final TextView errorLabel = view.findViewById(R.id.error_label);
        final View progressbar = view.findViewById(R.id.progressbar);

        continueButton.setEnabled(!component.state.authenticating);
        passwordView.setEnabled(!component.state.authenticating);
        passwordView.setText(component.state.password);
        errorLabel.setVisibility(TextUtil.isEmpty(component.state.errorMessage)
                ? View.GONE : View.VISIBLE);
        errorLabel.setText(component.state.errorMessage);
        progressbar.setVisibility(component.state.authenticating ? View.VISIBLE : View.GONE);
        continueButton.setVisibility(component.state.authenticating ? View.GONE : View.VISIBLE);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = view.findViewById(R.id.password);
                component.authenticate(editText.getText().toString());
            }
        });

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    final EditText editText = view.findViewById(R.id.password);
                    component.authenticate(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return view;
    }
}