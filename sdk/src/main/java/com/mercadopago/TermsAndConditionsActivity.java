package com.mercadopago;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.views.MPTextView;

public class TermsAndConditionsActivity extends MercadoPagoActivity {

    protected View mMPTermsAndConditionsView;
    protected View mBankDealsTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ProgressBar mProgressbar;
    protected DecorationPreference mDecorationPreference;

    @Override
    protected void onValidStart() {
        if (getIntent().getStringExtra("termsAndConditions") != null) {
            mMPTermsAndConditionsView.setVisibility(View.GONE);
            showBankDealsTermsAndConditions();
        }
        else {
            mBankDealsTermsAndConditionsView.setVisibility(View.GONE);
            showMPTermsAndConditions();
        }
    }

    @Override
    protected void showError(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(isCustomColorSet()) {
            if(toolbar != null) {
                decorateToolbar(toolbar);
            }
        }
        if(isDarkFontEnabled()) {
            TextView title = (TextView) findViewById(R.id.mpsdkTitle);
            title.setTextColor(getDarkFontColor());
        }
    }

    private void decorateToolbar(Toolbar toolbar) {
        if(isDarkFontEnabled()) {
            Drawable upArrow = toolbar.getNavigationIcon();
            if(upArrow != null) {
                upArrow.setColorFilter(getDarkFontColor(), PorterDuff.Mode.SRC_ATOP);
            }
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        toolbar.setBackgroundColor(getCustomBaseColor());
    }

    private void showMPTermsAndConditions() {
        if (mProgressbar != null) {
            mProgressbar.setVisibility(View.VISIBLE);
        }
        String siteId = getIntent().getStringExtra("siteId");
        if(siteId != null) {

            mTermsAndConditionsWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    mProgressbar.setVisibility(View.GONE);
                    mMPTermsAndConditionsView.setVisibility(View.VISIBLE);
                }
            });
            if(siteId.equals("MLA")) {
                mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.ar/ayuda/terminos-y-condiciones_299");
            }
            else if (siteId.equals("MLM")){
                mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.mx/ayuda/terminos-y-condiciones_715");
            }
            else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void showBankDealsTermsAndConditions() {
        MPTextView termsAndConditions = (MPTextView) findViewById(R.id.mpsdkTermsAndConditions);
        termsAndConditions.setText(getIntent().getStringExtra("termsAndConditions"));
    }

    @Override
    protected void initializeControls() {
        mBankDealsTermsAndConditionsView = findViewById(R.id.mpsdkBankDealsTermsAndConditions);
        mProgressbar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = (WebView) findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
        initializeToolbar();
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(getIntent().getStringExtra("termsAndConditions") == null
                && getIntent().getStringExtra("siteId") == null) {
            throw new IllegalStateException("bank deal terms or site id required");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("TERMS_AND_CONDITIONS", "2", "publicKey", "MLA", "1.0", this);
        setContentView(R.layout.mpsdk_activity_terms_and_conditions);
    }
}
