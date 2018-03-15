package com.mercadopago.plugins;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.plugins.components.BusinessPaymentContainer;
import com.mercadopago.plugins.model.BusinessPayment;
import com.mercadopago.plugins.model.ButtonAction;

public class BusinessPaymentResultActivity extends AppCompatActivity implements ActionDispatcher {

    private static final String EXTRA_BUSINESS_PAYMENT = "extra_business_payment";
    public static final String EXTRA_CLIENT_RES_CODE = "extra_res_code";

    public static void start(final AppCompatActivity activity,
                             final BusinessPayment businessPayment,
                             int requestCode) {
        Intent intent = new Intent(activity, BusinessPaymentResultActivity.class);
        intent.putExtra(EXTRA_BUSINESS_PAYMENT, businessPayment);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusinessPayment businessPayment = parseIntent();
        if (businessPayment != null) {
            initializeView(businessPayment);
        } else {
            throw new IllegalStateException("BusinessPayment can't be loaded");
        }
    }

    @Nullable
    private BusinessPayment parseIntent() {
        return getIntent().getExtras() != null ? (BusinessPayment) getIntent()
                .getExtras()
                .getParcelable(EXTRA_BUSINESS_PAYMENT) : null;
    }

    private void initializeView(final BusinessPayment businessPayment) {
        BusinessPaymentContainer businessPaymentContainer = new BusinessPaymentContainer(businessPayment, this);
        ComponentManager componentManager = new ComponentManager(this);
        componentManager.render(businessPaymentContainer);
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof ButtonAction) {
            int resCode = ((ButtonAction) action).getResCode();
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CLIENT_RES_CODE, resCode);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            throw new UnsupportedOperationException("this Action class can't be executed in this screen");
        }
    }
}
