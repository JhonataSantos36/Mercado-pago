package com.mercadopago;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;


public class CardVaultActivity extends ShowCardActivity {

    private View mCardBackground;

    protected PayerCost mPayerCost;
    protected PaymentPreference mPaymentPreference;
    protected List<PaymentMethod> mPaymentMethodList;
    protected Site mSite;
    protected Boolean mInstallmentsEnabled;

    @Override
    protected void initializeControls() {
        mCardBackground = findViewById(R.id.mpsdkCardBackground);

        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }

        initializeToolbar();
    }

    @Override
    protected void initializeFragments(Bundle savedInstanceState) {
        super.initializeFragments(savedInstanceState);
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();
        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        initializeFrontFragment();
    }

    @Override
    protected void onBeforeCreation() {
        if (getResources().getBoolean(R.bool.only_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onValidStart() {
        startGuessingCardActivity();
    }

    @Override
    protected void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mInstallmentsEnabled = getIntent().getBooleanExtra("installmentsEnabled", false);
        mPublicKey = getIntent().getStringExtra("publicKey");
        mSite = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        mSecurityCodeLocation = CardInterface.CARD_SIDE_BACK;

        String amount = getIntent().getStringExtra("amount");
        if (amount != null) {
            mAmount = new BigDecimal(amount);
        }
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            mPaymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            mPaymentMethodList = null;
        }
        mPaymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mPublicKey == null) {
            throw new IllegalStateException();
        }
        if (mInstallmentsEnabled && (mSite == null || mAmount == null)) {
            throw new IllegalStateException();
        }
    }

    protected void initializeToolbar() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (mDecorationPreference != null && mDecorationPreference.hasColors() && toolbar != null) {
            toolbar.setBackgroundColor(mDecorationPreference.getLighterColor());
            decorateUpArrow(toolbar);
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_new_card_vault);
    }

    private void startGuessingCardActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(getActivity())
                        .setPublicKey(mPublicKey)
                        .setAmount(new BigDecimal(100))
                        .setPaymentPreference(mPaymentPreference)
                        .setSupportedPaymentMethods(mPaymentMethodList)
                        .setDecorationPreference(mDecorationPreference)
                        .startGuessingCardActivity();
                overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mPayerCost = JsonUtil.getInstance().fromJson(bundle.getString("payerCost"), PayerCost.class);
            finishWithResult();
        } else if (resultCode == RESULT_CANCELED) {
            MPTracker.getInstance().trackEvent("INSTALLMENTS", "CANCELED", 2, mPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);

            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            if (mToken != null && mCurrentPaymentMethod != null) {
                mBin = mToken.getFirstSixDigits();
                mCardholder = mToken.getCardholder();
                List<Setting> settings = mCurrentPaymentMethod.getSettings();
                Setting setting = Setting.getSettingByBin(settings, mBin);
                mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
                mCardNumberLength = setting.getCardNumber().getLength();
            }
            initializeCard();
            if (mCurrentPaymentMethod != null) {
                int color = getCardColor(mCurrentPaymentMethod);
                mFrontFragment.setCardColor(color);
            }

            if (mInstallmentsEnabled) {
                checkStartInstallmentsActivity();
            } else {
                finishWithResult();
            }

        } else if (resultCode == RESULT_CANCELED) {

            if (mSite == null) {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", 2, mPublicKey, BuildConfig.VERSION_NAME, this);
            } else {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", 2, mPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);
            }

            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    public void checkStartInstallmentsActivity() {
        if (!mCurrentPaymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD)) {
            finishWithResult();
        } else {
            mMercadoPago.getInstallments(mBin, mAmount, mSelectedIssuer.getId(), mCurrentPaymentMethod.getId(),
                    new Callback<List<Installment>>() {
                        @Override
                        public void success(List<Installment> installments) {
                            if (isActivityActive()) {
                                if (installments.size() == 1) {
                                    if (installments.get(0).getPayerCosts().size() == 1) {
                                        mPayerCost = installments.get(0).getPayerCosts().get(0);
                                        finishWithResult();
                                    } else if (installments.get(0).getPayerCosts().size() > 1) {
                                        startInstallmentsActivity(installments.get(0).getPayerCosts());
                                    } else {
                                        ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), false);
                                    }
                                } else {
                                    ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), false);
                                }
                            }
                        }

                        @Override
                        public void failure(ApiException apiException) {
                            if (isActivityActive()) {
                                setFailureRecovery(new FailureRecovery() {
                                    @Override
                                    public void recover() {
                                        checkStartInstallmentsActivity();
                                    }
                                });
                                ApiUtil.showApiExceptionError(getActivity(), apiException);
                            }
                        }
                    });
        }
    }

    public void startInstallmentsActivity(final List<PayerCost> payerCosts) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(getActivity())
                .setPublicKey(mPublicKey)
                .setPaymentMethod(mCurrentPaymentMethod)
                .setAmount(mAmount)
                .setToken(mToken)
                .setPayerCosts(payerCosts)
                .setIssuer(mSelectedIssuer)
                .setPaymentPreference(mPaymentPreference)
                .setSite(mSite)
                .setDecorationPreference(mDecorationPreference)
                .startInstallmentsActivity();
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mPayerCost));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mCurrentPaymentMethod));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void initializeCardByToken() {

    }

    @Override
    public IdentificationType getCardIdentificationType() {
        return null;
    }
}
