package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.BankDealsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
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
    protected MercadoPagoServicesAdapter mMercadoPago;
    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;

    protected List<BankDeal> mBankDeals;
    protected String mPayerAccessToken;

    @Override
    protected void onValidStart() {
        trackInitialScreen();

        mMercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(getActivity())
                .setPublicKey(mMerchantPublicKey)
                .setPrivateKey(mPayerAccessToken)
                .build();

        getBankDeals();
    }

    protected void trackInitialScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mMerchantPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .setTrackingStrategy(TrackingUtil.BATCH_STRATEGY)
                .build();
        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_BANK_DEALS)
                .setScreenName(TrackingUtil.SCREEN_NAME_BANK_DEALS)
                .build();

        mpTrackingContext.trackEvent(event);
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false, mMerchantPublicKey);
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
        mPayerAccessToken = getIntent().getStringExtra("payerAccessToken");
        try {
            Type listType = new TypeToken<List<BankDeal>>() {
            }.getType();
            mBankDeals = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("bankDeals"), listType);
        } catch (Exception ex) {
            mBankDeals = null;
        }
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);

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
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
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
                        ApiUtil.showApiExceptionError(getActivity(), apiException, mMerchantPublicKey, ApiUtil.RequestOrigin.GET_BANK_DEALS);
                    } else {
                        finishWithCancelResult();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recoverFromFailure();
            } else {
                finishWithCancelResult();
            }
        }
    }

    private void finishWithCancelResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    protected OnSelectedCallback<View> getDpadSelectionCallback() {
        return new OnSelectedCallback<View>() {
            @Override
            public void onSelected(View view) {
                showSelectedBankDealTerms(view);
            }
        };
    }

    private void showSelectedBankDealTerms(View view) {
        BankDeal selectedBankDeal = (BankDeal) view.getTag();
        Intent intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
        intent.putExtra("bankDealLegals", selectedBankDeal.getLegals());
        startActivity(intent);
    }

    protected void solveBankDeals(List<BankDeal> bankDeals) {
        mRecyclerView.setAdapter(new BankDealsAdapter(getActivity(), bankDeals, getDpadSelectionCallback(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectedBankDealTerms(view);
            }
        }));

        LayoutUtil.showRegularLayout(getActivity());
    }
}
