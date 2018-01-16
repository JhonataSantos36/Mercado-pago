package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;

import com.mercadopago.callbacks.FailureRecovery;

@Deprecated
public abstract class MercadoPagoActivity extends MercadoPagoBaseActivity {

    private boolean mActivityActive;
    private FailureRecovery mFailureRecovery;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreation();
        getActivityParameters();

        setActivity();
        mActivityActive = true;
        setContentView();
        try {
            validateActivityParameters();
            initializeControls();
            initializeFragments(savedInstanceState);
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void onBeforeCreation() {
        //Perform actions
    }

    private void setActivity() {
        mActivity = this;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    protected abstract void getActivityParameters();

    protected abstract void validateActivityParameters() throws IllegalStateException;

    protected abstract void setContentView();

    protected abstract void initializeControls();

    protected abstract void onValidStart();

    protected abstract void onInvalidStart(String message);

    protected void initializeFragments(Bundle savedInstanceState) {
    }

    @Override
    protected void onResume() {
        mActivityActive = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActivityActive = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActivityActive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActivityActive = false;
        super.onStop();
    }

    protected boolean isActivityActive() {
        return mActivityActive;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    protected void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
