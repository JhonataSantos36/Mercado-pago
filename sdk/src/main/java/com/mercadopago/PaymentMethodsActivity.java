package com.mercadopago;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.presenters.PaymentMethodsPresenter;
import com.mercadopago.providers.PaymentMethodsProvider;
import com.mercadopago.providers.PaymentMethodsProviderImpl;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.PaymentMethodsView;

import java.lang.reflect.Type;
import java.util.List;


public class PaymentMethodsActivity extends MercadoPagoBaseActivity implements PaymentMethodsView {

    protected DecorationPreference mDecorationPreference;
    protected String mMerchantPublicKey;
    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;
    protected TextView mBankDealsTextView;
    protected TextView mTitle;

    private Activity mActivity;
    private PaymentMethodsPresenter mPresenter;
    private PaymentMethodsProvider mResourcesProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new PaymentMethodsPresenter();

        try {
            getActivityParameters();
            mResourcesProvider = new PaymentMethodsProviderImpl(this, mMerchantPublicKey);
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void getActivityParameters() {

        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        mPresenter.setPaymentPreference(paymentPreference);

        Boolean showBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        mPresenter.setShowBankDeals(showBankDeals);

        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            List<String> supportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
            mPresenter.setSupportedPaymentTypes(supportedPaymentTypes);
        }
    }

    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_payment_methods);
    }

    protected void initializeControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkPaymentMethodsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeToolbar();
    }

    protected void onValidStart() {
        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(mResourcesProvider);

        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        setContentView();
        initializeControls();

        mActivity = this;

        mPresenter.start();
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false, mMerchantPublicKey);
    }

    private void initializeToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        mBankDealsTextView = (TextView) findViewById(R.id.mpsdkBankDeals);
        mTitle = (TextView) findViewById(R.id.mpsdkToolbarTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        decorate(mToolbar);
        decorateFont(mTitle);
    }

    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    protected void recoverFromFailure() {
        mPresenter.recoverFromFailure();
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

    protected void decorateFont(TextView textView) {
        if (textView != null && isDarkFontEnabled()) {
            textView.setTextColor(getDarkFontColor());
        }
    }

    @Override
    public void showPaymentMethods(List<PaymentMethod> paymentMethods) {
        mRecyclerView.setAdapter(new PaymentMethodsAdapter(mActivity, paymentMethods, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to parent
                Intent returnIntent = new Intent();
                PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(selectedPaymentMethod));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    @Override
    public void showProgress() {
        LayoutUtil.showProgressLayout(mActivity);
    }

    @Override
    public void hideProgress() {
        LayoutUtil.showRegularLayout(mActivity);
    }

    @Override
    public void showError(MercadoPagoError exception) {
        ErrorUtil.startErrorActivity(this, exception, mMerchantPublicKey);
    }

    @Override
    public void showBankDeals() {
        decorateFont(mBankDealsTextView);
        mBankDealsTextView.setVisibility(View.VISIBLE);
        mBankDealsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MercadoPagoComponents.Activities.BankDealsActivityBuilder()
                        .setActivity(mActivity)
                        .setMerchantPublicKey(mMerchantPublicKey)
                        .setDecorationPreference(mDecorationPreference)
                        .startActivity();
            }
        });
    }

    protected void decorate(Toolbar toolbar) {
        if (toolbar != null) {
            if (isCustomColorSet()) {
                toolbar.setBackgroundColor(getCustomBaseColor());
            }
            decorateUpArrow(toolbar);
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

