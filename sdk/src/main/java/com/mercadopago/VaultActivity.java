package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VaultActivity extends AppCompatActivity {

    // Activity parameters
    protected BigDecimal mAmount;
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;
    protected boolean mShowBankDeals;
    protected boolean mCardGuessingEnabled;
    protected Integer mDefaultInstallments;
    protected Integer mMaxInstallments;
    protected List<String> mExcludedPaymentMethodIds;
    protected List<String> mExcludedPaymentTypes;
    protected String mDefaultPaymentMethodId;
    protected Boolean mSupportMPApp;

    // Input controls
    protected View mInstallmentsCard;
    protected View mSecurityCodeCard;
    protected MPEditText mSecurityCodeText;
    protected FrameLayout mCustomerMethodsLayout;
    protected MPTextView mCustomerMethodsText;
    protected FrameLayout mInstallmentsLayout;
    protected MPTextView mInstallmentsText;
    protected ImageView mCVVImage;
    protected MPTextView mCVVDescriptor;
    protected MPButton mSubmitButton;

    // Current values
    protected List<Card> mCards;
    protected List<PayerCost> mPayerCosts;
    protected CardToken mCardToken;
    protected PaymentMethodRow mSelectedPaymentMethodRow;
    protected PayerCost mSelectedPayerCost;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected Issuer mTempIssuer;
    protected PaymentMethod mTempPaymentMethod;

    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView();

        getActivityParameters();

        if (validParameters()) {

            setActivity();

            // Set layout controls
            mInstallmentsCard = findViewById(R.id.installmentsCard);
            mSecurityCodeCard = findViewById(R.id.securityCodeCard);
            mCVVImage = (ImageView) findViewById(R.id.cVVImage);
            mCVVDescriptor = (MPTextView) findViewById(R.id.cVVDescriptor);
            mSubmitButton = (MPButton) findViewById(R.id.payButton);
            mCustomerMethodsLayout = (FrameLayout) findViewById(R.id.customerMethodLayout);
            mCustomerMethodsText = (MPTextView) findViewById(R.id.customerMethodLabel);
            mInstallmentsLayout = (FrameLayout) findViewById(R.id.installmentsLayout);
            mInstallmentsText = (MPTextView) findViewById(R.id.installmentsLabel);
            mSecurityCodeText = (MPEditText) findViewById(R.id.securityCode);

            // Init MercadoPago object with public key
            mMercadoPago = new MercadoPago.Builder()
                    .setContext(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            // Init controls visibility
            mInstallmentsCard.setVisibility(View.GONE);
            mSecurityCodeCard.setVisibility(View.GONE);

            // Set customer method first value
            mCustomerMethodsText.setText(getString(com.mercadopago.R.string.mpsdk_select_pm_label));

            setFormGoButton(mSecurityCodeText);

            initPaymentFlow();
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "Invalid parameters");
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    public void getActivityParameters()
    {
        setAmount();
        mSupportMPApp = this.getIntent().getBooleanExtra("supportMPApp", false);
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        mCardGuessingEnabled = this.getIntent().getBooleanExtra("cardGuessingEnabled", false);
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if (this.getIntent().getSerializableExtra("paymentPreference") != null) {
            mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        }

    }

    protected void setContentView() {

        setContentView(R.layout.activity_vault);
    }

    protected void setAmount() {

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }
    }

    protected boolean validParameters() {

        if ((mMerchantPublicKey != null) && (mAmount != null)) {
            return true;
        }
        return false;
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
            if(mCardGuessingEnabled)
                startGuessingCardActivity();
            else
                startPaymentMethodsActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.vault, menu);
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
            if (mSelectedPaymentMethodRow != null) {
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

        } else if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {

            resolveGuessingCardRequest(resultCode, data);
        }

        return (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) ||
                (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) ||
                (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) ||
                (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) ||
                (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE);

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
            }

            else if (selectedPaymentMethodRow.getLabel().equals(getResources().getString(R.string.mpsdk_mp_app_name))) {

                resolveMPAppNeeded();

            } else {

                if(mCardGuessingEnabled)
                    startGuessingCardActivity();
                else
                    startPaymentMethodsActivity();
            }
        } else {

            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
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

                    // Call new cards activity
                    startNewCardActivity();
                }

            }
            else if (mTempPaymentMethod.getId().equals(getResources().getString(R.string.mpsdk_mp_app_id))) {
               resolveMPAppNeeded();
            }

            else {  // Off-line methods

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

                // Set security cards visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
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
            mSelectedPayerCost = (PayerCost) data.getSerializableExtra("payerCost");

            // Update installments view
            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());

        } else {

            if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = (Issuer) data.getSerializableExtra("issuer");

            // Call new cards activity
            startNewCardActivity();

        } else {

            if (data != null) {
                if (data.getSerializableExtra("apiException") != null) {

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
                if (data.getSerializableExtra("apiException") != null) {

                    finishWithApiException(data);

                } else if (data.getBooleanExtra("backButtonPressed", false)) {

                    if (mTempPaymentMethod.isIssuerRequired()) {

                        startIssuersActivity();

                    } else {

                        if(mCardGuessingEnabled)
                            startGuessingCardActivity();
                        else
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

        // Set security cards visibility
        showSecurityCodeCard(mSelectedPaymentMethod);

        // Get installments
        getInstallmentsAsync();
    }

    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {

                List<Card> customerCards = customer.getCards();
                if (customerCards != null)
                    mCards = getSupportedCustomerCards(customerCards);

                // If the customer has saved cards show the first one, else show the payment methods step
                if ((mCards != null) && (mCards.size() > 0)) {

                    // Set selected payment method row
                    mSelectedPaymentMethodRow = CustomerCardsAdapter.getPaymentMethodRow(mActivity, mCards.get(0));

                    // Set customer method selection
                    setCustomerMethodSelection();

                } else {

                    // Show payment methods step
                    if (mCardGuessingEnabled)
                        startGuessingCardActivity();
                    else
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

    protected void getInstallmentsAsync() {

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

                        // Set installments cards data and visibility
                        mPayerCosts = installments.get(0).getPayerCosts();
                        PayerCost defaultPayerCost = null;

                        if(mPaymentPreference != null) {
                            mPayerCosts = mPaymentPreference.getInstallmentsBelowMax(mPayerCosts);
                            defaultPayerCost = mPaymentPreference.getDefaultInstallments(mPayerCosts);
                        }

                        if(defaultPayerCost != null) {
                            mSelectedPayerCost = defaultPayerCost;
                        }
                        else {
                            mSelectedPayerCost = installments.get(0).getPayerCosts().get(0);
                        }

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
                public void failure(RetrofitError error) {

                    mExceptionOnMethod = "getInstallmentsAsync";
                    ApiUtil.finishWithApiException(mActivity, error);
                }
            });
        }
    }

    public void onCustomerMethodsClick(View view) {

        if ((mCards != null) && (mCards.size() > 0)) {  // customer cards activity

            startCustomerCardsActivity();

        } else {  // payment method activity
            if(mCardGuessingEnabled)
                startGuessingCardActivity();
            else
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

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().isSecurityCodeRequired();
        } else {
            return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
        }
    }

    protected String getSelectedPMBin() {

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().getFirstSixDigits();
        } else {
            return mCardToken.getCardNumber().substring(0, MercadoPago.BIN_LENGTH);
        }
    }

    protected void setFormGoButton(final MPEditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitForm(v);
                }
                return false;
            }
        });
    }

    protected void setCustomerMethodSelection() {

        // Set payment method and issuer
        mSelectedPaymentMethod = mSelectedPaymentMethodRow.getCard().getPaymentMethod();
        mSelectedIssuer = mSelectedPaymentMethodRow.getCard().getIssuer();

        // Set customer method selection
        mCustomerMethodsText.setText(mSelectedPaymentMethodRow.getLabel());
        mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(mSelectedPaymentMethodRow.getIcon(), 0, 0, 0);

        // Set security cards visibility
        showSecurityCodeCard(mSelectedPaymentMethodRow.getCard().getPaymentMethod());

        // Get installments
        getInstallmentsAsync();
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (((mSelectedPaymentMethodRow != null) || (mCardToken != null)) && mSelectedPayerCost == null) {
            return;
        }

        // Create token
        if (mSelectedPaymentMethodRow != null) {

            createSavedCardToken();

        } else if (mCardToken != null) {

            createNewCardToken();

        } else {  // Off-line methods

            // Return payment method id
            LayoutUtil.showRegularLayout(mActivity);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    protected void createNewCardToken() {

        // Validate CVV
        try {
            mCardToken.setSecurityCode(mSecurityCodeText.getText().toString());
            mCardToken.validateSecurityCode(this, mSelectedPaymentMethod);
            mSecurityCodeText.setError(null);
        }
        catch (Exception ex) {
            mSecurityCodeText.setError(ex.getMessage());
            mSecurityCodeText.requestFocus();
            return;
        }

        // Create token
        LayoutUtil.showProgressLayout(mActivity);
        mMercadoPago.createToken(mCardToken, getCreateTokenCallback());
    }

    protected void createSavedCardToken() {

        SavedCardToken savedCardToken = new SavedCardToken(mSelectedPaymentMethodRow.getCard().getId(), mSecurityCodeText.getText().toString());

        // Validate CVV
        try {
            savedCardToken.validateSecurityCode(this, mSelectedPaymentMethodRow.getCard());
            mSecurityCodeText.setError(null);
        }
        catch (Exception ex) {
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
            public void success(Token o, Response response) {

                resolveCreateTokenSuccess(o.getId());
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(mActivity, error);
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
        returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private List<Card> getSupportedCustomerCards(List<Card> cards) {
        List<Card> supportedCards = new ArrayList<>();
        if(mPaymentPreference != null) {
            for (Card card : cards) {
                if (mPaymentPreference.isPaymentMethodSupported(card.getPaymentMethod()))
                    supportedCards.add(card);
            }
            return supportedCards;
        }
        else
            return cards;
    }


    protected void finishWithApiException(Intent data) {

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
                .setSupportMPApp(mSupportMPApp)
                .startCustomerCardsActivity();
    }

    protected void startInstallmentsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
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
                .setSupportMPApp(mSupportMPApp)
                .startPaymentMethodsActivity();
    }

    protected void startGuessingCardActivity(){
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setRequireSecurityCode(false)
                .setRequireIssuer(true)
                .setShowBankDeals(mShowBankDeals)
                .setPaymentPreference(mPaymentPreference)
                .startGuessingCardActivity();
    }
}
