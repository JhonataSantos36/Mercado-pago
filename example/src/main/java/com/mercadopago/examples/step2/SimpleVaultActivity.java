package com.mercadopago.examples.step2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.examples.R;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPButton;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SimpleVaultActivity extends AppCompatActivity {

    // Activity parameters
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;
    protected List<String> mExcludedPaymentTypes;
    protected List<String> mExcludedPaymentMethodIds;

    // Input controls
    protected View mSecurityCodeCard;
    protected MPEditText mSecurityCodeText;
    protected FrameLayout mCustomerMethodsLayout;
    protected MPTextView mCustomerMethodsText;
    protected ImageView mCVVImage;
    protected MPTextView mCVVDescriptor;
    protected MPButton mSubmitButton;

    // Current values
    protected List<Card> mCards;
    protected CardToken mCardToken;
    protected PaymentMethodRow mSelectedPaymentMethodRow;
    protected PaymentMethod mSelectedPaymentMethod;
    protected PaymentMethod mTempPaymentMethod;
    protected PaymentPreference mPaymentPreference;


    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();
        createPaymentPreference();
        // Get activity parameters
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        if ((mMerchantPublicKey != null) && (!mMerchantPublicKey.equals(""))) {

            // Set activity
            mActivity = this;
            mActivity.setTitle(getString(R.string.mpsdk_title_activity_vault));

            // Set layout controls
            mSecurityCodeCard = findViewById(R.id.securityCodeCard);
            mCVVImage = (ImageView) findViewById(R.id.cVVImage);
            mCVVDescriptor = (MPTextView) findViewById(R.id.cVVDescriptor);
            mSubmitButton = (MPButton) findViewById(R.id.submitButton);
            mCustomerMethodsLayout = (FrameLayout) findViewById(R.id.customerMethodLayout);
            mCustomerMethodsText = (MPTextView) findViewById(R.id.customerMethodLabel);
            mSecurityCodeText = (MPEditText) findViewById(R.id.securityCode);

            // Init controls visibility
            mSecurityCodeCard.setVisibility(View.GONE);

            // Init MercadoPago object with public key
            mMercadoPago = new MercadoPago.Builder()
                    .setContext(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            // Set customer method first value
            mCustomerMethodsText.setText(getString(com.mercadopago.R.string.mpsdk_select_pm_label));

            // Set "Go" button
            setFormGoButton(mSecurityCodeText);

            // Hide main layout and go for customer's cards
            if ((mMerchantBaseUrl != null) && (!mMerchantBaseUrl.equals("") && (mMerchantGetCustomerUri != null) && (!mMerchantGetCustomerUri.equals("")))) {
                getCustomerCardsAsync();
            }
        }
        else {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            returnIntent.putExtra("message", "Invalid parameters");
            finish();
        }
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
        } else if (mExceptionOnMethod.equals("getCreateTokenCallback")) {
            if (mSelectedPaymentMethodRow != null) {
                createSavedCardToken();
            } else if (mCardToken != null) {
                createNewCardToken();
            }
        }
    }

    protected void createPaymentPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentMethodIds(this.mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(this.mExcludedPaymentTypes);
    }

    public void onCustomerMethodsClick(View view) {

        if ((mCards != null) && (mCards.size() > 0)) {  // customer cards activity

            new MercadoPago.StartActivityBuilder()
                    .setActivity(mActivity)
                    .setCards(mCards)
                    .startCustomerCardsActivity();

        } else {  // payment method activity

            new MercadoPago.StartActivityBuilder()
                    .setActivity(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .setPaymentPreference(mPaymentPreference)
                    .startPaymentMethodsActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {

            resolveCustomerCardsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {

            resolvePaymentMethodsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

            resolveNewCardRequest(resultCode, data);
        }
    }

    protected void setContentView() {

        setContentView(R.layout.activity_simple_vault);
    }

    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            PaymentMethodRow selectedPaymentMethodRow = (PaymentMethodRow) data.getSerializableExtra("paymentMethodRow");

            if (selectedPaymentMethodRow.getCard() != null) {

                // Set selection status
                mCardToken = null;
                mSelectedPaymentMethodRow = selectedPaymentMethodRow;
                mSelectedPaymentMethod = null;
                mTempPaymentMethod = null;

                // Set customer method selection
                setCustomerMethodSelection();

            } else {

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

            mTempPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

            // Call new cards activity
            startNewCardActivity();

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    protected void resolveNewCardRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mCardToken = (CardToken) data.getSerializableExtra("cardToken");
            mSelectedPaymentMethodRow = null;
            mSelectedPaymentMethod = mTempPaymentMethod;

            // Set customer method selection
            mCustomerMethodsText.setText(CustomerCardsAdapter.getPaymentMethodLabel(mActivity, mSelectedPaymentMethod.getName(),
                    mCardToken.getCardNumber().substring(mCardToken.getCardNumber().length() - 4, mCardToken.getCardNumber().length())));
            mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

            // Set security cards visibility
            showSecurityCodeCard(mSelectedPaymentMethod);

            // Set button visibility
            mSubmitButton.setEnabled(true);

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

    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {

                mCards = getSupportedCustomerCards(customer.getCards());
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    protected void showSecurityCodeCard(PaymentMethod paymentMethod) {

        if (paymentMethod != null) {

            if (isSecurityCodeRequired()) {

                if ("amex".equals(paymentMethod.getId())) {
                    mCVVDescriptor.setText(String.format(getString(com.mercadopago.R.string.mpsdk_cod_seg_desc_amex), 4));
                } else {
                    mCVVDescriptor.setText(String.format(getString(com.mercadopago.R.string.mpsdk_cod_seg_desc), 3));
                }

                int res = MercadoPagoUtil.getPaymentMethodImage(mActivity, paymentMethod.getId());
                if (res != 0) {
                    mCVVImage.setImageDrawable(getResources().getDrawable(res));
                }

                mSecurityCodeCard.setVisibility(View.VISIBLE);

            } else {

                mSecurityCodeCard.setVisibility(View.GONE);
            }
        }
    }

    protected String getSelectedPMBin() {

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().getFirstSixDigits();
        } else {
            return mCardToken.getCardNumber().substring(0, MercadoPago.BIN_LENGTH);
        }
    }

    private boolean isSecurityCodeRequired() {

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().isSecurityCodeRequired();
        } else {
            return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
        }
    }

    private void setFormGoButton(final MPEditText editText) {

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

        // Set payment method
        mSelectedPaymentMethod = mSelectedPaymentMethodRow.getCard().getPaymentMethod();

        // Set customer method selection
        mCustomerMethodsText.setText(mSelectedPaymentMethodRow.getLabel());
        mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(mSelectedPaymentMethodRow.getIcon(), 0, 0, 0);

        // Set security cards visibility
        showSecurityCodeCard(mSelectedPaymentMethodRow.getCard().getPaymentMethod());

        // Set button visibility
        mSubmitButton.setEnabled(true);
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Create token
        if (mSelectedPaymentMethodRow != null) {

            createSavedCardToken();

        } else if (mCardToken != null) {

            createNewCardToken();
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
            public void success(Token token, Response response) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("token", token.getId());
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

    protected void finishWithApiException(Intent data) {

        setResult(RESULT_CANCELED, data);
        finish();
    }

    protected void startNewCardActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mTempPaymentMethod)
                .setRequireSecurityCode(false)
                .startGuessingCardActivity();
    }

    protected void startPaymentMethodsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference)
                .startPaymentMethodsActivity();
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
}
