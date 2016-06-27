package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
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

    private Activity mActivity;
    protected boolean mActiveActivity;

    private View mCardBackground;

    private PayerCost mPayerCost;
    private PaymentPreference mPaymentPreference;
    private FailureRecovery mFailureRecovery;
    private List<PaymentMethod> mPaymentMethodList;
    private Site mSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        if(mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        mActivity = this;
        mActiveActivity = true;
        setContentView();
        initializeToolbar();
        initializeActivityControls();
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        initializeFrontFragment();
        startGuessingCardActivity();
    }

    private void initializeActivityControls() {
        mCardBackground = findViewById(R.id.mpsdkCardBackground);

        if(mDecorationPreference != null && mDecorationPreference.hasColors())
        {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }
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

    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mPublicKey = getIntent().getStringExtra("publicKey");
        mSite = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        mSecurityCodeLocation = CardInterface.CARD_SIDE_BACK;
        mAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        try {
            Type listType = new TypeToken<List<PaymentMethod>>(){}.getType();
            mPaymentMethodList =  JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            mPaymentMethodList = null;
        }
        mPaymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void initializeToolbar() {
        super.initializeToolbar("", true);
    }

    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_new_card_vault);
    }

    private void startGuessingCardActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
                        .setPublicKey(mPublicKey)
                        .setAmount(new BigDecimal(100))
                        .setPaymentPreference(mPaymentPreference)
                        .setSupportedPaymentMethods(mPaymentMethodList)
                        .setDecorationPreference(mDecorationPreference)
                        .startGuessingCardActivity();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
       } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
           resolveInstallmentsRequest(resultCode, data);
       }
       else if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            recoverFromFailure();
        }
        else {
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
            MPTracker.getInstance().trackEvent( "INSTALLMENTS", "CANCELED", "2", mPublicKey, "MLA", "1.0", this);

            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
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
                mFrontFragment.quickTransition(color);
            }
            checkStartInstallmentsActivity();

        } else if (resultCode == RESULT_CANCELED){
            MPTracker.getInstance().trackEvent( "GUESSING_CARD", "CANCELED", "2", mPublicKey, "MLA", "1.0", this);

            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    public void checkStartInstallmentsActivity() {
        if (!mCurrentPaymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD)) {
            finishWithResult();
        }
        else {
            mMercadoPago.getInstallments(mBin, mAmount, mSelectedIssuer.getId(), mCurrentPaymentMethod.getId(),
                    new Callback<List<Installment>>() {
                        @Override
                        public void success(List<Installment> installments) {
                            MPTracker.getInstance().trackEvent("CARD_INSTALLMENTS", "GET_INSTALLMENTS_RESPONSE", "SUCCESS", "2", mPublicKey, "MLA", "1.0", mActivity);
                            if (mActiveActivity) {
                                if (installments.size() == 1) {
                                    if (installments.get(0).getPayerCosts().size() == 1) {
                                        mPayerCost = installments.get(0).getPayerCosts().get(0);
                                        finishWithResult();
                                    }
                                    if (installments.get(0).getPayerCosts().size() > 1) {
                                        startInstallmentsActivity(installments.get(0).getPayerCosts());
                                    } else {
                                        ErrorUtil.startErrorActivity(mActivity, getString(R.string.mpsdk_standard_error_message), false);
                                    }
                                } else {
                                    ErrorUtil.startErrorActivity(mActivity, getString(R.string.mpsdk_standard_error_message), false);
                                }
                            }
                        }

                        @Override
                        public void failure(ApiException apiException) {
                            MPTracker.getInstance().trackEvent("CARD_INSTALLMENTS", "GET_INSTALLMENTS_RESPONSE", "FAIL", "2", mPublicKey, "MLA", "1.0", mActivity);
                            if (mActiveActivity) {
                                mFailureRecovery = new FailureRecovery() {
                                    @Override
                                    public void recover() {
                                        checkStartInstallmentsActivity();
                                    }
                                };
                                ApiUtil.showApiExceptionError(mActivity, apiException);
                            }
                        }
                    });
        }
    }

    public void startInstallmentsActivity(final List<PayerCost> payerCosts) {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
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
                overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
            }
        });
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

    private void recoverFromFailure() {
        if(mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
