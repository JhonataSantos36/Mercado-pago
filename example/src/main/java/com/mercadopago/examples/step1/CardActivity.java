package com.mercadopago.examples.step1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardActivity extends AppCompatActivity {

    // Activity parameters
    private PaymentMethod mPaymentMethod;

    // Input controls
    private EditText mCardHolderName;
    private EditText mCardNumber;
    private TextView mExpiryError;
    private EditText mExpiryMonth;
    private EditText mExpiryYear;
    private RelativeLayout mIdentificationLayout;
    private EditText mIdentificationNumber;
    private Spinner mIdentificationType;
    private EditText mSecurityCode;

    // Current values
    private CardToken mCardToken;

    // Local vars
    private Activity mActivity;
    private String mExceptionOnMethod;
    private MercadoPago mMercadoPago;
    private String mMerchantPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mActivity = this;

        // Get activity parameters
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");

        // Set input controls
        mCardNumber = (EditText) findViewById(R.id.cardNumber);
        mSecurityCode = (EditText) findViewById(R.id.securityCode);
        mCardHolderName = (EditText) findViewById(R.id.cardholderName);
        mIdentificationNumber = (EditText) findViewById(R.id.identificationNumber);
        mIdentificationType = (Spinner) findViewById(R.id.identificationType);
        mIdentificationLayout = (RelativeLayout) findViewById(R.id.identificationLayout);
        mExpiryError = (TextView) findViewById(R.id.expiryError);
        mExpiryMonth = (EditText) findViewById(R.id.expiryMonth);
        mExpiryYear = (EditText) findViewById(R.id.expiryYear);

        // Init MercadoPago object with public key
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .build();

        // Set identification type listener to control identification number keyboard
        setIdentificationNumberKeyboardBehavior();

        // Error text cleaning hack
        setErrorTextCleaner(mCardHolderName);

        // Set payment method image
        mPaymentMethod = (PaymentMethod) this.getIntent().getSerializableExtra("paymentMethod");
        if ((mPaymentMethod != null) && (mPaymentMethod.getId() != null)) {
            ImageView pmImage = (ImageView) findViewById(com.mercadopago.R.id.pmImage);
            pmImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId()));
        }

        // Set up expiry edit texts
        mExpiryMonth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mExpiryError.setError(null);
                return false;
            }
        });
        mExpiryYear.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mExpiryError.setError(null);
                return false;
            }
        });

        // Get identification types
        getIdentificationTypesAsync();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        if (mExceptionOnMethod.equals("getIdentificationTypesAsync")) {
            getIdentificationTypesAsync();
        } else if (mExceptionOnMethod.equals("createTokenAsync")) {
            createTokenAsync();
        }
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Set cards token
        mCardToken = new CardToken(getCardNumber(), getMonth(), getYear(), getSecurityCode(), getCardHolderName(),
                getIdentificationTypeId(getIdentificationType()), getIdentificationNumber());

        if (validateForm(mCardToken)) {

            // Create token
            createTokenAsync();
        }
    }

    private boolean validateForm(CardToken cardToken) {

        boolean result = true;
        boolean focusSet = false;

        // Validate cards number
        try {
            validateCardNumber(cardToken);
            mCardNumber.setError(null);
        } catch (Exception ex) {
            mCardNumber.setError(ex.getMessage());
            mCardNumber.requestFocus();
            result = false;
            focusSet = true;
        }

        // Validate security code
        try {
            validateSecurityCode(cardToken);
            mSecurityCode.setError(null);
        } catch (Exception ex) {
            mSecurityCode.setError(ex.getMessage());
            if (!focusSet) {
                mSecurityCode.requestFocus();
                focusSet = true;
            }
            result = false;
        }

        // Validate expiry month and year
        if (!cardToken.validateExpiryDate()) {
            mExpiryError.setVisibility(View.VISIBLE);
            mExpiryError.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
            if (!focusSet) {
                mExpiryMonth.requestFocus();
                focusSet = true;
            }
            result = false;
        } else {
            mExpiryError.setError(null);
            mExpiryError.setVisibility(View.GONE);
        }

        // Validate cards holder name
        if (!cardToken.validateCardholderName()) {
            mCardHolderName.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
            if (!focusSet) {
                mCardHolderName.requestFocus();
                focusSet = true;
            }
            result = false;
        } else {
            mCardHolderName.setError(null);
        }

        // Validate identification number
        if (getIdentificationType() != null) {
            if (!cardToken.validateIdentificationNumber()) {
                mIdentificationNumber.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
                if (!focusSet) {
                    mIdentificationNumber.requestFocus();
                }
                result = false;
            } else {
                mIdentificationNumber.setError(null);
            }
        }

        return result;
    }

    protected void validateCardNumber(CardToken cardToken) throws Exception {

        cardToken.validateCardNumber(this, mPaymentMethod);
    }

    protected void validateSecurityCode(CardToken cardToken) throws Exception {

        cardToken.validateSecurityCode(this, mPaymentMethod);
    }

    private void getIdentificationTypesAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        mMercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes, Response response) {

                mIdentificationType.setAdapter(new IdentificationTypesAdapter(mActivity, identificationTypes));

                // Set form "Go" button
                setFormGoButton(mIdentificationNumber);

                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(RetrofitError error) {

                if ((error.getResponse() != null) && (error.getResponse().getStatus() == 404)) {

                    // No identification type for this country
                    mIdentificationLayout.setVisibility(View.GONE);

                    // Set form "Go" button
                    setFormGoButton(mCardHolderName);

                    LayoutUtil.showRegularLayout(mActivity);

                } else {

                    mExceptionOnMethod = "getIdentificationTypesAsync";
                    ApiUtil.finishWithApiException(mActivity, error);
                }
            }
        });
    }

    private void createTokenAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        mMercadoPago.createToken(mCardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("token", token.getId());
                returnIntent.putExtra("paymentMethod", mPaymentMethod);
                setResult(RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {

                mExceptionOnMethod = "createTokenAsync";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    private void setFormGoButton(final EditText editText) {

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

    private void setIdentificationNumberKeyboardBehavior() {

        mIdentificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IdentificationType identificationType = getIdentificationType();
                if (identificationType != null) {
                    if (identificationType.getType().equals("number")) {
                        mIdentificationNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        mIdentificationNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setErrorTextCleaner(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable edt) {
                if (editText.getText().length() > 0) {
                    editText.setError(null);
                }
            }
        });
    }

    private String getCardNumber() {

        return this.mCardNumber.getText().toString();
    }

    private String getSecurityCode() {

        return this.mSecurityCode.getText().toString();
    }

    private Integer getMonth() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryMonth.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    private Integer getYear() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryYear.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    private String getCardHolderName() {

        return this.mCardHolderName.getText().toString();
    }

    private IdentificationType getIdentificationType() {

        return (IdentificationType) mIdentificationType.getSelectedItem();
    }

    private String getIdentificationTypeId(IdentificationType identificationType) {

        if (identificationType != null) {
            return identificationType.getId();
        } else {
            return null;
        }
    }

    private String getIdentificationNumber() {

        if (!this.mIdentificationNumber.getText().toString().equals("")) {
            return this.mIdentificationNumber.getText().toString();
        } else {
            return null;
        }
    }
}