package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsActivity extends AppCompatActivity {

    protected MercadoPago mMercadoPago;
    private String mMerchantPublicKey;
    private boolean mShowBankDeals;

    private PaymentPreference mPaymentPreference;
    private DecorationPreference mDecorationPreference;

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mBankDealsTextView;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        setContentView();
        mActivity = this;
        initializeToolbar();
        mMercadoPago = createMercadoPago(mMerchantPublicKey);

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkPaymentMethodsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load payment methods
        getPaymentMethodsAsync();

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

        if(mShowBankDeals) {
            mBankDealsTextView.setVisibility(View.VISIBLE);
            mBankDealsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(mActivity)
                            .setPublicKey(mMerchantPublicKey)
                            .setDecorationPreference(mDecorationPreference)
                            .startBankDealsActivity();
                }
            });
        }

        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                mToolbar.setBackgroundColor(mDecorationPreference.getBaseColor());
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                mTitle.setTextColor(mDecorationPreference.getDarkFontColor(this));
                Drawable upArrow = mToolbar.getNavigationIcon();
                upArrow.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                if(mShowBankDeals) {
                    mBankDealsTextView.setTextColor(mDecorationPreference.getDarkFontColor(this));
                }
            }
        }
    }

    protected MercadoPago createMercadoPago(String publicKey) {
        return new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKey)
                .build();
    }

    private void getActivityParameters() {

        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        if (mMerchantPublicKey == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }
        if (this.getIntent().getSerializableExtra("paymentPreference") != null) {
            mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        }
        if (this.getIntent().getSerializableExtra("decorationPreference") != null) {
            mDecorationPreference = (DecorationPreference) this.getIntent().getSerializableExtra("decorationPreference");
        }
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
    }


    protected void setContentView() {
        setContentView(R.layout.activity_payment_methods);
    }

    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {
        getPaymentMethodsAsync();
    }

    private void getPaymentMethodsAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                mRecyclerView.setAdapter(new PaymentMethodsAdapter(mActivity, getSupportedPaymentMethods(paymentMethods), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Return to parent
                        Intent returnIntent = new Intent();
                        PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                        returnIntent.putExtra("paymentMethod", selectedPaymentMethod);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }));
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {

                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
    }

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods;
        if(mPaymentPreference != null) {
            supportedPaymentMethods = mPaymentPreference.getSupportedPaymentMethods(paymentMethods);
            supportedPaymentMethods = getPaymentMethodsOfType(mPaymentPreference.getDefaultPaymentTypeId(), supportedPaymentMethods);
        }
        else {
            supportedPaymentMethods = paymentMethods;
        }
        return supportedPaymentMethods;
    }

    private List<PaymentMethod> getPaymentMethodsOfType(String paymentTypeId, List<PaymentMethod> paymentMethodList) {

        if(paymentMethodList != null && paymentTypeId != null && !paymentTypeId.isEmpty()) {

            List<PaymentMethod> validPaymentMethods = new ArrayList<>();

            for (PaymentMethod currentPaymentMethod : paymentMethodList) {
                if(currentPaymentMethod.getPaymentTypeId().equals(paymentTypeId)) {
                    validPaymentMethods.add(currentPaymentMethod);
                }
            }
            return validPaymentMethods;
        } else {
            return paymentMethodList;
        }
    }
}

