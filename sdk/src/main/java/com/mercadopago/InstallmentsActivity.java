package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PayerCostsAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.InstallmentsPresenter;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.InstallmentsActivityView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsActivity extends AppCompatActivity implements InstallmentsActivityView {

    protected InstallmentsPresenter mPresenter;
    protected Activity mActivity;

    //View controls
    protected PayerCostsAdapter mPayerCostsAdapter;
    protected RecyclerView mInstallmentsRecyclerView;
    protected ProgressBar mProgressBar;
    protected DecorationPreference mDecorationPreference;
    //ViewMode
    protected boolean mLowResActive;
    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;
    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new InstallmentsPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        analyzeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }
    
    private void getActivityParameters() {
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        String publicKey = getIntent().getStringExtra("merchantPublicKey");
        Token token = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("token"), Token.class);
        Card card = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        Issuer issuer = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("issuer"), Issuer.class);
        BigDecimal amount = null;
        if (this.getIntent().getStringExtra("amount") != null) {
            amount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        }

        Site site = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);
        List<PayerCost> payerCosts;
        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("payerCosts"), listType);
        } catch (Exception ex) {
            payerCosts = null;
        }
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        mDecorationPreference = null;
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }

        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setToken(token);
        mPresenter.setCard(card);
        mPresenter.setIssuer(issuer);
        mPresenter.setAmount(amount);
        mPresenter.setSite(site);
        mPresenter.setPayerCosts(payerCosts);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setCardInformation();
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    public void analyzeLowRes() {
        if (mPresenter.isCardInfoAvailable()) {
            this.mLowResActive = ScaleUtil.isLowRes(this);
        } else {
            this.mLowResActive = true;
        }
    }

    public void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    public void setContentView() {
        MPTracker.getInstance().trackScreen("CARD_INSTALLMENTS", "2", mPresenter.getPublicKey(),
                mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    public void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_installments_lowres);
    }

    public void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_installments_normal);
    }

    @Override
    public void onValidStart() {
        mPresenter.initializeMercadoPago();
        initializeViews();
        loadViews();
        hideHeader();
        decorate();
        initializeAdapter();
        mPresenter.loadPayerCosts();
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initializeViews() {
        mInstallmentsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityInstallmentsView);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
            mAppBar = (AppBarLayout) findViewById(R.id.mpsdkInstallmentesAppBar);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    public void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_card_installments_title));
    }

    public void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(getString(R.string.mpsdk_card_installments_title));
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInformation() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInformation().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
    }

    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void hideHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.GONE);
        } else {
            mNormalToolbar.setTitle("");
        }
    }

    @Override
    public void showHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar.setTitle(getString(R.string.mpsdk_card_installments_title));
        }
    }

    private void decorate() {
        if (isDecorationEnabled()) {
            if (mLowResActive) {
                decorateLowRes();
            } else {
                decorateNormal();
            }
        }
    }

    private void decorateLowRes() {
        ColorsUtil.decorateLowResToolbar(mLowResToolbar, mLowResTitleToolbar, mDecorationPreference,
                getSupportActionBar(), this);
    }

    private void decorateNormal() {
        ColorsUtil.decorateNormalToolbar(mNormalToolbar, mDecorationPreference, mAppBar,
                mCollapsingToolbar, getSupportActionBar(), this);
        mFrontCardView.decorateCardBorder(mDecorationPreference.getLighterColor());
    }

    private void initializeAdapter() {
        mPayerCostsAdapter = new PayerCostsAdapter(this, mPresenter.getSite().getCurrencyId(), getDpadSelectionCallback());
        initializeAdapterListener(mPayerCostsAdapter, mInstallmentsRecyclerView);
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
            new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mPresenter.onItemSelected(position);
                }
            }));
    }

    protected OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                mPresenter.onItemSelected(position);
            }
        };
    }

    @Override
    public void initializeInstallments(List<PayerCost> payerCostList) {
        mPayerCostsAdapter.addResults(payerCostList);
    }

    @Override
    public void showLoadingView() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingView() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void finishWithResult(PayerCost payerCost) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CARD_INSTALLMENTS", "BACK_PRESSED", "2",
                mPresenter.getPublicKey(), mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
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
                mPresenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }
}
