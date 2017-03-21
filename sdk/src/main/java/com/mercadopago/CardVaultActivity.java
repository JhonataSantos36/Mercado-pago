package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.presenters.CardVaultPresenter;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.CardVaultActivityView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 10/12/16.
 */

public class CardVaultActivity extends AppCompatActivity implements CardVaultActivityView {

    protected Activity mActivity;
    protected CardVaultPresenter mPresenter;
    protected DecorationPreference mDecorationPreference;

    //View controls
    private Boolean mShowBankDeals;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreation(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new CardVaultPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        setContentView();
        if (savedInstanceState == null) {
            getActivityParameters();
            mPresenter.validateActivityParameters();
        }
    }

    private void onBeforeCreation(Bundle savedInstanceState) {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        if(savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        mPresenter = new CardVaultPresenter(getBaseContext());

        BigDecimal amountValue = null;
        String amount = savedInstanceState.getString("amount");
        if (amount != null) {
            amountValue = new BigDecimal(amount);
        }
        mPresenter.setAmount(amountValue);

        List<PaymentMethod> paymentMethods;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethods = JsonUtil.getInstance().getGson().fromJson(savedInstanceState.getString("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethods = null;
        }
        mPresenter.setPaymentMethodList(paymentMethods);
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }

        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setPaymentRecovery(JsonUtil.getInstance().fromJson(savedInstanceState.getString("paymentRecovery"), PaymentRecovery.class));
        mPresenter.setCard(JsonUtil.getInstance().fromJson(savedInstanceState.getString("card"), Card.class));
        mPresenter.setPublicKey(savedInstanceState.getString("merchantPublicKey"));
        mPresenter.setPrivateKey(savedInstanceState.getString("privateKey"));
        mPresenter.setSite(JsonUtil.getInstance().fromJson(savedInstanceState.getString("site"), Site.class));
        mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(savedInstanceState.getString("paymentMethod"), PaymentMethod.class));
        mPresenter.setIssuer(JsonUtil.getInstance().fromJson(savedInstanceState.getString("issuer"), Issuer.class));
        mPresenter.setPayerCost(JsonUtil.getInstance().fromJson(savedInstanceState.getString("payerCost"), PayerCost.class));
        mPresenter.setCardToken(JsonUtil.getInstance().fromJson(savedInstanceState.getString("cardToken"), CardToken.class));
        mPresenter.setCardInfo(JsonUtil.getInstance().fromJson(savedInstanceState.getString("cardInfo"), CardInfo.class));
        mPresenter.setInstallmentsEnabled(savedInstanceState.getBoolean("installmentsEnabled", false));
        mPresenter.setInstallmentsReviewEnabled(savedInstanceState.getBoolean("installmentsReviewEnabled", false));

        mShowBankDeals = savedInstanceState.getBoolean("showBankDeals", true);
        mDecorationPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString("decorationPreference"), DecorationPreference.class);

