package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IssuersAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class IssuersActivity extends ShowCardActivity {

    //Local vars
    protected List<Issuer> mIssuers;
    protected PaymentMethod mCurrentPaymentMethod;
    protected MercadoPago mMercadoPago;
    protected CardInformation mCardInfo;
    protected String mPublicKey;
    protected Issuer mSelectedIssuer;

    //IssuersContainer
    private RecyclerView mIssuersView;
    private IssuersAdapter mIssuersAdapter;
    private ProgressBar mProgressBar;
    private View mCardBackground;
    protected Card mCard;
    protected Token mToken;

    @Override
    protected void hideCardLayout() {
        mCardBackground.setVisibility(View.GONE);
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CARD_ISSUERS", 2, mPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_new_issuers);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mCurrentPaymentMethod == null || mPublicKey == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    protected void initializeControls() {
        mIssuersView = (RecyclerView) findViewById(R.id.mpsdkActivityIssuersView);
        mIssuersView = (RecyclerView) findViewById(R.id.mpsdkActivityIssuersView);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityNewCardContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);

        mCardBackground = findViewById(R.id.mpsdkCardBackground);
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void initializeFragments(Bundle savedInstanceState) {
        super.initializeFragments(savedInstanceState);
    }

    @Override
    protected void onValidStart() {
        setCardInformation();
        setPaymentMethod(mCurrentPaymentMethod);
        if (isCardInfoAvailable()) {
            initializeFrontFragment();
            super.initializeCard();
        } else {
            hideCardLayout();
        }
        initializeAdapter();
        initializeToolbar();
        showIssuers();
    }

    private void setCardInformation() {
        if(mCard == null && mToken != null) {
            setCardInformation(mToken);
        } else if (mCard != null) {
            setCardInformation(mCard);
        }
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
        mIssuersAdapter = new IssuersAdapter(this, getDpadSelectionCallback());
        initializeAdapterListener(mIssuersAdapter, mIssuersView);
    }

    protected void onItemSelected(int position) {
        mSelectedIssuer = mIssuers.get(position);
        finishWithResult();
    }

    protected void initializeToolbar() {
        if (isCardInfoAvailable()) {
            super.initializeToolbar("", true);
        } else {
            super.initializeToolbar(getString(R.string.mpsdk_card_issuers_title), false);
        }
    }

    protected void showIssuers() {
        if (mIssuers == null) {
            getIssuersAsync();
        } else {
            resolveIssuersList();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void getActivityParameters() {
        mPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        try {
            Type listType = new TypeToken<List<Issuer>>() {
            }.getType();
            mIssuers = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("issuers"), listType);
        } catch (Exception ex) {
            mIssuers = null;
        }
        mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        mCard = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        mToken = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("token"), Token.class);
    }
    protected void getIssuersAsync() {
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        mProgressBar.setVisibility(View.VISIBLE);
        String bin = "";
        if(mCardInfo != null) {
            bin = mCardInfo.getFirstSixDigits() == null ? "" : mCardInfo.getFirstSixDigits();
        }
        mMercadoPago.getIssuers(mCurrentPaymentMethod.getId(), bin,
                new Callback<List<Issuer>>() {
                    @Override
                    public void success(List<Issuer> issuers) {
                        mIssuers = issuers;
                        if (isActivityActive()) {
                            mProgressBar.setVisibility(View.GONE);
                            resolveIssuersList();
                        }
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        if (isActivityActive()) {
                            mProgressBar.setVisibility(View.GONE);
                            setFailureRecovery(new FailureRecovery() {
                                @Override
                                public void recover() {
                                    getIssuersAsync();
                                }
                            });
                            ApiUtil.showApiExceptionError(getActivity(), apiException);
                        }
                    }
                });
    }

    protected void resolveIssuersList() {
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
                        onItemSelected(position);
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CARD_ISSUERS", "BACK_PRESSED", 2, mPublicKey, BuildConfig.VERSION_NAME, this);

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

    @Override
    public void initializeCardByToken() {

    }

    @Override
    public IdentificationType getCardIdentificationType() {
        return null;
    }

    @Override
    public PaymentMethod getCurrentPaymentMethod() {
        return mCurrentPaymentMethod;
    }
}
