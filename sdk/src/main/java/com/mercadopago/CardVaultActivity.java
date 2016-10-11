package com.mercadopago;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
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

    protected PaymentRecovery mPaymentRecovery;
    protected PayerCost mPayerCost;
    protected PaymentPreference mPaymentPreference;
    protected List<PaymentMethod> mPaymentMethodList;
    protected Site mSite;
    protected Boolean mInstallmentsEnabled;
    protected Card mCard;
    protected Token mToken;
    protected View mCardBackground;
    protected PaymentMethod mPaymentMethod;
    protected String mPublicKey;
    protected BigDecimal mAmount;
    protected MercadoPago mMercadoPago;
    protected Issuer mSelectedIssuer;

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
        initializeFrontFragment();
    }

    @Override
    protected void onBeforeCreation() {
        super.onBeforeCreation();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    @Override
    protected void onValidStart() {
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();
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
        mInstallmentsEnabled = getIntent().getBooleanExtra("installmentsEnabled", false);
        mPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mSite = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        mCard = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        mSecurityCodeLocation = CardInterface.CARD_SIDE_BACK;

        mPaymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);

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
    protected void hideCardLayout() {
        mCardBackground.setVisibility(View.GONE);
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
                    .setPaymentRecovery(mPaymentRecovery)
                    .setCard(mCard)
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
            mPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);

            if (mToken != null && mPaymentMethod != null) {
                String bin = mToken.getFirstSixDigits();
                if (bin != null) {
                    List<Setting> settings = mPaymentMethod.getSettings();
                    Setting setting = Setting.getSettingByBin(settings, bin);

                    mSecurityCodeLocation = mCard == null ? setting.getSecurityCode().getCardLocation() : mCard.getSecurityCode().getCardLocation();
                    mCardNumberLength = mCard == null ? setting.getCardNumber().getLength() : CARD_NUMBER_MAX_LENGTH;
                }
            }

            if (mCard == null) {
                setCardInformation(mToken);
            } else {
                //Token from saved card does not have last four digits.
                mToken.setLastFourDigits(mCard.getLastFourDigits());
                setCardInformation(mCard);
            }
            setPaymentMethod(mPaymentMethod);

            initializeCard();
            if (mPaymentMethod != null) {
                int color = getCardColor(mPaymentMethod);
                mFrontFragment.setCardColor(color);
            }

            if (installmentsRequired()) {
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

    private boolean installmentsRequired() {
        return mInstallmentsEnabled && (mPaymentRecovery == null || !mPaymentRecovery.isTokenRecoverable());
    }

    public void checkStartInstallmentsActivity() {
        if (mPaymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD)) {
            mMercadoPago.getInstallments(mToken.getFirstSixDigits(), mAmount, mSelectedIssuer.getId(), mPaymentMethod.getId(),
                    new Callback<List<Installment>>() {
                        @Override
                        public void success(List<Installment> installments) {
                            if (installments.size() == 1) {
                                resolvePayerCosts(installments.get(0).getPayerCosts());
                            } else {
                                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), false);
                            }
                        }

                        @Override
                        public void failure(ApiException apiException) {
                            setFailureRecovery(new FailureRecovery() {
                                @Override
                                public void recover() {
                                    checkStartInstallmentsActivity();
                                }
                            });
                            ApiUtil.showApiExceptionError(getActivity(), apiException);
                        }
                    });
        } else {
            finishWithResult();
        }
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);

        if (defaultPayerCost == null) {
            if (payerCosts.isEmpty()) {
                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "no payer costs found at InstallmentsActivity", false);
            } else if (payerCosts.size() == 1) {
                mPayerCost = payerCosts.get(0);
                finishWithResult();
            } else {
                startInstallmentsActivity(payerCosts);
            }
        } else {
            mPayerCost = defaultPayerCost;
            finishWithResult();
        }
    }

    public void startInstallmentsActivity(final List<PayerCost> payerCosts) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(getActivity())
                .setPublicKey(mPublicKey)
                .setPaymentMethod(mPaymentMethod)
                .setAmount(mAmount)
                .setToken(mToken)
                .setCard(mCard)
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
        if (mPaymentRecovery != null && mPaymentRecovery.isTokenRecoverable()){
            mPayerCost = mPaymentRecovery.getPayerCost();
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mPayerCost));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        setResult(RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
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
