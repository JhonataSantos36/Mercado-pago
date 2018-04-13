package com.mercadopago.plugins;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.plugins.components.BusinessPaymentContainer;
import com.mercadopago.plugins.model.BusinessPaymentModel;
import com.mercadopago.plugins.model.ExitAction;

public class BusinessPaymentResultActivity extends AppCompatActivity implements ActionDispatcher {

    private static final String EXTRA_BUSINESS_PAYMENT_MODEL = "extra_business_payment_model";

    public static void start(final AppCompatActivity activity,
                             final BusinessPaymentModel model,
                             int requestCode) {
        Intent intent = new Intent(activity, BusinessPaymentResultActivity.class);
        intent.putExtra(EXTRA_BUSINESS_PAYMENT_MODEL, model);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusinessPaymentModel model = parseBusinessPaymentModel();
        if (model != null) {
            initializeView(model);
        } else {
            throw new IllegalStateException("BusinessPayment can't be loaded");
        }
    }

    @Nullable
    private BusinessPaymentModel parseBusinessPaymentModel() {
        return getIntent().getExtras() != null ? (BusinessPaymentModel) getIntent()
                .getExtras()
                .getParcelable(EXTRA_BUSINESS_PAYMENT_MODEL) : null;
    }

    private void initializeView(final BusinessPaymentModel model) {
        BusinessPaymentContainer businessPaymentContainer = new BusinessPaymentContainer(new BusinessPaymentContainer.Props(model.payment, model.getPaymentMethodProps()), this);
        ComponentManager componentManager = new ComponentManager(this);
        componentManager.render(businessPaymentContainer);
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof ExitAction) {
            processCustomExit((ExitAction) action);
        } else {
            throw new UnsupportedOperationException("this Action class can't be executed in this screen");
        }
    }

    private void processCustomExit(final ExitAction action) {
        Intent intent = action.toIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
