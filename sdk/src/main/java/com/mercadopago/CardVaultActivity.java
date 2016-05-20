package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CardVaultActivity extends ShowCardActivity {

    private Activity mActivity;

    private PayerCost mPayerCost;
    private PaymentPreference mPaymentPreference;
    private FailureRecovery mFailureRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView();
        getActivityParameters();

        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .build();

        if (mCurrentPaymentMethod != null) {
            initializeCard();
        }
        initializeFrontFragment();
        fadeInFormActivity();
    }

    @Override
    protected void getActivityParameters() {
        mPublicKey = getIntent().getStringExtra("publicKey");
        mSecurityCodeLocation = CardInterface.CARD_SIDE_BACK;
        mAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void setContentView() {
        setContentView(R.layout.activity_flow_card);
    }

    private void fadeInFormActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
                        .setPublicKey(mPublicKey)
                        .setAmount(new BigDecimal(100))
                        .setPaymentPreference(mPaymentPreference)
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
            }
            initializeCard();
            checkStartInstallmentsActivity();

        } else if (resultCode == RESULT_CANCELED){
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
                        public void success(List<Installment> installments, Response response) {
                            if (installments.size() == 1) {
                                if(installments.get(0).getPayerCosts().size() == 1) {
                                    mPayerCost = installments.get(0).getPayerCosts().get(0);
                                    finishWithResult();
                                }
                                if(installments.get(0).getPayerCosts().size() > 1)
                                {
                                    startInstallmentsActivity(installments.get(0).getPayerCosts());
                                }
                                else {
                                    ErrorUtil.startErrorActivity(mActivity, getString(R.string.mpsdk_standard_error_message), false);
                                }
                            } else {
                                ErrorUtil.startErrorActivity(mActivity, getString(R.string.mpsdk_standard_error_message), false);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            mFailureRecovery = new FailureRecovery() {
                                @Override
                                public void recover() {
                                    checkStartInstallmentsActivity();
                                }
                            };
                            ApiUtil.showApiExceptionError(mActivity, error);
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
