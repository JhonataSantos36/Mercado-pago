package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.presenters.PaymentResultPresenter;
import com.mercadopago.providers.PaymentResultProvider;
import com.mercadopago.providers.PaymentResultProviderImpl;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.PaymentResultView;

import java.math.BigDecimal;

public class PaymentResultActivity extends Activity implements PaymentResultView {

    public static final String PAYER_ACCESS_TOKEN_BUNDLE = "mMerchantPublicKey";
    public static final String MERCHANT_PUBLIC_KEY_BUNDLE = "mpayerAccessToken";

    public static final String CONGRATS_DISPLAY_BUNDLE = "mCongratsDisplay";
    public static final String PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE = "mPaymentResultScreenPreference";
    public static final String SERVICE_PREFERENCE_BUNDLE = "mServicePreference";

    public static final String PRESENTER_BUNDLE = "mPresenter";

    private PaymentResultPresenter mPresenter;

    private String mMerchantPublicKey;
    private  String mPayerAccessToken;

    private Integer mCongratsDisplay;
    private PaymentResultScreenPreference mPaymentResultScreenPreference;
    private ServicePreference mServicePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mPresenter = new PaymentResultPresenter();
            getActivityParameters();
            configurePresenter();
            mPresenter.initialize();
        }
    }

    @Override
    public void showCongrats(Site site, BigDecimal amount, PaymentResult paymentResult, Boolean discountEnabled) {
        new MercadoPagoComponents.Activities.CongratsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setCongratsDisplay(mCongratsDisplay)
                .setServicePreference(mServicePreference)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setSite(site)
                .setAmount(amount)
                .setDiscountEnabled(discountEnabled)
                .setPaymentResult(paymentResult)
                .startActivity();
    }

    @Override
    public void showCallForAuthorize(Site site, PaymentResult paymentResult) {
        new MercadoPagoComponents.Activities.CallForAuthorizeActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setPaymentResult(paymentResult)
                .setSite(site)
                .startActivity();
    }

    @Override
    public void showRejection(PaymentResult paymentResult) {
        new MercadoPagoComponents.Activities.RejectionActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setPaymentResult(paymentResult)
                .startActivity();
    }

    @Override
    public void showPending(PaymentResult paymentResult) {
        new MercadoPagoComponents.Activities.PendingActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setPaymentResult(paymentResult)
                .startActivity();
    }

    @Override
    public void showInstructions(Site site, BigDecimal amount, PaymentResult paymentResult) {
        new MercadoPagoComponents.Activities.InstructionsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPayerAccessToken(mPayerAccessToken)
                .setActivity(this)
                .setServicePreference(mServicePreference)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setSite(site)
                .setAmount(amount)
                .setPaymentResult(paymentResult)
                .startActivity();
    }

    @Override
    public void showError(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), false, mMerchantPublicKey);
    }

    @Override
    public void showError(String errorMessage, String errorDetail) {
        ErrorUtil.startErrorActivity(this, errorMessage, errorDetail, false, mMerchantPublicKey);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PRESENTER_BUNDLE, JsonUtil.getInstance().toJson(mPresenter));

        outState.putString(MERCHANT_PUBLIC_KEY_BUNDLE, mMerchantPublicKey);
        outState.putString(PAYER_ACCESS_TOKEN_BUNDLE, mPayerAccessToken);

        outState.putInt(CONGRATS_DISPLAY_BUNDLE, mCongratsDisplay);
        outState.putString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mPaymentResultScreenPreference));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPresenter = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PRESENTER_BUNDLE), PaymentResultPresenter.class);

        mMerchantPublicKey = savedInstanceState.getString(MERCHANT_PUBLIC_KEY_BUNDLE);
        mPayerAccessToken = savedInstanceState.getString(PAYER_ACCESS_TOKEN_BUNDLE);

        mCongratsDisplay = savedInstanceState.getInt(CONGRATS_DISPLAY_BUNDLE, -1);
        mServicePreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(SERVICE_PREFERENCE_BUNDLE), ServicePreference.class);

        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE), PaymentResultScreenPreference.class);
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void getActivityParameters() {

        Boolean discountEnabled = getIntent().getExtras().getBoolean("discountEnabled", true);
        PaymentResult paymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        Site site = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("site"), Site.class);
        BigDecimal amount = null;
        if (getIntent().getStringExtra("amount") != null) {
            amount = new BigDecimal(getIntent().getStringExtra("amount"));
        }

        mPresenter.setDiscountEnabled(discountEnabled);
        mPresenter.setPaymentResult(paymentResult);
        mPresenter.setSite(site);
        mPresenter.setAmount(amount);

        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayerAccessToken = getIntent().getStringExtra("payerAccessToken");

        mCongratsDisplay = getIntent().getIntExtra("congratsDisplay", -1);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        mServicePreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("servicePreference"), ServicePreference.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE) {
            resolveTimerObserverResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PENDING_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else {
            finishWithCancelResult(data);
        }
    }

    private void configurePresenter() {
        PaymentResultProvider provider = new PaymentResultProviderImpl(this);
        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(provider);
    }

    private void resolveTimerObserverResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    private void resolveRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult(resultCode, data);
        }
    }

    private void finishWithCancelResult(Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }
}
