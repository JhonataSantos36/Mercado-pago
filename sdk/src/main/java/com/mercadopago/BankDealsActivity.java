package com.mercadopago;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.BankDealsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.List;


public class BankDealsActivity extends MercadoPagoActivity {

    // Local vars
    protected MercadoPago mMercadoPago;
    protected RecyclerView mRecyclerView;
    protected DecorationPreference mDecorationPreference;
    protected Toolbar mToolbar;

    @Override
    protected void onValidStart() {

        mMercadoPago = new MercadoPago.Builder()
                .setContext(getActivity())
                .setPublicKey(getMerchantPublicKey())
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
        decorate(title);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(getMerchantPublicKey() == null) {
            throw new IllegalStateException("public key not set");
        }
    }

    public void refreshLayout(View view) {
        getBankDeals();
    }

    private void getBankDeals() {
        LayoutUtil.showProgressLayout(this);
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(List<BankDeal> bankDeals) {
                MPTracker.getInstance().trackScreen("BANK_DEALS", "2", getMerchantPublicKey(), "MLA", "1.0", getActivity());
                MPTracker.getInstance().trackEvent("BANK_DEALS", "GET_BANK_DEALS_RESPONSE", "SUCCESS", "2", mMerchantAccessToken, "MLA", "1.0", getActivity());
                if (isActivityActive()) {
                    mRecyclerView.setAdapter(new BankDealsAdapter(getActivity(), bankDeals, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            BankDeal selectedBankDeal = (BankDeal) view.getTag();
                            Intent intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
                            intent.putExtra("termsAndConditions", selectedBankDeal.getLegals());
                            startActivity(intent);
                        }
                    }));

                    LayoutUtil.showRegularLayout(getActivity());
                }
            }

            @Override
            public void failure(ApiException apiException) {
                MPTracker.getInstance().trackEvent("BANK_DEALS", "GET_BANK_DEALS_RESPONSE", "FAIL", "2", getMerchantPublicKey(), "MLA", "1.0", getActivity());
                if (isActivityActive()) {
                    ApiUtil.finishWithApiException(getActivity(), apiException);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            finish();
        }
    }
}
