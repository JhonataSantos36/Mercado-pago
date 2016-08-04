package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.BankDealsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.util.List;


public class BankDealsActivity extends MercadoPagoActivity {

    //Activity parameters
    protected String mMerchantPublicKey;

    // Local vars
    protected MercadoPago mMercadoPago;
    protected RecyclerView mRecyclerView;
    protected DecorationPreference mDecorationPreference;
    protected Toolbar mToolbar;

    protected List<BankDeal> mBankDeals;

    @Override
    protected void onValidStart() {

        mMercadoPago = new MercadoPago.Builder()
                .setContext(getActivity())
                .setPublicKey(mMerchantPublicKey)
                .build();

        getBankDeals();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_bank_deals);
    }

    @Override
    protected void initializeControls() {
        initializeToolbar();
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkBankDealsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        try {
            Type listType = new TypeToken<List<BankDeal>>() {}.getType();
            mBankDeals = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("bankDeals"), listType);
        } catch (Exception ex) {
            mBankDeals = null;
        }
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        TextView title = (TextView) findViewById(R.id.mpsdkTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        decorateFont(title);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(mMerchantPublicKey == null) {
            throw new IllegalStateException("public key not set");
        }
    }

    private void getBankDeals() {
        if (mBankDeals != null) {
            solveBankDeals(mBankDeals);
        } else {
            LayoutUtil.showProgressLayout(this);
            mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
                @Override
                public void success(List<BankDeal> bankDeals) {
                    solveBankDeals(bankDeals);
                }

                @Override
                public void failure(ApiException apiException) {
                    if (isActivityActive()) {
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getBankDeals();
                            }
                        });
                        ApiUtil.showApiExceptionError(getActivity(), apiException);
                    } else {
                        finishWithCancelResult();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                recoverFromFailure();
            }
            else {
                finishWithCancelResult();
            }
        }
    }

    private void finishWithCancelResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    protected void solveBankDeals(List<BankDeal> bankDeals) {
        MPTracker.getInstance().trackScreen("BANK_DEALS", 2, mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());
        mRecyclerView.setAdapter(new BankDealsAdapter(getActivity(), bankDeals, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDeal selectedBankDeal = (BankDeal) view.getTag();
                Intent intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
                intent.putExtra("bankDealLegals", selectedBankDeal.getLegals());
                startActivity(intent);
            }
        }));

        LayoutUtil.showRegularLayout(getActivity());
    }
}
