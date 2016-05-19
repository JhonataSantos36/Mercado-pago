package com.mercadopago;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

public abstract class StaticFrontCardActivity extends FrontCardActivity {

    public static Integer LAST_DIGITIS_LENGTH = 4;

    protected MercadoPago mMercadoPago;

    //Card container
    protected FrameLayout mCardContainer;
    protected CardFrontFragment mFrontFragment;

    //Card data
    protected String mBin;
    protected BigDecimal mAmount;
    protected String mKey;
    protected Token mToken;
    protected String mSecurityCodeLocation;
    protected Cardholder mCardholder;

    //Local vars
    protected Issuer mSelectedIssuer;
    protected MPTextView mToolbarTitle;

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
                .setPublicKey(mKey)
                .build();

        if (mCurrentPaymentMethod == null) {
            guessPaymentMethod();
        } else {
            initializeCard();
            initializeFrontFragment();
        }
    }

    protected abstract void setContentView();
    protected abstract void setLayout();
    protected abstract void initializeToolbar();
    protected abstract void finishWithResult();
    protected abstract void initializeAdapter();
    protected abstract void onItemSelected(View view, int position);

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

    protected void getActivityParameters() {
        mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("payment_method"), PaymentMethod.class);
        mKey = getIntent().getStringExtra("key");
        mToken = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("token"), Token.class);
        mBin = mToken.getFirstSixDigits();
        mCardholder = mToken.getCardholder();
        Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
                mToken.getFirstSixDigits());
        mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
        mSelectedIssuer = (Issuer) this.getIntent().getSerializableExtra("issuer");
    }

    protected void initializeToolbarWithTitle(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (MPTextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbarTitle.setText(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void guessPaymentMethod() {
//        List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
        //cuando haya un paymentmethod
        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        initializeFrontFragment();
    }

    protected void initializeCard() {
        saveCardNumber(getCardNumberHidden());
        saveCardName(mCardholder.getName());
        saveCardExpiryMonth(String.valueOf(mToken.getExpirationMonth()));
        saveCardExpiryYear(String.valueOf(mToken.getExpirationYear()).substring(2,4));
        if (mCurrentPaymentMethod.isSecurityCodeRequired(mBin)
                && mSecurityCodeLocation.equals(CARD_SIDE_FRONT)) {
            saveCardSecurityCode(getSecurityCodeHidden());
        }
    }

    protected void initializeFrontFragment() {
        saveErrorState(NORMAL_STATE);
        if (mFrontFragment == null) {
            mFrontFragment = new CardFrontFragment();
            mFrontFragment.disableAnimate();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_new_card_container, mFrontFragment)
                .commit();
    }

    private String getCardNumberHidden() {
        StringBuilder sb = new StringBuilder();
        sb.append(mBin);
        int length = mToken.getCardNumberLength();
        for (int i = 0; i < length - MercadoPago.BIN_LENGTH - LAST_DIGITIS_LENGTH; i++) {
            sb.append("X");
        }
        sb.append(mToken.getLastFourDigits());
        return sb.toString();
    }

    private String getSecurityCodeHidden() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mToken.getSecurityCodeLength(); i++) {
            sb.append("X");
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_seamless, R.anim.fade_out_seamless);
    }

    @Override
    public void checkFocusOnSecurityCode() {

    }

    @Override
    public boolean hasToFlipCard() {
        return false;
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return mCurrentPaymentMethod == null || mCurrentPaymentMethod.isSecurityCodeRequired(mBin);
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mCurrentPaymentMethod == null || mBin == null) {
            return CARD_SIDE_BACK;
        }
        Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(), mBin);
        return setting.getSecurityCode().getCardLocation();
    }
}
