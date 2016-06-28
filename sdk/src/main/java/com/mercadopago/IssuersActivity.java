package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IssuersAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class IssuersActivity extends ShowCardActivity {

    //IssuersContainer
    private RecyclerView mIssuersView;
    private IssuersAdapter mIssuersAdapter;
    private ProgressBar mProgressBar;
    private View mCardBackground;

    //Local vars
    private List<Issuer> mIssuers;
    private FailureRecovery mFailureRecovery;
    private Activity mActivity;
    protected boolean mActiveActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        setContentView();
        mActivity = this;
        mActiveActivity = true;
        setLayout();
        initializeAdapter();
        initializeToolbar();

        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        if(mToken != null) {
            initializeFrontFragment();
        }
        else {
            hideCardLayout();
        }
    }

    private void hideCardLayout() {
        mCardBackground.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        mActiveActivity = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActiveActivity = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActiveActivity = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActiveActivity = false;
        super.onStop();
    }

    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CARD_ISSUERS", "2", mPublicKey, "MLA", "1.0", this);
        setContentView(R.layout.mpsdk_activity_new_issuers);
    }

    protected void setLayout() {
        mIssuersView = (RecyclerView) findViewById(R.id.mpsdkActivityIssuersView);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityNewCardContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);

        mCardBackground = findViewById(R.id.mpsdkCardBackground);
        if(mDecorationPreference != null && mDecorationPreference.hasColors())
        {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }
        mProgressBar.setVisibility(View.GONE);
    }

    protected void initializeAdapter() {
        mIssuersAdapter = new IssuersAdapter(this);
        initializeAdapterListener(mIssuersAdapter, mIssuersView);
    }

    protected void onItemSelected(View view, int position) {
        mSelectedIssuer = mIssuers.get(position);
    }

    protected void initializeToolbar() {
        if(mToken != null) {
            super.initializeToolbar("", true);
        } else {
            super.initializeToolbar(getString(R.string.mpsdk_card_issuers_title), false);
        }
    }

    @Override
    protected void initializeCard() {
        super.initializeCard();

        if (mIssuers == null) {
            getIssuersAsync();
        } else {
            initializeIssuers();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        try {
            Type listType = new TypeToken<List<Issuer>>(){}.getType();
            mIssuers =  JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("issuers"), listType);
        } catch (Exception ex) {
            mIssuers = null;
        }
    }

    protected void getIssuersAsync() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMercadoPago.getIssuers(mCurrentPaymentMethod.getId(), mBin,
                new Callback<List<Issuer>>() {
                    @Override
                    public void success(List<Issuer> issuers) {
                        mIssuers = issuers;
                        MPTracker.getInstance().trackEvent("CARD_ISSUERS", "GET_ISSUERS_RESPONSE", "SUCCESS", mPublicKey, "MLA", "1.0", mActivity);
                        if (mActiveActivity) {
                            mProgressBar.setVisibility(View.GONE);
                            if (mIssuers.isEmpty()) {
                                mSelectedIssuer = null;
                                finishWithResult();
                            } else if (mIssuers.size() == 1) {
                                mSelectedIssuer = mIssuers.get(0);
                                finishWithResult();
                            } else {
                                initializeIssuers();
                            }
                        }
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        MPTracker.getInstance().trackEvent("CARD_ISSUERS", "GET_ISSUERS_RESPONSE", "FAIL", mPublicKey, "MLA", "1.0", mActivity);
                            if (mActiveActivity) {
                                mProgressBar.setVisibility(View.GONE);
                                mFailureRecovery = new FailureRecovery() {
                                    @Override
                                    public void recover() {
                                        getIssuersAsync();
                                    }
                                };
                                ApiUtil.showApiExceptionError(mActivity, apiException);
                            }
                        }
                    }

                    );
                }

        @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void initializeIssuers() {
        mIssuersAdapter.addResults(mIssuers);
    }

    protected void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onItemSelected(view, position);
                        finishWithResult();
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CARD_ISSUERS", "BACK_PRESSED", "2", mPublicKey, "MLA", "1.0", this);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                recoverFromFailure();
            }
            else {
                setResult(resultCode, data);
                finish();
            }
        }
    }
    private void recoverFromFailure() {
        if(mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
