package com.mercadopago.examples.step3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.step2.SimpleVaultActivity;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdvancedVaultActivity extends SimpleVaultActivity {

    protected BigDecimal mAmount;
    protected View mInstallmentsCard;
    protected FrameLayout mInstallmentsLayout;
    protected TextView mInstallmentsText;
    protected List<PayerCost> mPayerCosts;
    protected PayerCost mSelectedPayerCost;
    protected Issuer mSelectedIssuer;
    protected Issuer mTempIssuer;
    protected Boolean mGuessingCardEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }

        if (mAmount != null) {

            mInstallmentsCard = findViewById(com.mercadopago.R.id.installmentsCard);
            mInstallmentsLayout = (FrameLayout) findViewById(R.id.installmentsLayout);
            mInstallmentsText = (TextView) findViewById(R.id.installmentsLabel);
            mInstallmentsCard.setVisibility(View.GONE);

        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "Invalid parameters");
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
        mGuessingCardEnabled = getIntent().getBooleanExtra("cardGuessingEnabled", false);
    }

    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_advanced_vault);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        // Retry method call
        if (mExceptionOnMethod.equals("getCustomerCardsAsync")) {
            getCustomerCardsAsync();
        } else if (mExceptionOnMethod.equals("getInstallmentsAsync")) {
            getInstallmentsAsync();
        } else if (mExceptionOnMethod.equals("getCreateTokenCallback")) {
            if (mSelectedPaymentMethodRow != null) {
                createSavedCardToken();
            } else if (mCardToken != null) {
                createNewCardToken();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {

            resolveCustomerCardsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {

            resolvePaymentMethodsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {

            resolveInstallmentsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {

            resolveIssuersRequest(resultCode, data);

        } else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

            resolveNewCardRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {

            resolveGuessingCardRequest(resultCode, data);
        }
    }

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
                if(mGuessingCardEnabled)
                    startGuessingCardActivity();
                else
                    startPaymentMethodsActivity();
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

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

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

            // Update installments view
            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = (Issuer) data.getSerializableExtra("issuer");

            // Call new card activity
            startNewCardActivity();

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {

                    finishWithApiException(data);

                } else if (data.getBooleanExtra("backButtonPressed", false)) {

                    startPaymentMethodsActivity();
                }
            }
        }
    }

    protected void resolveNewCardRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mPayerCosts = null;
            mCardToken = (CardToken) data.getSerializableExtra("cardToken");
            mSelectedPaymentMethodRow = null;
            mSelectedPayerCost = null;
            mSelectedPaymentMethod = mTempPaymentMethod;
            mSelectedIssuer = mTempIssuer;

           this.refreshCardData();

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {

                    finishWithApiException(data);

                } else if (data.getBooleanExtra("backButtonPressed", false)) {

                    if (mTempPaymentMethod.isIssuerRequired()) {

                        startIssuersActivity();

                    } else {

                        startPaymentMethodsActivity();
                    }
                }
            }
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            mSelectedPaymentMethodRow = null;
            mSelectedPayerCost = null;

            mTempPaymentMethod = null;
            mTempIssuer =  null;

            mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mCardToken = (CardToken) data.getSerializableExtra("cardToken");
            mSelectedIssuer = (Issuer) data.getSerializableExtra("issuer");

            this.refreshCardData();

        }
        else if ((data != null) && (data.getStringExtra("apiException") != null)) {
            finishWithApiException(data);
        }
    }

    protected void refreshCardData() {
        // Set customer method selection
        mCustomerMethodsText.setText(CustomerCardsAdapter.getPaymentMethodLabel(mActivity, mSelectedPaymentMethod.getName(),
                mCardToken.getCardNumber().substring(mCardToken.getCardNumber().length() - 4, mCardToken.getCardNumber().length())));
        mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

        // Set security card visibility
        showSecurityCodeCard(mSelectedPaymentMethod);

        // Get installments
        getInstallmentsAsync();
    }
    private void getInstallmentsAsync() {

        String bin = getSelectedPMBin();
        BigDecimal amount = mAmount;
        Long issuerId = (mSelectedIssuer != null) ? mSelectedIssuer.getId() : null;
        String paymentTypeId = mSelectedPaymentMethod.getPaymentTypeId();

        if (bin.length() == MercadoPago.BIN_LENGTH) {
            LayoutUtil.showProgressLayout(mActivity);
            mMercadoPago.getInstallments(bin, amount, issuerId, paymentTypeId, new Callback<List<Installment>>() {
                @Override
                public void success(List<Installment> installments, Response response) {

                    LayoutUtil.showRegularLayout(mActivity);

                    if ((installments.size() > 0) && (installments.get(0).getPayerCosts().size() > 0)) {

                        // Set installments card data and visibility
                        mPayerCosts = installments.get(0).getPayerCosts();
                        mSelectedPayerCost = installments.get(0).getPayerCosts().get(0);

                        if (installments.get(0).getPayerCosts().size() == 1) {

                            mInstallmentsCard.setVisibility(View.GONE);

                        } else {

                            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());
                            mInstallmentsCard.setVisibility(View.VISIBLE);
                        }

                        // Set button visibility
                        mSubmitButton.setEnabled(true);

                    } else {
                        Toast.makeText(getApplicationContext(), getString(com.mercadopago.R.string.mpsdk_invalid_pm_for_current_amount), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    mExceptionOnMethod = "getInstallmentsAsync";
                    ApiUtil.finishWithApiException(mActivity, error);
                }
            });
        }
    }

    public void onInstallmentsClick(View view) {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPayerCosts(mPayerCosts)
                .startInstallmentsActivity();
    }

    protected void setCustomerMethodSelection() {

        // Set payment method and issuer
        mSelectedPaymentMethod = mSelectedPaymentMethodRow.getCard().getPaymentMethod();
        mSelectedIssuer = mSelectedPaymentMethodRow.getCard().getIssuer();

        // Set customer method selection
        mCustomerMethodsText.setText(mSelectedPaymentMethodRow.getLabel());
        mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(mSelectedPaymentMethodRow.getIcon(), 0, 0, 0);

        // Set security card visibility
        showSecurityCodeCard(mSelectedPaymentMethodRow.getCard().getPaymentMethod());

        // Get installments
        getInstallmentsAsync();
    }

    @Override
    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (mSelectedPayerCost == null) {
            return;
        }

        super.submitForm(view);
    }

    @Override
    protected Callback<Token> getCreateTokenCallback() {

        return new Callback<Token>() {
            @Override
            public void success(Token o, Response response) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("token", o.getId());
                if (mSelectedIssuer != null) {
                    returnIntent.putExtra("issuerId", Long.toString(mSelectedIssuer.getId()));
                }
                returnIntent.putExtra("installments", Integer.toString(mSelectedPayerCost.getInstallments()));
                returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
                setResult(RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        };
    }

    protected void startIssuersActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mTempPaymentMethod)
                .startIssuersActivity();
    }

    private void startGuessingCardActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setRequireIssuer(true)
                .setRequireSecurityCode(false)
                .startGuessingCardActivity();
    }
}
