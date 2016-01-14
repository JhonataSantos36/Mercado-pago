package com.mercadopago;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mercadopago.adapters.IssuersSpinnerAdapter;
import com.mercadopago.adapters.PaymentMethodsSpinnerAdapter;
import com.mercadopago.core.MercadoPago;

import com.mercadopago.model.CardToken;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

/**
 * Created by mreverter on 20/11/15.
 */
public class GuessingNewCardActivityTest  extends BaseTest<GuessingNewCardActivity> {
    private GuessingNewCardActivity mActivity;
    private EditText mCardNumberText;
    private EditText mCardholderNameText;
    private EditText mExpiryMonthText;
    private EditText mExpiryYearText;
    private TextView mExpiryErrorText;
    private EditText mIdentificationNumberText;
    private TextView mCVVDescriptor;
    private ImageView mCVVImage;
    private EditText mSecurityCodeText;
    private ImageView mPaymentMethodImage;
    private Spinner mPaymentMethodsSpinner;
    private LinearLayout mPaymentMethodsLayout;
    private RelativeLayout mSecurityCodeLayout;

    private Spinner mIssuersSpinner;

    public GuessingNewCardActivityTest() {

        super(GuessingNewCardActivity.class);
    }

    public void testGetCardToken() {

        // Set activity
        doRegularStart();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                EditText fieldText;
                fieldText = (EditText) mActivity.findViewById(R.id.cardNumber);
                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.expiryMonth);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                fieldText = (EditText) mActivity.findViewById(R.id.expiryYear);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                fieldText = (EditText) mActivity.findViewById(R.id.cardholderName);
                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                fieldText = (EditText) mActivity.findViewById(R.id.identificationNumber);
                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });

        // Validate the result
        try {
            ActivityResult activityResult = getActivityResult(mActivity);
            CardToken cardToken = (CardToken) activityResult.getExtras().getSerializable("cardToken");


            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
            assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        } catch (Exception ex) {
            fail("Get card token test failed, cause: " + ex.getMessage());
        }
    }

    public void testEmptyCardNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText("");
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_empty_card)));
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongCardNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText("5678000123456789");
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                sleepThread();
                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_card_luhn)));
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongExpiryDate() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText("12");
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });
        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongCardholderName() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText("");
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });
        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongIdentificationNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText("");

            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });
        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
    }

    public void testGetCardTokenWithSecurityCode() {

        // Set activity and set fields
        doSecurityCodeStart();
        setFields();
        mSecurityCodeText = (EditText) mActivity.findViewById(R.id.securityCode);

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                EditText fieldText;
                fieldText = (EditText) mActivity.findViewById(R.id.cardNumber);
                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.expiryMonth);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                fieldText = (EditText) mActivity.findViewById(R.id.expiryYear);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                fieldText = (EditText) mActivity.findViewById(R.id.cardholderName);
                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                fieldText = (EditText) mActivity.findViewById(R.id.identificationNumber);
                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.securityCode);
                fieldText.setText(StaticMock.DUMMY_SECURITY_CODE);
            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });
        // Validate the result
        try {
            ActivityResult activityResult = getActivityResult(mActivity);
            CardToken cardToken = (CardToken) activityResult.getExtras().getSerializable("cardToken");

            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
            assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
            assertTrue(cardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
        } catch (Exception ex) {
            fail("Get card token with security code test failed, cause: " + ex.getMessage());
        }
    }

    public void testEmptySecurityCode() {

        // Set activity and set fields
        doSecurityCodeStart();
        setFields();
        mSecurityCodeText = (EditText) mActivity.findViewById(R.id.securityCode);

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mSecurityCodeText.setText("");

            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });
        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
        assertTrue(mSecurityCodeText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_cvv_length, 3)));
    }

    public void testWhenBinEnteredAndThereIsOnlyOnePaymentMethodPossibleSetImageHidePMLayout()
    {
        doRegularStart();
        setFields();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {

                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));

            }
        });

        sleepThread();
        assertTrue(mPaymentMethodImage.getDrawable() != null);
        assertTrue(mPaymentMethodsLayout.getVisibility() == View.GONE);

    }

    public void testWhenBinEnteredAndThereAreMultiplePaymentMethodsPossibleShowPMLayoutHideImage(){
        doRegularStartForMultiplePaymentMethods();
        setFields();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));
            }
        });

        sleepThread();

        assertTrue(mPaymentMethodImage.getDrawable() == null);
        assertTrue(mPaymentMethodsLayout.getVisibility() == View.VISIBLE);
    }

    public void testIfIssuersRequiredAndPaymentMethodNeedsSelectionShowIssuerSelector()
    {
        doIssuersStart();
        setFields();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));
            }
        });

        assertTrue(mIssuersSpinner.getVisibility() == View.VISIBLE);
    }

    public void testIfMultiplePaymentMethodsAndNoOneSelectedWhenSubmitShowError(){
        doRegularStartForMultiplePaymentMethods();
        setFields();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));
            }
        });
        sleepThread();

        assertTrue(mPaymentMethodImage.getDrawable() == null);
        assertTrue(mPaymentMethodsLayout.getVisibility() == View.VISIBLE);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });

        PaymentMethodsSpinnerAdapter adapter = (PaymentMethodsSpinnerAdapter) mPaymentMethodsSpinner.getAdapter();
        assertTrue(adapter.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
    }

    public void testWithMultiplePaymentMethodsIfIssuerRequiredButNotSelectedShowError(){
        doIssuersStartForMultiplePaymentMethods();
        setFields();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                //Master needs issuer selection
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER_MASTER.substring(0, MercadoPago.BIN_LENGTH));


            }
        });
        assertTrue(mIssuersSpinner.getVisibility() == View.VISIBLE);
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mPaymentMethodsSpinner.setSelection(mPaymentMethodsSpinner.getCount() - 1);
            }
        });
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });

        IssuersSpinnerAdapter adapter = (IssuersSpinnerAdapter) mIssuersSpinner.getAdapter();
        assertTrue(!adapter.getError().equals(""));

    }

    public void testWithOnePaymentMethodIfIssuerRequiredButNotSelectedShowError(){
        doIssuersStart();
        setFields();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));
                //Master needs issuer selection
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER_MASTER.substring(0, MercadoPago.BIN_LENGTH));


            }
        });
        assertTrue(mIssuersSpinner.getVisibility() == View.VISIBLE);
        sleepThread();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.submitForm(null);
            }
        });

        IssuersSpinnerAdapter adapter = (IssuersSpinnerAdapter) mIssuersSpinner.getAdapter();
        assertTrue(!adapter.getError().equals(""));

    }

    public void testIfSecurityCodeRequiredShowSecurityCodeLayout()
    {
        doSecurityCodeStart();
        setFields();
        assertTrue(mSecurityCodeLayout.getVisibility() == View.VISIBLE);

    }

    public void testIfSecurityCodeNotHideSecurityCodeLayout()
    {
        doRegularStart();
        setFields();
        assertTrue(mSecurityCodeLayout.getVisibility() == View.GONE);

    }

    public void testIfSecurityCodeRequiredHideSecurityCodeHelpWhenNoPaymentMethodSet()
    {
        doSecurityCodeStart();
        setFields();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {

                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

            }
        });

        sleepThread();
        assertTrue(mCVVDescriptor.getText().equals(""));
        assertTrue(mCVVImage.getDrawable() == null);

    }

    public void testIfSecurityCodeRequiredShowSecurityCodeHelpWhenPaymentMethodSet()
    {
        doSecurityCodeStart();
        setFields();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {

                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER.substring(0, MercadoPago.BIN_LENGTH));

            }
        });

        sleepThread();
        assertTrue(mCVVDescriptor.getVisibility() == View.VISIBLE);
        assertTrue(mCVVImage.getVisibility() == View.VISIBLE);

    }

    private GuessingNewCardActivity prepareActivity(String keyType,
                                                 String key, Boolean requireSecurityCode, Boolean requireIssuer) {

        Intent intent = new Intent();

        if (keyType != null) {
            intent.putExtra("keyType", keyType);
        }
        if (key != null) {
            intent.putExtra("key", key);
        }
        if (requireSecurityCode != null) {
            intent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        if(requireIssuer != null)
        {
            intent.putExtra("requireIssuer", requireIssuer);
        }
        setActivityIntent(intent);
        return getActivity();
    }

    private void doRegularStart() {

        // Set activity
        mActivity = prepareActivity(MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, false, false);

        // Wait for identification types
        sleepThread();
    }
    private void doRegularStartForMultiplePaymentMethods() {

        // Set activity
        //MX PK brings multiple PM for Visa and Master
        mActivity = prepareActivity(MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MX_MERCHANT_PUBLIC_KEY, false, false);

        // Wait for identification types
        sleepThread();
    }


    private void doSecurityCodeStart() {
        // Set activity
        mActivity = prepareActivity(MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, true, false);

        // Wait for identification types
        sleepThread();
    }

    private void doIssuersStart(){
        mActivity = prepareActivity(MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, false, true);
        sleepThread();
    }

    private void doIssuersStartForMultiplePaymentMethods(){
        //MX PK brings multiple PM for Visa and Master
        mActivity = prepareActivity(MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MX_MERCHANT_PUBLIC_KEY, false, true);
        sleepThread();
    }


    private void setFields() {

        mPaymentMethodImage = (ImageView) mActivity.findViewById(R.id.pmImage);
        mPaymentMethodsSpinner = (Spinner) mActivity.findViewById(R.id.spinnerPaymentMethod);
        mCardNumberText = (EditText) mActivity.findViewById(R.id.cardNumber);
        mCardholderNameText = (EditText) mActivity.findViewById(R.id.cardholderName);
        mExpiryMonthText = (EditText) mActivity.findViewById(R.id.expiryMonth);
        mExpiryYearText = (EditText) mActivity.findViewById(R.id.expiryYear);
        mExpiryErrorText = (TextView) mActivity.findViewById(R.id.expiryError);
        mIdentificationNumberText = (EditText) mActivity.findViewById(R.id.identificationNumber);
        mPaymentMethodsLayout = (LinearLayout) mActivity.findViewById(R.id.paymentMethodSelectionLayout);
        mIssuersSpinner = (Spinner) mActivity.findViewById(R.id.spinnerIssuer);
        mSecurityCodeLayout = (RelativeLayout) mActivity.findViewById(R.id.securityCodeLayout);
        mCVVDescriptor = (TextView) mActivity.findViewById(R.id.cVVDescriptor);
        mCVVImage = (ImageView) mActivity.findViewById(R.id.cVVImage);

    }
}
