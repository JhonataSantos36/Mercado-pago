package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends AppCompatActivity{

    //Parameters
    protected CheckoutPreference mCheckoutPreference;
    protected String mMerchantPublicKey;
    protected Boolean mShowBankDeals;

    //Local vars
    protected MercadoPago mMercadoPago;
    protected Activity mActivity;
    protected Payment mPayment;
    protected boolean mSupportMPApp = true;
    protected PaymentMethod mSelectedPaymentMethod;
    protected PaymentIntent mPaymentIntent;
    protected String mExceptionOnMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Set checkout preference
        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        if(validParameters())
        {
            setActivity();

            mMercadoPago = new MercadoPago.Builder()
                    .setContext(this)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            startPaymentVaultActivity();
        }
        //set content view!
    }

    protected void setActivity() {
        this.mActivity = this;
    }

    protected void startPaymentVaultActivity() {

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder();

        builder.setActivity(this);
        builder.setPublicKey(mMerchantPublicKey);
        builder.setSupportMPApp(mSupportMPApp);
        builder.setAmount(mCheckoutPreference.getAmount());
        builder.setShowBankDeals(mShowBankDeals);
        builder.setDefaultPaymentMethodId(mCheckoutPreference.getDefaultPaymentMethodId());
        builder.setExcludedPaymentMethodIds(mCheckoutPreference.getExcludedPaymentMethods());
        builder.setExcludedPaymentTypes(mCheckoutPreference.getExcludedPaymentTypes());
        builder.setDefaultInstallments(mCheckoutPreference.getDefaultInstallments());
        builder.setMaxInstallments(mCheckoutPreference.getMaxInstallments());

        if(payerHasEmail())
        {
            builder.setMerchantBaseUrl("https://mp-android-sdk.herokuapp.com/");
            builder.setMerchantGetCustomerUri("customers?preference_id=" + mCheckoutPreference.getId());
        }

        builder.startVaultActivity();
    }

    protected boolean payerHasEmail() {

        return mCheckoutPreference != null
                && mCheckoutPreference.getPayer() != null
                && mCheckoutPreference.getPayer().getEmail() != null
                && !mCheckoutPreference.getPayer().getEmail().equals("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == MercadoPago.VAULT_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                boolean MPAppNeeded = data.getBooleanExtra("MPAppNeeded", false);
                if(MPAppNeeded) {
                    startMPApp();
                }
                else {
                    Long issuerId = (data.getStringExtra("issuerId") != null)
                            ? Long.parseLong(data.getStringExtra("issuerId")) : null;

                    Integer installments = (data.getStringExtra("installments") != null)
                            ? Integer.parseInt(data.getStringExtra("installments")) : null;

                    String token = data.getStringExtra("token");

                    mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

                    setPaymentIntent(issuerId, installments, token, mSelectedPaymentMethod);
                    createPayment();
                }
            }

        }
        Intent checkoutResult = null;
        if (requestCode == MercadoPago.INSTALL_APP_REQUEST_CODE) {

            if(data != null) {
                if (!data.getBooleanExtra("backButtonPressed", false)) {
                    checkoutResult = data;
                } else {
                    return;
                }
            }

        } else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

            // from SDK
            checkoutResult = new Intent();
            checkoutResult.putExtra("externalReference", mPayment.getExternalReference());
            checkoutResult.putExtra("paymentId", mPayment.getId());
            checkoutResult.putExtra("paymentStatus", mPayment.getStatus());
            checkoutResult.putExtra("paymentType", mPayment.getPaymentTypeId());
            checkoutResult.putExtra("preferenceId", mCheckoutPreference.getId());
        }

        // Return checkout result
        setResult(RESULT_OK, checkoutResult);
        finish();

    }

    protected void setPaymentIntent(Long issuerId, Integer installments, String token, PaymentMethod paymentMethod) {
        mPaymentIntent = new PaymentIntent();
        mPaymentIntent.setPrefId(mCheckoutPreference.getId());
        mPaymentIntent.setToken(token);
        if (issuerId != null) {
            mPaymentIntent.setIssuerId(issuerId);
        }
        mPaymentIntent.setInstallments(installments);
        mPaymentIntent.setPaymentMethodId(paymentMethod.getId());
    }


    protected boolean validParameters() {

        if ((mMerchantPublicKey != null) && (mCheckoutPreference != null)) {
            return true;
        }
        return false;
    }

    protected void createPayment() {

        // Create payment
        LayoutUtil.showProgressLayout(this);
        mMercadoPago.createPayment(mPaymentIntent, new Callback<Payment>() {
            @Override
            public void success(Payment payment, Response response) {

                // Set local payment
                mPayment = payment;

                // Show congrats activity
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
                        .setPayment(payment)
                        .setPaymentMethod(mSelectedPaymentMethod)
                        .startCongratsActivity();
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "resolveCreateTokenSuccess";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    protected void startMPApp() {

        if ((mCheckoutPreference != null) && (mCheckoutPreference.getId() != null)) {
            Intent intent = new Intent(this, InstallAppActivity.class);
            intent.putExtra("preferenceId", mCheckoutPreference.getId());
            intent.putExtra("packageName", this.getPackageName());
            intent.putExtra("deepLink", "mercadopago://mpsdk_install_app");
            startActivityForResult(intent, MercadoPago.INSTALL_APP_REQUEST_CODE);
        }
    }
}
