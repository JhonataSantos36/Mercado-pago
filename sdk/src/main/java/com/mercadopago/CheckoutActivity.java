package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CheckoutActivity extends VaultActivity {

    protected CheckoutPreference mCheckoutPreference;
    protected Payment mPayment;
    protected boolean mSupportMPApp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set checkout preference
        mCheckoutPreference = (CheckoutPreference) this.getIntent().getSerializableExtra("checkoutPreference");

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_checkout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!onVaultActivityResult(requestCode, resultCode, data)) {

            // Set checkout result
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
    }

    @Override
    protected void setAmount() {

        if (mCheckoutPreference != null) {
            mAmount = mCheckoutPreference.getAmount();
        }
    }

    @Override
    protected boolean validParameters() {

        if ((mMerchantPublicKey != null) && (mCheckoutPreference != null)) {
            return true;
        }
        return false;
    }

    @Override
    protected void setActivity() {

        mActivity = this;
        mActivity.setTitle(getString(R.string.mpsdk_title_activity_checkout));
    }

    @Override
    protected void initPaymentFlow() {

        // Show payment method selection or go for customer's cards
        if ((mCheckoutPreference.getPayer() != null) &&
                (mCheckoutPreference.getPayer().getEmail() != null) && (!mCheckoutPreference.getPayer().getEmail().equals(""))) {
            getCustomerCardsAsync();
        } else {
            startPaymentMethodsActivity();
        }
    }

    @Override
    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            PaymentMethodRow selectedPaymentMethodRow = (PaymentMethodRow) data.getSerializableExtra("paymentMethodRow");

            if (selectedPaymentMethodRow.getCard() != null) {

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = selectedPaymentMethodRow;
                mSelectedPayerCost = null;
                mTempPaymentMethod = null;

                // Set customer method selection
                setCustomerMethodSelection();

            } else {

                if (selectedPaymentMethodRow.getLabel().equals(getResources().getString(R.string.mpsdk_mp_app_name))) {

                    startMPApp();

                } else {

                    startPaymentMethodsActivity();
                }
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    @Override
    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

            if (MercadoPagoUtil.isCardPaymentType(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new card activity
                    startNewCardActivity();
                }
            } else if (mTempPaymentMethod.getId().equals(getResources().getString(R.string.mpsdk_mp_app_id))) {

                // Start MercadoPago App
                startMPApp();

            } else {  // Off-line methods

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                mSelectedIssuer = null;

                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

                // Set security card visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    @Override
    protected void startCustomerCardsActivity() {

        // Now call customer cards activity with MP App support
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setCards(mCards)
                .setSupportMPApp(mSupportMPApp)
                .startCustomerCardsActivity();
    }

    @Override
    protected void startPaymentMethodsActivity() {

        // Now call payment methods activity with MP App support
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setSupportedPaymentTypes(mPaymentMethodPreference.getSupportedPaymentTypes())
                .setShowBankDeals(mShowBankDeals)
                .setSupportMPApp(mSupportMPApp)
                .startPaymentMethodsActivity();
    }

    @Override
    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.getCustomer(mCheckoutPreference.getId(), new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {

                mCards = customer.getCards();

                // If the customer has saved cards show the first one, else show the payment methods step
                if ((mCards != null) && (mCards.size() > 0)) {

                    // Set selected payment method row
                    mSelectedPaymentMethodRow = CustomerCardsAdapter.getPaymentMethodRow(mActivity, mCards.get(0));

                    // Set customer method selection
                    setCustomerMethodSelection();

                } else {

                    // Show payment methods step
                    startPaymentMethodsActivity();

                    LayoutUtil.showRegularLayout(mActivity);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    @Override
    protected void resolveCreateTokenSuccess(String token) {

        // Set payment intent
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setToken(token);
        if (mSelectedIssuer != null) {
            paymentIntent.setIssuerId(mSelectedIssuer.getId());
        }
        paymentIntent.setInstallments(mSelectedPayerCost.getInstallments());
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());

        // Create payment
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createPayment(paymentIntent, new Callback<Payment>() {
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
