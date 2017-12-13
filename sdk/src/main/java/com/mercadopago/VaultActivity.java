package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class VaultActivity extends AppCompatActivity {

    // Activity parameters
    protected BigDecimal mAmount;
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;
    protected boolean mShowBankDeals;
    protected List<String> mSupportedPaymentTypes;
    protected Site mSite;

    // Input controls
    protected View mInstallmentsCard;
    protected View mSecurityCodeCard;
    protected MPEditText mSecurityCodeText;
    protected LinearLayout mCustomerMethodsLayout;
    protected MPTextView mCustomerMethodsText;
    protected ImageView mCustomerMethodsImage;
    protected FrameLayout mInstallmentsLayout;
    protected MPTextView mInstallmentsText;
    protected ImageView mCVVImage;
    protected MPTextView mCVVDescriptor;
    protected MPButton mSubmitButton;

    // Current values
    protected List<Card> mCards;
    protected List<PayerCost> mPayerCosts;
    protected CardToken mCardToken;
    protected PayerCost mSelectedPayerCost;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected Issuer mTempIssuer;
    protected PaymentMethod mTempPaymentMethod;
    protected Card mSelectedCard;

    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPagoServicesAdapter mMercadoPago;
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView();

        getActivityParameters();

        if (validParameters()) {

            setActivity();

            // Set layout controls
            mInstallmentsCard = findViewById(R.id.mpsdkInstallmentsCard);
            mSecurityCodeCard = findViewById(R.id.mpsdkSecurityCodeCard);
            mCVVImage = findViewById(R.id.mpsdkCVVImage);
            mCVVDescriptor = findViewById(R.id.mpsdkCVVDescriptor);
            mSubmitButton = findViewById(R.id.mpsdkPayButton);
            mCustomerMethodsLayout = findViewById(R.id.mpsdkCustomerMethodLayout);
            mCustomerMethodsText = findViewById(R.id.mpsdkCustomerMethodLabel);
            mCustomerMethodsImage = findViewById(R.id.mpsdkCustomerMethodImage);
            mInstallmentsLayout = findViewById(R.id.mpsdkInstallmentsLayout);
            mInstallmentsText = findViewById(R.id.mpsdkInstallmentsLabel);
            mSecurityCodeText = findViewById(R.id.mpsdkSecurityCode);

            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitForm();
                }
            });

            // Init MercadoPago object with public key
            mMercadoPago = new MercadoPagoServicesAdapter.Builder()
                    .setContext(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            // Init controls visibility
            mInstallmentsCard.setVisibility(View.GONE);
            mSecurityCodeCard.setVisibility(View.GONE);

            // Set customer method first value
            mCustomerMethodsText.setText(getString(com.mercadopago.R.string.mpsdk_select_pm_label));

            setFormGoButton(mSecurityCodeText);

            String siteId = mSite == null ? "" : mSite.getId();
            initPaymentFlow();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "Invalid parameters");
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    public void getActivityParameters() {
        setAmount();
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        mSite = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);

        if (getIntent().getStringExtra("paymentPreference") != null) {
            mPaymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        }

        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
        }

        //Give priority to PaymentPreference over supported payment types
        if (!isPaymentPreferenceSet() && supportedPaymentTypesSet()) {
            List<String> excludedPaymentTypes = new ArrayList<>();
            for (String type : PaymentTypes.getAllPaymentTypes()) {
                if (!mSupportedPaymentTypes.contains(type)) {
                    excludedPaymentTypes.add(type);
                }
            }
            mPaymentPreference = new PaymentPreference();
            mPaymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        }
    }

    private boolean supportedPaymentTypesSet() {
        return mSupportedPaymentTypes != null;
    }

    private boolean isPaymentPreferenceSet() {
        return mPaymentPreference != null;
    }

    protected void setContentView() {

        setContentView(R.layout.mpsdk_activity_vault);
    }

    protected void setAmount() {

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }
    }

    protected boolean validParameters() {

        boolean valid = false;
        if ((mMerchantPublicKey != null) && (mAmount != null)) {
            valid = true;
        }
        return valid;
    }

    protected void setActivity() {

        mActivity = this;
        mActivity.setTitle(getString(R.string.mpsdk_title_activity_vault));
    }

    protected void initPaymentFlow() {

        // Show payment method selection or go for customer's cards
        if ((mMerchantBaseUrl != null) && (!mMerchantBaseUrl.equals("") && (mMerchantGetCustomerUri != null) && (!mMerchantGetCustomerUri.equals("")))) {
            getCustomerCardsAsync();
        } else {
            startPaymentMethodsActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.mpsdk_vault, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bank_deals) {
            startBankDealsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
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
            if (mSelectedCard != null) {
                createSavedCardToken();
            } else if (mCardToken != null) {
                createNewCardToken();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        onVaultActivityResult(requestCode, resultCode, data);
    }

    protected boolean onVaultActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPagoComponents.Activities.CUSTOMER_CARDS_REQUEST_CODE) {

            resolveCustomerCardsRequest(resultCode, data);

        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_METHODS_REQUEST_CODE) {

            resolvePaymentMethodsRequest(resultCode, data);

        } else if (requestCode == MercadoPagoComponents.Activities.INSTALLMENTS_REQUEST_CODE) {

            resolveInstallmentsRequest(resultCode, data);

        } else if (requestCode == MercadoPagoComponents.Activities.ISSUERS_REQUEST_CODE) {

            resolveIssuersRequest(resultCode, data);

        } else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

            resolveNewCardRequest(resultCode, data);

        }

        return (requestCode == MercadoPagoComponents.Activities.CUSTOMER_CARDS_REQUEST_CODE) ||
                (requestCode == MercadoPagoComponents.Activities.PAYMENT_METHODS_REQUEST_CODE) ||
                (requestCode == MercadoPagoComponents.Activities.INSTALLMENTS_REQUEST_CODE) ||
                (requestCode == MercadoPagoComponents.Activities.ISSUERS_REQUEST_CODE) ||
                (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE);

    }

    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Card selectedCard = JsonUtil.getInstance().fromJson(data.getStringExtra("card"), Card.class);

            if (selectedCard != null) {

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedCard = selectedCard;
                mSelectedPayerCost = null;
                mTempPaymentMethod = null;

                // Set customer method selection
                setCustomerMethodSelection();
            } else {
                startPaymentMethodsActivity();
            }
        } else if ((data != null) && (data.getStringExtra("mercadoPagoError") != null)) {
            finishWithMpException(data);
        }
    }

    protected String getPaymentMethodLabel(String name, String lastFourDigits) {
        return name + " " + getString(com.mercadopago.R.string.mpsdk_last_digits_label) + " " + lastFourDigits;
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if (MercadoPagoUtil.isCard(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new cards activity
                    startNewCardActivity();
                }

            } else if (mTempPaymentMethod.getId().equals(getResources().getString(R.string.mpsdk_mp_app_id))) {
                resolveMPAppNeeded();
            } else {  // Off-line methods

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedCard = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                mSelectedIssuer = null;

                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mCustomerMethodsImage.setImageDrawable(ContextCompat.getDrawable(this, MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId())));

                // Set security cards visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithMpException(data);
            } else if ((mSelectedCard == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    protected void resolveMPAppNeeded() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("MPAppNeeded", true);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);

            // Update installments view
            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithMpException(data);
            }
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);

            // Call new cards activity
            startNewCardActivity();

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {

                    finishWithMpException(data);

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
            mCardToken = JsonUtil.getInstance().fromJson(data.getStringExtra("cardToken"), CardToken.class);
            mSelectedCard = null;
            mSelectedPayerCost = null;
            mSelectedPaymentMethod = mTempPaymentMethod;
            mSelectedIssuer = mTempIssuer;

            this.refreshCardData();

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {

                    finishWithMpException(data);

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

    protected void refreshCardData() {
        // Set customer method selection
        mCustomerMethodsText.setText(getPaymentMethodLabel(mSelectedPaymentMethod.getName(),
                mCardToken.getCardNumber().substring(mCardToken.getCardNumber().length() - 4, mCardToken.getCardNumber().length())));
        mCustomerMethodsImage.setImageDrawable(ContextCompat.getDrawable(this, MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId())));

        // Set security cards visibility
        showSecurityCodeCard(mSelectedPaymentMethod);

        // Get installments
        getInstallmentsAsync();
    }

    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {

                mCards = customer.getCards();

                // If the customer has saved cards show the first one, else show the payment methods step
                if ((mCards != null) && (mCards.size() > 0)) {

                    // Set selected payment method row
                    mSelectedCard = mCards.get(0);

                    // Set customer method selection
                    setCustomerMethodSelection();

                } else {

                    // Show payment methods step

                    startPaymentMethodsActivity();

                    LayoutUtil.showRegularLayout(mActivity);
                }
            }

            @Override
            public void failure(ApiException apiException) {

                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
    }

    protected void getInstallmentsAsync() {

        String bin = getSelectedPMBin();
        BigDecimal amount = mAmount;
        Long issuerId = (mSelectedIssuer != null) ? mSelectedIssuer.getId() : null;
        String paymentMethodId = mSelectedPaymentMethod.getId();

        if (bin.length() == MercadoPagoUtil.BIN_LENGTH) {
            LayoutUtil.showProgressLayout(mActivity);
            mMercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, new Callback<List<Installment>>() {
                @Override
                public void success(List<Installment> installments) {

                    LayoutUtil.showRegularLayout(mActivity);

                    if ((installments.size() > 0) && (installments.get(0).getPayerCosts().size() > 0)) {

                        // Set installments cards data and visibility
                        mPayerCosts = installments.get(0).getPayerCosts();

                        mSelectedPayerCost = installments.get(0).getPayerCosts().get(0);

                        if (mPayerCosts.size() == 1) {

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
                public void failure(ApiException apiException) {
                    mExceptionOnMethod = "getInstallmentsAsync";
                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            });
        }
    }

    public void onCustomerMethodsClick(View view) {

        if ((mCards != null) && (mCards.size() > 0)) {  // customer cards activity

            startCustomerCardsActivity();

        } else {  // payment method activity
            startPaymentMethodsActivity();
        }
    }

    public void onInstallmentsClick(View view) {

        startInstallmentsActivity();
    }

    protected void showSecurityCodeCard(PaymentMethod paymentMethod) {

        if (paymentMethod != null) {

            if (isSecurityCodeRequired()) {

                // Set CVV descriptor
                mCVVDescriptor.setText(MercadoPagoUtil.getCVVDescriptor(this, paymentMethod));

                // Set CVV image
                mCVVImage.setImageDrawable(getResources().getDrawable(MercadoPagoUtil.getCVVImageResource(this, paymentMethod)));

                // Set cards visibility
                mSecurityCodeCard.setVisibility(View.VISIBLE);

                return;
            }
        }
        mSecurityCodeCard.setVisibility(View.GONE);
    }

    protected boolean isSecurityCodeRequired() {

        if (mSelectedCard != null) {
            return mSelectedCard.isSecurityCodeRequired();
        } else {
            return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
        }
    }

    protected String getSelectedPMBin() {

        if (mSelectedCard != null) {
            return mSelectedCard.getFirstSixDigits();
        } else {
            return mCardToken.getCardNumber().substring(0, MercadoPagoUtil.BIN_LENGTH);
        }
    }

    protected void setFormGoButton(final MPEditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitForm();
                }
                return false;
            }
        });
    }

    protected void setCustomerMethodSelection() {

        // Set payment method and issuer
        mSelectedPaymentMethod = mSelectedCard.getPaymentMethod();
        mSelectedIssuer = mSelectedCard.getIssuer();

        // Set customer method selection
        mCustomerMethodsText.setText(getPaymentMethodLabel(mSelectedCard.getPaymentMethod().getName(), mSelectedCard.getLastFourDigits()));
        mCustomerMethodsImage.setImageDrawable(ContextCompat.getDrawable(this, MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId())));

        // Set security cards visibility
        showSecurityCodeCard(mSelectedCard.getPaymentMethod());

        // Get installments
        getInstallmentsAsync();
    }

    private void submitForm() {

        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (((mSelectedCard != null) || (mCardToken != null)) && mSelectedPayerCost == null) {
            return;
        }

        // Create token
        if (mSelectedCard != null) {

            createSavedCardToken();

        } else if (mCardToken != null) {

            createNewCardToken();

        } else {  // Off-line methods

            // Return payment method id
            LayoutUtil.showRegularLayout(mActivity);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    protected void createNewCardToken() {

        // Validate CVV
        try {
            mCardToken.setSecurityCode(mSecurityCodeText.getText().toString());
            mCardToken.validateSecurityCode(mSelectedPaymentMethod);
            mSecurityCodeText.setError(null);
        } catch (Exception ex) {
            mSecurityCodeText.setError(ex.getMessage());
            mSecurityCodeText.requestFocus();
            return;
        }

        // Create token
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createToken(mCardToken, getCreateTokenCallback());
    }

    protected void createSavedCardToken() {

        SavedCardToken savedCardToken = new SavedCardToken(mSelectedCard.getId(), mSecurityCodeText.getText().toString());

        // Validate CVV
        try {
            savedCardToken.validateSecurityCode(mSelectedCard);
            mSecurityCodeText.setError(null);
        } catch (Exception ex) {
            mSecurityCodeText.setError(ex.getMessage());
            mSecurityCodeText.requestFocus();
            return;
        }

        // Create token
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createToken(savedCardToken, getCreateTokenCallback());
    }

    protected Callback<Token> getCreateTokenCallback() {

        return new Callback<Token>() {
            @Override
            public void success(Token o) {

                resolveCreateTokenSuccess(o.getId());
            }

            @Override
            public void failure(ApiException apiException) {

                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        };
    }

    protected void resolveCreateTokenSuccess(String token) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", token);
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuerId", Long.toString(mSelectedIssuer.getId()));
        }
        returnIntent.putExtra("installments", Integer.toString(mSelectedPayerCost.getInstallments()));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    protected void finishWithMpException(Intent data) {

        setResult(RESULT_CANCELED, data);
        finish();
    }

    protected void startBankDealsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .startBankDealsActivity();
    }

    protected void startCustomerCardsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setCards(mCards)
                .startCustomerCardsActivity();
    }

    protected void startInstallmentsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setSite(mSite)
                .setAmount(mAmount)
                .setPayerCosts(mPayerCosts)
                .startInstallmentsActivity();
    }

    protected void startIssuersActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mTempPaymentMethod)
                .startIssuersActivity();
    }

    protected void startNewCardActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mTempPaymentMethod)
                .setRequireSecurityCode(false)
                .startNewCardActivity();
    }

    protected void startPaymentMethodsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference)
                .setShowBankDeals(mShowBankDeals)
                .startPaymentMethodsActivity();
    }
}