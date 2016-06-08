package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.BankDealsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import com.mercadopago.callbacks.Callback;

import java.util.List;


public class BankDealsActivity extends AppCompatActivity {

    // Activity parameters
    protected String mPublicKey;

    // Local vars
    protected Activity mActivity;
    protected boolean mActiveActivity;
    protected MercadoPago mMercadoPago;
    protected RecyclerView mRecyclerView;
    protected DecorationPreference mDecorationPreference;
    protected Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Set activity
        mActivity = this;
        mActiveActivity = true;

        // Get activity parameters
        mPublicKey = mActivity.getIntent().getStringExtra("publicKey");
        mDecorationPreference = (DecorationPreference) getIntent().getSerializableExtra("decorationPreference");

        initializeToolbar();

        /// / Init MercadoPago object with public key
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mPublicKey)
                .build();

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkBankDealsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        // Get bank deals
        getBankDeals();
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

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(mDecorationPreference != null) {
            if(mDecorationPreference.hasColors()) {
                mToolbar.setBackgroundColor(mDecorationPreference.getBaseColor());
            }
            if(mDecorationPreference.isDarkFontEnabled()) {
                TextView title = (TextView) findViewById(R.id.mpsdkTitle);
                title.setTextColor(mDecorationPreference.getDarkFontColor(this));
            }
        }
    }

    protected void setContentView() {
        setContentView(R.layout.activity_bank_deals);
    }

    public void refreshLayout(View view) {
        getBankDeals();
    }

    private void getBankDeals() {
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(List<BankDeal> bankDeals) {
                MPTracker.getInstance().trackScreen("BANK_DEALS", "2", mPublicKey, "MLA", "1.0", mActivity);
                MPTracker.getInstance().trackEvent("BANK_DEALS", "GET_BANK_DEALS_RESPONSE", "SUCCESS", "2", mPublicKey, "MLA", "1.0", mActivity);
                if (mActiveActivity) {
                    mRecyclerView.setAdapter(new BankDealsAdapter(mActivity, bankDeals, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            BankDeal selectedBankDeal = (BankDeal) view.getTag();
                            Intent intent = new Intent(mActivity, TermsAndConditionsActivity.class);
                            intent.putExtra("termsAndConditions", selectedBankDeal.getLegals());
                            startActivity(intent);
                        }
                    }));

                    LayoutUtil.showRegularLayout(mActivity);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                MPTracker.getInstance().trackEvent("BANK_DEALS", "GET_BANK_DEALS_RESPONSE", "FAIL", "2", mPublicKey, "MLA", "1.0", mActivity);
                if (mActiveActivity) {
                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            }
        });
    }
}
