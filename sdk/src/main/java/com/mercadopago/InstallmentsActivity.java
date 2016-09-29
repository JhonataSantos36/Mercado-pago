package com.mercadopago;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PayerCostsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class InstallmentsActivity extends ShowCardActivity {

    //InstallmentsContainer
    protected RecyclerView mInstallmentsView;
    protected PayerCostsAdapter mPayerCostsAdapter;
    protected ProgressBar mProgressBar;
    protected View mCardBackground;

    //Local vars
    protected List<PayerCost> mPayerCosts;
    protected PayerCost mSelectedPayerCost;
    protected PaymentMethod mPaymentMethod;
    protected PaymentPreference mPaymentPreference;
    protected Site mSite;
    protected MercadoPago mMercadoPago;
    protected String mPublicKey;
    protected BigDecimal mAmount;
    protected Issuer mSelectedIssuer;
    protected Card mCard;
    protected Token mToken;


    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CARD_INSTALLMENTS", 2, mPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_new_installments);
    }

    @Override
    protected void initializeControls() {
        mInstallmentsView = (RecyclerView) findViewById(R.id.mpsdkActivityInstallmentsView);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityNewCardContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mCardBackground = findViewById(R.id.mpsdkCardBackground);

        if (isCustomColorSet()) {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void initializeFragments(Bundle savedInstanceState) {
        super.initializeFragments(savedInstanceState);
    }

    @Override
    protected void hideCardLayout() {
        mCardBackground.setVisibility(View.GONE);
    }

    @Override
    protected void onValidStart() {
        setCardInformation();
        setPaymentMethod(mPaymentMethod);
        if (isCardInfoAvailable()) {
            initializeFrontFragment();
            super.initializeCard();
        } else {
            hideCardLayout();
        }
        initializeAdapter();
        initializeToolbar();
        showPayerCosts();
    }

    private void setCardInformation() {
        if(mCard == null && mToken != null) {
            setCardInformation(mToken);
        } else if (mCard != null) {
            setCardInformation(mCard);
        }
    }

    private boolean werePayerCostsSet() {
        return mPayerCosts != null;
    }

    @Override
    protected void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    protected OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                onItemSelected(position);
            }
        };
    }

    protected void initializeAdapter() {
        mPayerCostsAdapter = new PayerCostsAdapter(this, mSite.getCurrencyId(), getDpadSelectionCallback());
        initializeAdapterListener(mPayerCostsAdapter, mInstallmentsView);
    }

    protected void onItemSelected(int position) {
        mSelectedPayerCost = mPayerCosts.get(position);
        finishWithResult();
    }

    protected void initializeToolbar() {
        if (isCardInfoAvailable()) {
            super.initializeToolbar("", true);
        } else {
            super.initializeToolbar(getString(R.string.mpsdk_card_installments_title), false);
        }
    }

    protected void showPayerCosts() {

        if (werePayerCostsSet()) {
            resolvePayerCosts(mPayerCosts);
        } else {
            getInstallmentsAsync();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void getActivityParameters() {
        mPublicKey = this.getIntent().getStringExtra("publicKey");
        if (this.getIntent().getStringExtra("amount") != null) {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        }
        mPaymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mSelectedIssuer = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("issuer"), Issuer.class);
        mSite = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);
        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            mPayerCosts = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("payerCosts"), listType);
        } catch (Exception ex) {
            mPayerCosts = null;
        }
        mPaymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
        mCard = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        mToken = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("token"), Token.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mAmount == null || mSite == null) {
            throw new IllegalStateException();
        }
        if (mPayerCosts == null) {
            if (mSelectedIssuer == null) {
                throw new IllegalStateException("issuer is null");
            }
            if (mPublicKey == null) {
                throw new IllegalStateException("public key not set");
            }
            if (mPaymentMethod == null) {
                throw new IllegalStateException("payment method is null");
            }
        }
    }

    private void getInstallmentsAsync() {

        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        mProgressBar.setVisibility(View.VISIBLE);
        Long issuerId = mSelectedIssuer == null ? null : mSelectedIssuer.getId();
        String bin = "";
        if (getCardInformation() != null) {
            bin = getCardInformation().getFirstSixDigits() == null ? "" : getCardInformation().getFirstSixDigits();
        }
        mMercadoPago.getInstallments(bin, mAmount, issuerId, mPaymentMethod.getId(),
                new Callback<List<Installment>>() {
                    @Override
                    public void success(List<Installment> installments) {
                        if (isActivityActive()) {
                            mProgressBar.setVisibility(View.GONE);
                            if (installments.size() == 0) {
                                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "no installments found for an issuer at InstallmentsActivity", false);
                            } else if (installments.size() == 1) {
                                resolvePayerCosts(installments.get(0).getPayerCosts());
                            } else {
                                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "multiple installments found for an issuer at InstallmentsActivity", false);
                            }
                        }
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        if (isActivityActive()) {
                            mProgressBar.setVisibility(View.GONE);
                            setFailureRecovery(new FailureRecovery() {
                                @Override
                                public void recover() {
                                    getInstallmentsAsync();
                                }
                            });
                            ApiUtil.showApiExceptionError(getActivity(), apiException);
                        }
                    }
                });
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCosts = mPaymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            mSelectedPayerCost = defaultPayerCost;
            finishWithResult();
        } else if (mPayerCosts.isEmpty()) {
            ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "no payer costs found at InstallmentsActivity", false);
        } else if (mPayerCosts.size() == 1) {
            mSelectedPayerCost = payerCosts.get(0);
            finishWithResult();
        } else {
            initializeInstallments();
        }
    }

    @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mSelectedPayerCost));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CARD_INSTALLMENTS", "BACK_PRESSED", 2, mPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);

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
                        onItemSelected(position);
                    }
                }));
    }

    private void initializeInstallments() {
        mPayerCostsAdapter.addResults(mPayerCosts);
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

    @Override
    public void initializeCardByToken() {

    }

    @Override
    public IdentificationType getCardIdentificationType() {
        return null;
    }

    @Override
    public PaymentMethod getCurrentPaymentMethod() {
        return mPaymentMethod;
    }
}