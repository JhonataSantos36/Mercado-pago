package com.mercadopago;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.views.MPTextView;

public class TermsAndConditionsActivity extends AppCompatActivity {

    protected View mMPTermsAndConditionsView;
    protected View mBankDealsTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ProgressBar mProgressbar;
    protected DecorationPreference mDecorationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(this.getIntent().getSerializableExtra("decorationPreference") != null) {
            mDecorationPreference = (DecorationPreference) this.getIntent().getSerializableExtra("decorationPreference");
        }
        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        setContentView();
        initializeControls();
        initializeToolbar();

        // Set terms and conditions
        if (getIntent().getStringExtra("termsAndConditions") != null) {
            mMPTermsAndConditionsView.setVisibility(View.GONE);
            showBankDealsTermsAndConditions();
        }
        else
        {
            mBankDealsTermsAndConditionsView.setVisibility(View.GONE);
            showMPTermsAndConditions();
        }
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
        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                if(toolbar != null) {
                    decorateToolbar(toolbar);
                }
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                TextView title = (TextView) findViewById(R.id.mpsdkTitle);
                title.setTextColor(mDecorationPreference.getDarkFontColor(this));
            }
        }
    }

    private void decorateToolbar(Toolbar toolbar) {
        if(mDecorationPreference.isDarkFontEnabled()) {
            Drawable upArrow = toolbar.getNavigationIcon();
            if(upArrow != null) {
                upArrow.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
            }
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        toolbar.setBackgroundColor(mDecorationPreference.getLighterColor());
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

    private void initializeControls() {
        mBankDealsTermsAndConditionsView = findViewById(R.id.mpsdkBankDealsTermsAndConditions);
        mProgressbar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = (WebView) findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_terms_and_conditions);
    }
}
