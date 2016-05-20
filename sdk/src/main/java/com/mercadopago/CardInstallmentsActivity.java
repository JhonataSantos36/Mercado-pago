package com.mercadopago;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.adapters.CardInstallmentsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.Installment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentPreference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardInstallmentsActivity extends ShowCardActivity {

    //InstallmentsContainer
    private RecyclerView mInstallmentsView;
    private CardInstallmentsAdapter mInstallmentsAdapter;


    //Local vars
    private List<PayerCost> mPayerCosts;
    private PayerCost mSelectedPayerCost;
    private PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
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
        setContentView(R.layout.activity_new_installments);
    }

    protected void setLayout() {
        mInstallmentsView = (RecyclerView) findViewById(R.id.activity_installments_view);
        mCardContainer = (FrameLayout) findViewById(R.id.activity_new_card_container);
    }

    protected void initializeAdapter() {
        mInstallmentsAdapter = new CardInstallmentsAdapter(this);
        initializeAdapterListener(mInstallmentsAdapter, mInstallmentsView);
    }

    protected void onItemSelected(View view, int position) {
        mSelectedPayerCost = mPayerCosts.get(position);
    }

    protected void initializeToolbar() {
        super.initializeToolbarWithTitle(getString(R.string.mpsdk_card_installments_title));
    }


    @Override
    protected void initializeCard() {
        super.initializeCard();

        if (mPayerCosts == null) {
            getInstallmentsAsync();
        } else {
            initializeInstallments();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        mPayerCosts = (ArrayList<PayerCost>)getIntent().getSerializableExtra("payerCosts");
        mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    private void getInstallmentsAsync() {
        mMercadoPago.getInstallments(mBin, mAmount, mSelectedIssuer.getId(), mCurrentPaymentMethod.getId(),
                new Callback<List<Installment>>() {
                    @Override
                    public void success(List<Installment> installments, Response response) {
                        if (installments.size() == 0) {
                            //TODO error
                        } else if (installments.size() == 1) {
                            resolvePayerCosts(installments.get(0).getPayerCosts());
                        } else if (installments.size() > 1) {
                            //TODO error
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //TODO manejar el error
                    }
                });
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCosts = mPaymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            mSelectedPayerCost = defaultPayerCost;
            finishWithResult();
        } else if(mPayerCosts.isEmpty()) {
            //TODO tirarle error
        } else if (mPayerCosts.size() == 1) {
            mSelectedPayerCost = payerCosts.get(0);
            finishWithResult();
        } else if (payerCosts.size() > 1) {
            initializeInstallments();
        }
    }

    @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", mSelectedPayerCost);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
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

    private void initializeInstallments() {
        mInstallmentsAdapter.addResults(mPayerCosts);
    }


}