        mPresenter.initializeMercadoPago();
    }

    private void getActivityParameters() {
        Boolean installmentsEnabled = getIntent().getBooleanExtra("installmentsEnabled", false);
        Boolean discountEnabled = getIntent().getBooleanExtra("discountEnabled", true);
        Boolean directDiscountEnabled = getIntent().getBooleanExtra("directDiscountEnabled", true);
        Boolean installmentsReviewEnabled = getIntent().getBooleanExtra("installmentsReviewEnabled", true);
        String publicKey = getIntent().getStringExtra("merchantPublicKey");
        String payerAccessToken = getIntent().getStringExtra("payerAccessToken");
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        Site site = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        Card card = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        PaymentRecovery paymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);
        BigDecimal amountValue = null;
        String amount = getIntent().getStringExtra("amount");
        String payerEmail = getIntent().getStringExtra("payerEmail");
        Discount discount = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class);

        String discountAdditionalInfo = getIntent().getStringExtra("discountAdditionalInfo");
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> discountAdditionalInfoMap = JsonUtil.getInstance().getGson().fromJson(discountAdditionalInfo, type);

        if (amount != null) {
            amountValue = new BigDecimal(amount);
        }
        List<PaymentMethod> paymentMethods;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethods = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethods = null;
        }
        mShowBankDeals = getIntent().getBooleanExtra("showBankDeals", true);

        mPresenter.setCard(card);
        mPresenter.setInstallmentsEnabled(installmentsEnabled);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setPrivateKey(payerAccessToken);
        mPresenter.setSite(site);
        mPresenter.setPaymentRecovery(paymentRecovery);
        mPresenter.setAmount(amountValue);
        mPresenter.setPaymentMethodList(paymentMethods);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setPayerEmail(payerEmail);
        mPresenter.setDiscount(discount);
        mPresenter.setDiscountEnabled(discountEnabled);
        mPresenter.setDirectDiscountEnabled(directDiscountEnabled);
        mPresenter.setInstallmentsReviewEnabled(installmentsReviewEnabled);
    }

    private void setContentView() {
        setContentView(R.layout.mpsdk_activity_card_vault);
    }

    @Override
    public void onValidStart() {
        MPTracker.getInstance().trackScreen("CARD_VAULT", "2", mPresenter.getPublicKey(), BuildConfig.VERSION_NAME, this);

        mPresenter.initializeMercadoPago();
        initializeViews();
        if (tokenRecoveryAvailable()) {
            mPresenter.setCardInfo(new CardInfo(mPresenter.getPaymentRecovery().getToken()));
            mPresenter.setPaymentMethod(mPresenter.getPaymentRecovery().getPaymentMethod());
            mPresenter.setToken(mPresenter.getPaymentRecovery().getToken());
            startSecurityCodeActivity();

        } else if (savedCardAvailable()) {
            mPresenter.setDiscount(mPresenter.getDiscount());
            mPresenter.setCardInfo(new CardInfo(mPresenter.getCard()));
            mPresenter.setPaymentMethod(mPresenter.getCard().getPaymentMethod());
            mPresenter.setIssuer(mPresenter.getCard().getIssuer());
            if (mPresenter.installmentsRequired()) {
                startInstallmentsActivity();
            } else {
                startSecurityCodeActivity();
            }
            overrideTransitionSlideOutIn();

        } else {
            startGuessingCardActivity();
        }
    }

    private boolean tokenRecoveryAvailable() {
        return mPresenter.getPaymentRecovery() != null && mPresenter.getPaymentRecovery().isTokenRecoverable();
    }

    private boolean savedCardAvailable() {
        return mPresenter.getCard() != null;
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initializeViews() {
        LayoutUtil.showProgressLayout(this);
    }

    private void startSecurityCodeActivity() {
        new MercadoPagoComponents.Activities.SecurityCodeActivityBuilder()
                .setActivity(mActivity)
                .setMerchantPublicKey(mPresenter.getPublicKey())
                .setPaymentMethod(mPresenter.getPaymentMethod())
                .setCardInfo(mPresenter.getCardInfo())
                .setToken(mPresenter.getToken())
                .setCard(mPresenter.getCard())
                .setDecorationPreference(mDecorationPreference)
                .setPayerAccessToken(mPresenter.getPrivateKey())
                .startActivity();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void startGuessingCardActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPagoComponents.Activities.GuessingCardActivityBuilder()
                        .setActivity(mActivity)
                        .setMerchantPublicKey(mPresenter.getPublicKey())
                        .setAmount(mPresenter.getAmount())
                        .setPayerEmail(mPresenter.getPayerEmail())
                        .setPayerAccessToken(mPresenter.getPrivateKey())
                        .setDiscount(mPresenter.getDiscount())
                        .setDiscountEnabled(mPresenter.getDiscountEnabled())
                        .setDirectDiscountEnabled(mPresenter.getDirectDiscountEnabled())
                        .setShowBankDeals(mShowBankDeals)
                        .setPaymentPreference(mPresenter.getPaymentPreference())
                        .setAcceptedPaymentMethods(mPresenter.getPaymentMethodList())
                        .setDecorationPreference(mDecorationPreference)
                        .setPaymentRecovery(mPresenter.getPaymentRecovery())
                        .startActivity();
                overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoComponents.Activities.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.ISSUERS_REQUEST_CODE) {
            resolveIssuersRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.SECURITY_CODE_REQUEST_CODE) {
            resolveSecurityCodeRequest(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPresenter != null) {
            outState.putBoolean("installmentsEnabled", mPresenter.installmentsRequired());
            outState.putBoolean("installmentsReviewEnabled", mPresenter.getInstallmentsReviewEnabled());
            outState.putString("merchantPublicKey", mPresenter.getPublicKey());
            outState.putString("privateKey", mPresenter.getPrivateKey());
            outState.putString("site", JsonUtil.getInstance().toJson(mPresenter.getSite()));
            outState.putString("card", JsonUtil.getInstance().toJson(mPresenter.getCard()));
            outState.putString("paymentRecovery", JsonUtil.getInstance().toJson(mPresenter.getPaymentRecovery()));
            outState.putBoolean("showBankDeals", mShowBankDeals);

            if (mPresenter.getAmount() != null) {
                outState.putString("amount", mPresenter.getAmount().toString());
            }

            if (mPresenter.getPaymentMethodList() != null) {
                outState.putString("paymentMethodList", JsonUtil.getInstance().toJson(mPresenter.getPaymentMethodList()));
            }

            if (mPresenter.getPaymentPreference() != null) {
                outState.putString("paymentPreference", JsonUtil.getInstance().toJson(mPresenter.getPaymentPreference()));
            }

            if (mPresenter.getPaymentMethod() != null) {
                outState.putString("paymentMethod", JsonUtil.getInstance().toJson(mPresenter.getPaymentMethod()));
            }

            if (mPresenter.getIssuer() != null) {
                outState.putString("issuer", JsonUtil.getInstance().toJson(mPresenter.getIssuer()));
            }

            if (mPresenter.getPayerCost() != null) {
                outState.putString("payerCost", JsonUtil.getInstance().toJson(mPresenter.getPayerCost()));
            }

            if (mPresenter.getCardToken() != null) {
                outState.putString("cardToken", JsonUtil.getInstance().toJson(mPresenter.getCardToken()));
            }

            if (mPresenter.getCardInfo() != null) {
                outState.putString("cardInfo", JsonUtil.getInstance().toJson(mPresenter.getCardInfo()));
            }

            if (mDecorationPreference != null) {
                outState.putString("decorationPreference", JsonUtil.getInstance().toJson(mDecorationPreference));
            }
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mPresenter.recoverFromFailure();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Issuer issuer = JsonUtil.getInstance().fromJson(bundle.getString("issuer"), Issuer.class);
            mPresenter.setIssuer(issuer);
            mPresenter.checkStartInstallmentsActivity();
        } else if (resultCode == RESULT_CANCELED) {
            MPTracker.getInstance().trackEvent("INSTALLMENTS", "CANCELED", "2", mPresenter.getPublicKey(),
                    mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            PayerCost payerCost = JsonUtil.getInstance().fromJson(bundle.getString("payerCost"), PayerCost.class);
            Discount discount = JsonUtil.getInstance().fromJson(bundle.getString("discount"), Discount.class);

            mPresenter.setPayerCost(payerCost);
            mPresenter.setDiscount(discount);

            if (savedCardAvailable()) {
                startSecurityCodeActivity();
            } else {
                mPresenter.createToken();
            }
        } else if (resultCode == RESULT_CANCELED) {
            MPTracker.getInstance().trackEvent("INSTALLMENTS", "CANCELED", "2", mPresenter.getPublicKey(),
                    mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            CardToken cardToken = JsonUtil.getInstance().fromJson(data.getStringExtra("cardToken"), CardToken.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            Discount discount = JsonUtil.getInstance().fromJson(data.getStringExtra("discount"), Discount.class);
            Boolean directDiscountEnabled = data.getBooleanExtra("directDiscountEnabled", true);

            mPresenter.setPaymentMethod(paymentMethod);
            mPresenter.setCardToken(cardToken);
            mPresenter.setCardInfo(new CardInfo(cardToken));
            mPresenter.checkStartIssuersActivity();
            mPresenter.setDirectDiscountEnabled(directDiscountEnabled);

            if (discount != null) {
                mPresenter.setDiscount(discount);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (mPresenter.getSite() == null) {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        BuildConfig.VERSION_NAME, this);
            } else {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            }
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveSecurityCodeRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mPresenter.setToken(token);
            finishWithResult();
        } else if (resultCode == RESULT_CANCELED) {
            if (mPresenter.getSite() == null) {
                MPTracker.getInstance().trackEvent("SECURITY_CODE_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        BuildConfig.VERSION_NAME, this);
            } else {
                MPTracker.getInstance().trackEvent("SECURITY_CODE_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            }
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    @Override
    public void startIssuersActivity() {
        new MercadoPagoComponents.Activities.IssuersActivityBuilder()
                .setActivity(mActivity)
                .setMerchantPublicKey(mPresenter.getPublicKey())
                .setPayerAccessToken(mPresenter.getPrivateKey())
                .setPaymentMethod(mPresenter.getPaymentMethod())
                .setCardInfo(mPresenter.getCardInfo())
                .setDecorationPreference(mDecorationPreference)
                .startActivity();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void startInstallmentsActivity() {
        new MercadoPagoComponents.Activities.InstallmentsActivityBuilder()
                .setActivity(mActivity)
                .setMerchantPublicKey(mPresenter.getPublicKey())
                .setPayerAccessToken(mPresenter.getPrivateKey())
                .setPaymentMethod(mPresenter.getPaymentMethod())
                .setAmount(mPresenter.getAmount())
                .setPayerEmail(mPresenter.getPayerEmail())
                .setDiscount(mPresenter.getDiscount())
                .setDiscountEnabled(mPresenter.getDiscountEnabled())
                .setIssuer(mPresenter.getIssuer())
                .setPaymentPreference(mPresenter.getPaymentPreference())
                .setSite(mPresenter.getSite())
                .setDecorationPreference(mDecorationPreference)
                .setInstallmentsEnabled(mPresenter.getInstallmentsEnabled())
                .setInstallmentsReviewEnabled(mPresenter.getInstallmentsReviewEnabled())
                .setCardInfo(mPresenter.getCardInfo())
                .startActivity();
    }

    @Override
    public void overrideTransitionHold() {
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    @Override
    public void overrideTransitionSlideOutIn() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void finishWithResult() {
        if (mPresenter.getPaymentRecovery() != null && mPresenter.getPaymentRecovery().isTokenRecoverable()) {
            PayerCost payerCost = mPresenter.getPaymentRecovery().getPayerCost();
            mPresenter.setPayerCost(payerCost);
            Issuer issuer = mPresenter.getPaymentRecovery().getIssuer();
            mPresenter.setIssuer(issuer);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mPresenter.getPayerCost()));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPresenter.getPaymentMethod()));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mPresenter.getToken()));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mPresenter.getIssuer()));
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    @Override
    public void startErrorView(String message) {
        ErrorUtil.startErrorActivity(mActivity, message, false);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

}
