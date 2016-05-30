package com.mercadopago;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mercadopago.views.MPTextView;

public class TermsAndConditionsActivity extends AppCompatActivity {

    protected View mMPTermsAndConditionsView;
    protected View mBankDealsTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        MPTextView termsAndConditions = (MPTextView) findViewById(R.id.termsAndConditions);
        termsAndConditions.setText(getIntent().getStringExtra("termsAndConditions"));
    }

    private void initializeControls() {
        mBankDealsTermsAndConditionsView = findViewById(R.id.bankDealsTermsAndConditions);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mMPTermsAndConditionsView = findViewById(R.id.MPTermsAndConditions);
        mTermsAndConditionsWebView = (WebView) findViewById(R.id.termsAndConditionsWebView);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_terms_and_conditions);
    }
}
