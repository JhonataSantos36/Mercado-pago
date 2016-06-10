package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        mSite = (Site) getIntent().getSerializableExtra("site");
        mSecurityCodeLocation = CardInterface.CARD_SIDE_BACK;
        mAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        mPaymentMethodList = (ArrayList<PaymentMethod>) this.getIntent().getSerializableExtra("paymentMethodList");
        mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void initializeToolbar() {
        super.initializeToolbar("", true);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_new_card_vault);
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
            mPayerCost = (PayerCost) bundle.getSerializable("payerCost");
            finishWithResult();
        } else if (resultCode == RESULT_CANCELED) {
            MPTracker.getInstance().trackEvent( "INSTALLMENTS", "CANCELED", "2", mPublicKey, "MLA", "1.0", this);

            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mCurrentPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mToken = (Token) data.getSerializableExtra("token");
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");
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
        if (!mCurrentPaymentMethod.getPaymentTypeId().equals(PaymentType.CREDIT_CARD)) {
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
                        .startCardInstallmentsActivity();
                overridePendingTransition(R.anim.fade_in_seamless, R.anim.fade_out_seamless);
            }
        });
    }

    @Override
    protected void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", mPayerCost);
        returnIntent.putExtra("paymentMethod", mCurrentPaymentMethod);
        returnIntent.putExtra("token", mToken);
        returnIntent.putExtra("issuer", mSelectedIssuer);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void recoverFromFailure() {
        if(mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
