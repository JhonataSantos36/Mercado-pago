package com.mercadopago;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mercadopago.constants.Sites;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.ErrorUtil;

import static android.text.TextUtils.isEmpty;

public class TermsAndConditionsActivity extends MercadoPagoActivity {

    public static final String EXTRA_SITE_ID = "siteId";
    public static final String EXTRA_BANK_DEAL_LEGALS = "bankDealLegals";
    protected View mMPTermsAndConditionsView;
    protected View mBankDealsTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ViewGroup mProgressLayout;
    protected MPTextView mBankDealsLegalsTextView;
    protected Toolbar mToolbar;
    protected TextView mTitle;

    protected String mBankDealsTermsAndConditions;
    protected String mSiteId;

    public static void startWithBankDealLegals(final Context context, final String bankDealLegals) {
        Intent intent = new Intent(context, TermsAndConditionsActivity.class);
        intent.putExtra(EXTRA_BANK_DEAL_LEGALS, bankDealLegals);
        context.startActivity(intent);
    }

    public static void start(final Context context, final String siteId) {
        Intent intent = new Intent(context, TermsAndConditionsActivity.class);
        intent.putExtra(EXTRA_SITE_ID, siteId);
        context.startActivity(intent);
    }

    @Override
    protected void getActivityParameters() {
        mBankDealsTermsAndConditions = getIntent().getStringExtra(EXTRA_BANK_DEAL_LEGALS);
        mSiteId = getIntent().getStringExtra(EXTRA_SITE_ID);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mBankDealsTermsAndConditions == null
                && mSiteId == null) {
            throw new IllegalStateException("bank deal terms or site id required");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_terms_and_conditions);
    }

    @Override
    protected void initializeControls() {
        mBankDealsTermsAndConditionsView = findViewById(R.id.mpsdkBankDealsTermsAndConditions);
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mBankDealsLegalsTextView = findViewById(R.id.mpsdkTermsAndConditions);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
        initializeToolbar();
    }


    private void initializeToolbar() {
        mToolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        mTitle = findViewById(R.id.mpsdkTitle);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onValidStart() {
        if (!isEmpty(mBankDealsTermsAndConditions)) {
            mMPTermsAndConditionsView.setVisibility(View.GONE);
            showBankDealsTermsAndConditions();
        } else if (!isEmpty(mSiteId)) {
            mBankDealsTermsAndConditionsView.setVisibility(View.GONE);
            showMPTermsAndConditions();
        }
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), message, false, "");
    }

    private void showMPTermsAndConditions() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mTermsAndConditionsWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressLayout.setVisibility(View.GONE);
                mMPTermsAndConditionsView.setVisibility(View.VISIBLE);
            }
        });
        if (Sites.ARGENTINA.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.ar/ayuda/terminos-y-condiciones_299");
        } else if (Sites.MEXICO.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.mx/ayuda/terminos-y-condiciones_715");
        } else if (Sites.BRASIL.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.br/ajuda/termos-e-condicoes_300");
        } else if (Sites.CHILE.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.cl/ayuda/terminos-y-condiciones_299");
        } else if (Sites.VENEZUELA.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.ve/ayuda/terminos-y-condiciones_299");
        } else if (Sites.PERU.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.pe/ayuda/terminos-condiciones-uso_2483");
        } else if (Sites.COLOMBIA.getId().equals(mSiteId)) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.co/ayuda/terminos-y-condiciones_299");
        } else {
            finish();
        }
    }

    private void showBankDealsTermsAndConditions() {
        mBankDealsLegalsTextView.setText(mBankDealsTermsAndConditions);
    }
}
