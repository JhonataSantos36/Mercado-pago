package com.mercadopago;

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

import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

@Deprecated
public class NewCardActivity extends AppCompatActivity {

    // Activity parameters
    protected String mKey;
    protected String mKeyType;
    protected PaymentMethod mPaymentMethod;
    protected Boolean mRequireSecurityCode;

    // Input controls
    protected MPEditText mCardHolderName;
    protected MPEditText mCardNumber;
    protected MPTextView mCVVDescriptor;
    protected ImageView mCVVImage;
    protected MPTextView mExpiryError;
    protected MPEditText mExpiryMonth;
    protected MPEditText mExpiryYear;
    protected MPEditText mIdentificationNumber;
    protected RelativeLayout mIdentificationLayout;
    protected Spinner mIdentificationType;
    protected RelativeLayout mSecurityCodeLayout;
    protected MPEditText mSecurityCode;
    protected MPButton mSubmitButton;

    // Local vars
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        mActivity = this;

        getActivityParameters();

        verifyValidCreate();

        setInputControls();

        // Set identification type listener to control identification number keyboard
        setIdentificationNumberKeyboardBehavior();

        // Error text cleaning hack
        setErrorTextCleaner(mCardHolderName);

        getIdentificationTypesAsync();

        setPaymentMethodImage();

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

