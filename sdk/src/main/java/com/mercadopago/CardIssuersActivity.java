package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.adapters.CardIssuersAdapter;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.Issuer;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardIssuersActivity extends ShowCardActivity {

    //IssuersContainer
    private RecyclerView mIssuersView;
    private CardIssuersAdapter mIssuersAdapter;

    //Local vars
    private List<Issuer> mIssuers;
    private FailureRecovery mFailureRecovery;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        mActivity = this;
        setLayout();
        initializeAdapter();
        getActivityParameters();
        initializeToolbar();

        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        initializeFrontFragment();
    }

    protected void setContentView() {
        setContentView(R.layout.activity_new_issuers);
    }

    protected void setLayout() {
        mIssuersView = (RecyclerView) findViewById(R.id.activity_issuers_view);
        mCardContainer = (FrameLayout) findViewById(R.id.activity_new_card_container);
    }

    protected void initializeAdapter() {
        mIssuersAdapter = new CardIssuersAdapter(this);
        initializeAdapterListener(mIssuersAdapter, mIssuersView);
    }

    protected void onItemSelected(View view, int position) {
        mSelectedIssuer = mIssuers.get(position);
    }

    protected void initializeToolbar() {
        super.initializeToolbarWithTitle(getString(R.string.mpsdk_card_issuers_title));
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
        mIssuers = (ArrayList<Issuer>)getIntent().getSerializableExtra("issuers");
    }

    protected void getIssuersAsync() {
        mMercadoPago.getIssuers(mCurrentPaymentMethod.getId(), mBin,
                new Callback<List<Issuer>>() {
                    @Override
                    public void success(List<Issuer> issuers, Response response) {
                        if (issuers.isEmpty()) {
                            ErrorUtil.startErrorActivity(mActivity, getString(R.string.mpsdk_standard_error_message), "no issuers found at CardIssuersActivity", false);
                        } else if (issuers.size() == 1) {
                            mSelectedIssuer = issuers.get(0);
                            finishWithResult();
                        } else {
                            initializeIssuers();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mFailureRecovery = new FailureRecovery() {
                            @Override
                            public void recover() {
                                getIssuersAsync();
                            }
                        };
                        ApiUtil.showApiExceptionError(mActivity, error);
                    }
                });
    }

    @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("issuer", mSelectedIssuer);
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
