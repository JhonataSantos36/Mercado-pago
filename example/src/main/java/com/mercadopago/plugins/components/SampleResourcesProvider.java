package com.mercadopago.plugins.components;

import android.content.Context;

import com.mercadopago.examples.R;

/**
 * Created by nfortuna on 1/10/18.
 */

public class SampleResourcesProvider implements SampleResources {

    private Context context;

    public SampleResourcesProvider(Context context) {
        this.context = context;
    }

    @Override
    public String getPasswordErrorMessage() {
        return context.getString(R.string.auth_error_password);
    }

    @Override
    public String getPasswordRequiredMessage() {
        return context.getString(R.string.auth_error_password_required);
    }
}
