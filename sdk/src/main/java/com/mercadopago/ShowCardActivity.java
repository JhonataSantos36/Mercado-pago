package com.mercadopago;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

public abstract class ShowCardActivity extends FrontCardActivity {

    public static Integer LAST_DIGITIS_LENGTH = 4;

    protected MercadoPago mMercadoPago;

    //Card container
    protected FrameLayout mCardContainer;
    protected CardFrontFragment mFrontFragment;

    //Card data
    protected String mBin;
    protected BigDecimal mAmount;
    protected String mPublicKey;
    protected Token mToken;
    protected String mSecurityCodeLocation;
    protected Cardholder mCardholder;
    protected int mCardNumberLength;

    //Local vars
    protected Issuer mSelectedIssuer;
    protected MPTextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void finishWithResult();

    protected void getActivityParameters() {
        mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mPublicKey = getIntent().getStringExtra("publicKey");
        mToken = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("token"), Token.class);
        mBin = mToken.getFirstSixDigits();
        mCardholder = mToken.getCardholder();
        Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
                mToken.getFirstSixDigits());
        if (setting != null) {
            mCardNumberLength = setting.getCardNumber().getLength();
        } else {
            mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        }
        mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
        mSelectedIssuer = (Issuer) this.getIntent().getSerializableExtra("issuer");
    }

    protected void initializeToolbarWithTitle(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (MPTextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarTitle.setText(title);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    protected void initializeCard() {
        if (mCurrentPaymentMethod == null || mToken == null || mCardholder == null) {
            return;
        }
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
        int length = mToken.getCardNumberLength();
        for (int i = 0; i < length - LAST_DIGITIS_LENGTH; i++) {
            sb.append("x");
        }
        sb.append(mToken.getLastFourDigits());
        return sb.toString();
    }

    private String getSecurityCodeHidden() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mToken.getSecurityCodeLength(); i++) {
            sb.append("x");
        }
        return sb.toString();
    }

    @Override
    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_seamless, R.anim.fade_out_seamless);
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
