package com.mercadopago;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.util.JsonUtil;

public abstract class MercadoPagoActivity extends AppCompatActivity {

    private boolean mActivityActive;
    protected DecorationPreference mDecorationPreference;
    private FailureRecovery mFailureRecovery;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreation();
        getDecorationPreference();
        getActivityParameters();
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
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

    private void getDecorationPreference() {
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }
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

    protected void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    protected void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    protected boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    protected int getCustomBaseColor() {
        return mDecorationPreference.getBaseColor();
    }

    protected boolean isDarkFontEnabled() {
        return mDecorationPreference != null && mDecorationPreference.isDarkFontEnabled();
    }

    protected int getDarkFontColor() {
        return mDecorationPreference.getDarkFontColor(this);
    }

    protected void decorate(Button button) {
        if (isCustomColorSet()) {
            button.setBackgroundColor(getCustomBaseColor());
        }

        if (isDarkFontEnabled()) {
            button.setTextColor(getDarkFontColor());
        }
    }

    protected void decorate(Toolbar toolbar) {
        if (toolbar != null) {
            if (isCustomColorSet()) {
                toolbar.setBackgroundColor(getCustomBaseColor());
            }
            decorateUpArrow(toolbar);
        }
    }

    protected void decorateFont(TextView textView) {
        if (textView != null) {
            if (isDarkFontEnabled()) {
                textView.setTextColor(getDarkFontColor());
            }
        }
    }

    protected void decorateUpArrow(Toolbar toolbar) {
        if (isDarkFontEnabled()) {
            int darkFont = getDarkFontColor();
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null && getSupportActionBar() != null) {
                upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }
}