        // Set security code visibility
        setSecurityCodeLayout();
        setFocusOrder();
    }

    protected void getActivityParameters() {
        mKeyType = this.getIntent().getStringExtra("keyType");
        mKey = this.getIntent().getStringExtra("key");
        mRequireSecurityCode = this.getIntent().getBooleanExtra("requireSecurityCode", true);
        mPaymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
    }

    protected void setInputControls() {
        mCardNumber = findViewById(R.id.mpsdkCardNumber);
        mCardHolderName = findViewById(R.id.mpsdkCardholderName);
        mIdentificationNumber = findViewById(R.id.mpsdkIdentificationNumber);
        mIdentificationType = findViewById(R.id.mpsdkIdentificationType);
        mIdentificationLayout = findViewById(R.id.mpsdkIdentificationLayout);
        mSecurityCodeLayout = findViewById(R.id.mpsdkSecurityCodeLayout);
        mCVVImage = findViewById(R.id.mpsdkCVVImage);
        mCVVDescriptor = findViewById(R.id.mpsdkCVVDescriptor);
        mSecurityCode = findViewById(R.id.mpsdkSecurityCode);
        mExpiryError = findViewById(R.id.mpsdkExpiryError);
        mExpiryMonth = findViewById(R.id.mpsdkExpiryMonth);
        mExpiryYear = findViewById(R.id.mpsdkExpiryYear);
        mSubmitButton = findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    protected void setPaymentMethodImage() {
        if (mPaymentMethod.getId() != null) {
            ImageView pmImage = (ImageView) findViewById(R.id.mpsdkPmImage);
            if (pmImage != null) {
                pmImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId()));
            }
        }
    }

    protected void verifyValidCreate() {
        if ((mPaymentMethod == null) || (mKeyType == null) || (mKey == null)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setContentView() {

        setContentView(R.layout.mpsdk_activity_new_card);
    }

    protected void setFocusOrder() {
        mCardNumber.setNextFocusDownId(R.id.mpsdkExpiryMonth);
        mExpiryMonth.setNextFocusDownId(R.id.mpsdkExpiryYear);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        getIdentificationTypesAsync();
    }

    private void submitForm() {

        LayoutUtil.hideKeyboard(mActivity);

        // Set cards token
        CardToken cardToken = new CardToken(getCardNumber(), getMonth(), getYear(), getSecurityCode(), getCardHolderName(),
                getIdentificationTypeId(getIdentificationType()), getIdentificationNumber());

        if (validateForm(cardToken)) {
            // Return to parent
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    protected boolean validateForm(CardToken cardToken) {

        boolean result = true;
        boolean requestFocus = true;

        if (!validateCardNumber(cardToken, requestFocus)) {
            result = false;
            requestFocus = false;
        }

        if (!validateSecurityCode(cardToken, requestFocus)) {
            result = false;
            requestFocus = false;
        }

        if (!validateExpiryDate(cardToken, requestFocus)) {
            result = false;
            requestFocus = false;
        }

        if (!validateCardHolderName(cardToken, requestFocus)) {
            result = false;
            requestFocus = false;
        }

        if (!validateIdentificationNumber(cardToken, requestFocus)) {
            result = false;
        }

        return result;
    }

    protected boolean validateCardNumber(CardToken cardToken, boolean requestFocus) {

        try {
            validateCardNumber(cardToken);
            mCardNumber.setError(null);
        } catch (Exception ex) {
            mCardNumber.setError(ex.getMessage());
            if (requestFocus)
                mCardNumber.requestFocus();
            return false;
        }
        return true;
    }

    protected boolean validateIdentificationNumber(CardToken cardToken, boolean requestFocus) {
        if (getIdentificationType() != null) {
            if (!cardToken.validateIdentificationNumber(getIdentificationType())) {
                mIdentificationNumber.setError(getString(R.string.mpsdk_invalid_field));
                if (requestFocus) {
                    mIdentificationNumber.requestFocus();
                }
                return false;
            } else {
                mIdentificationNumber.setError(null);
            }
        }
        return true;
    }

    protected boolean validateCardHolderName(CardToken cardToken, Boolean requestFocus) {
        if (!cardToken.validateCardholderName()) {
            mCardHolderName.setError(getString(R.string.mpsdk_invalid_field));
            if (requestFocus) {
                mCardHolderName.requestFocus();
            }
            return false;
        } else {
            mCardHolderName.setError(null);
        }
        return true;
    }

    protected boolean validateExpiryDate(CardToken cardToken, Boolean requestFocus) {
        if (!cardToken.validateExpiryDate()) {
            mExpiryError.setVisibility(View.VISIBLE);
            mExpiryError.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
            if (requestFocus) {
                mExpiryMonth.requestFocus();
            }
            return false;
        } else {
            mExpiryError.setError(null);
            mExpiryError.setVisibility(View.GONE);
        }
        return true;
    }

    protected boolean validateSecurityCode(CardToken cardToken, boolean requestFocus) {
        if (mRequireSecurityCode) {
            try {
                validateSecurityCode(cardToken);
                mSecurityCode.setError(null);
            } catch (Exception ex) {
                mSecurityCode.setError(ex.getMessage());
                if (requestFocus) {
                    mSecurityCode.requestFocus();
                }
                return false;
            }
        }
        return true;
    }

    protected void validateCardNumber(CardToken cardToken) throws Exception {

        cardToken.validateCardNumber(mPaymentMethod);
    }

    protected void validateSecurityCode(CardToken cardToken) throws Exception {

        cardToken.validateSecurityCode(mPaymentMethod);
    }

    protected void getIdentificationTypesAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        MercadoPagoServicesAdapter mercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(mActivity)
                .setPublicKey(mKey)
                .build();

        mercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {

                mIdentificationType.setAdapter(new IdentificationTypesAdapter(mActivity, identificationTypes));

                // Set form "Go" button
                if (mSecurityCodeLayout.getVisibility() == View.GONE) {
                    setFormGoButton(mIdentificationNumber);
                }

                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {

                if ((apiException.getStatus() != null) && (apiException.getStatus() == 404)) {

                    // No identification type for this country
                    mIdentificationLayout.setVisibility(View.GONE);

                    // Set form "Go" button
                    if (mSecurityCodeLayout.getVisibility() == View.GONE) {
                        setFormGoButton(mCardHolderName);
                    }

                    LayoutUtil.showRegularLayout(mActivity);

                } else {

                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            }
        });
    }

    protected void setFormGoButton(final EditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitForm();
                    return true;
                }
                return false;
            }
        });
    }

    protected void setIdentificationNumberKeyboardBehavior() {

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

    protected void setErrorTextCleaner(final EditText editText) {

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

    protected void setSecurityCodeLayout() {

        if (mPaymentMethod != null) {

            if (mRequireSecurityCode) {

                // Set CVV descriptor
                mCVVDescriptor.setText(MercadoPagoUtil.getCVVDescriptor(this, mPaymentMethod));

                // Set CVV image
                mCVVImage.setImageDrawable(getResources().getDrawable(MercadoPagoUtil.getCVVImageResource(this, mPaymentMethod)));

                // Set layout visibility
                mSecurityCodeLayout.setVisibility(View.VISIBLE);
                setFormGoButton(mSecurityCode);

                return;
            }
        }
        mSecurityCodeLayout.setVisibility(View.GONE);
    }

    protected String getCardNumber() {

        return this.mCardNumber.getText().toString();
    }

    protected String getSecurityCode() {

        return this.mSecurityCode.getText().toString();
    }

    protected Integer getMonth() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryMonth.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    protected Integer getYear() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryYear.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    protected String getCardHolderName() {

        return this.mCardHolderName.getText().toString();
    }

    protected IdentificationType getIdentificationType() {

        return (IdentificationType) mIdentificationType.getSelectedItem();
    }

    protected String getIdentificationTypeId(IdentificationType identificationType) {

        if (identificationType != null) {
            return identificationType.getId();
        } else {
            return null;
        }
    }

    protected String getIdentificationNumber() {

        if (!this.mIdentificationNumber.getText().toString().equals("")) {
            return this.mIdentificationNumber.getText().toString();
        } else {
            return null;
        }
    }
}