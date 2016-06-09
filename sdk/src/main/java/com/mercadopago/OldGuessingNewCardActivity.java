package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IssuersSpinnerAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.util.List;

public class OldGuessingNewCardActivity extends NewCardActivity {

    //Activity parameters
    protected Boolean mRequireIssuer;
    protected Boolean mShowBankDeals;
    private List<String> mExcludedPaymentMethodIds;
    private List<String> mExcludedPaymentTypes;
    private String mDefaultPaymentMethodId;
    private String mSupportedPaymentTypeId;

    //Input controls
    protected Spinner mSpinnerIssuers;
    protected LinearLayout mIssuerLayout;
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;


    //Local vars
    protected Issuer mIssuer;
    protected MercadoPago mMercadoPago;
    private PaymentPreference mPaymentPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutUtil.showProgressLayout(this);

        getActivityParameters();

        createPaymentMethodPreference();

        setInputControls();

        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setKey(mKey, mKeyType)
                .build();

        getPaymentMethodsAsync();
        setFocusOrder();
        setLayouts();
    }

    @Override
    protected void getActivityParameters(){
        super.getActivityParameters();

        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mExcludedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        mDefaultPaymentMethodId = this.getIntent().getStringExtra("defaultPaymentMethodId");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        mRequireIssuer = this.getIntent().getBooleanExtra("requireIssuer", true);
        mSupportedPaymentTypeId = this.getIntent().getStringExtra("paymentTypeId");
    }

    @Override
    protected void setInputControls()
    {
        super.setInputControls();
        mIssuerLayout = (LinearLayout) findViewById(R.id.mpsdkIssuerSelectionLayout);
        mSpinnerIssuers = (Spinner) findViewById(R.id.mpsdkSpinnerIssuer);
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                initializeGuessingCardNumberController(paymentMethods);
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {
                apiException.getMessage();
            }
        });
    }

    protected void initializeGuessingCardNumberController(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference.getSupportedPaymentMethods(paymentMethods);

//        mPaymentMethodGuessingController = new PaymentMethodGuessingController(this, supportedPaymentMethods, mSupportedPaymentTypeId,
//                new PaymentMethodSelectionCallback(){
//                    @Override
//                    public void onPaymentMethodSet(PaymentMethod paymentMethod) {
//                        mPaymentMethod = paymentMethod;
//                        setLayouts();
//                    }
//
//                    @Override
//                    public void onPaymentMethodCleared() {
//                        mPaymentMethod = null;
//                        setLayouts();
//                    }
//
//                    @Override
//                    public void onMaxCardNumberReached() {
//
//                    }
//
//                    @Override
//                    public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
//
//                    }
//                });
    }

    private void createPaymentMethodPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentMethodIds(this.mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(this.mExcludedPaymentTypes);
        mPaymentPreference.setDefaultPaymentMethodId(this.mDefaultPaymentMethodId);

    }

    @Override
    protected void verifyValidCreate(){
        if ((mKeyType == null) || (mKey == null)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public void setPaymentMethodImage(){
        ImageView pmImage = (ImageView) findViewById(R.id.mpsdkPmImage);
        if (pmImage != null) {
            pmImage.setImageDrawable(null);
        }
    }

    @Override
    public void setContentView()
    {
        setContentView(R.layout.activity_guessing_new_card);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.payment_methods, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mpsdkActionBankDeals) {
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this)
                    .setPublicKey(mKey)
                    .startBankDealsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean validateForm(CardToken cardToken)
    {
        boolean result = true;
        boolean requestFocus = true;


        if(!validateCardNumber(cardToken, requestFocus)) {
            result = false;
            requestFocus = false;
        }

        if(!validatePaymentMethod()){
            result = false;
        }

        if(!validateIssuer()) {
            result = false;
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

        if(!validateIdentificationNumber(cardToken, requestFocus)) {
            result = false;
        }

        return result;
    }

    protected boolean validateIssuer() {
        if(mPaymentMethod != null && mRequireIssuer && mPaymentMethod.isIssuerRequired() && mIssuer == null)
        {
            IssuersSpinnerAdapter adapter = (IssuersSpinnerAdapter) mSpinnerIssuers.getAdapter();
            View view = mSpinnerIssuers.getSelectedView();
            adapter.setError(view, getString(com.mercadopago.R.string.mpsdk_invalid_field));
            mSpinnerIssuers.requestFocus();
            return false;
        }
        return true;
    }

    protected boolean validatePaymentMethod() {
//        if(mPaymentMethodGuessingController.getCardNumberText().length() >= MercadoPago.BIN_LENGTH && mPaymentMethod == null)
//        {
//            mPaymentMethodGuessingController.setPaymentMethodError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
//            return false;
//        }
        return true;
    }

    @Override
    public boolean validateCardNumber(CardToken cardToken, boolean requestFocus)
    {
        boolean valid = true;
//        if(mPaymentMethodGuessingController.getCardNumberText().equals(""))
//        {
//            mPaymentMethodGuessingController.setCardNumberError(getString(R.string.mpsdk_invalid_empty_card));
//
//            if(requestFocus)
//                mPaymentMethodGuessingController.requestFocusForCardNumber();
//            valid = false;
//        }
//        else {
//            try {
//                validateCardNumber(cardToken);
//            } catch (Exception ex) {
//                mPaymentMethodGuessingController.setCardNumberError(ex.getMessage());
//                if (requestFocus)
//                    mPaymentMethodGuessingController.requestFocusForCardNumber();
//                valid = false;
//            }
//        }
        return valid;
    }

    @Override
    public void setSecurityCodeLayout()
    {
        if (!mRequireSecurityCode)
            mSecurityCodeLayout.setVisibility(View.GONE);
        else if(mPaymentMethod == null)
            setSecurityCodeHelpForPaymentMethod(null);
        else {
            setSecurityCodeHelpForPaymentMethod(mPaymentMethod);
            // Set layout visibility
            mSecurityCodeLayout.setVisibility(View.VISIBLE);
            setFormGoButton(mSecurityCode);
        }
    }

    protected void setLayouts() {
        setSecurityCodeLayout();
        setIssuerLayout();
    }

    protected void setSecurityCodeHelpForPaymentMethod(PaymentMethod paymentMethod) {
        if(paymentMethod != null) {
            mCVVDescriptor.setText(MercadoPagoUtil.getCVVDescriptor(this, this.mPaymentMethod));
            mCVVImage.setImageDrawable(ContextCompat.getDrawable(this, MercadoPagoUtil.getCVVImageResource(this, this.mPaymentMethod)));
        }
        else {
            mCVVDescriptor.setText("");
            mCVVImage.setImageDrawable(null);
        }
    }

    protected void setIssuerLayout() {
        if (!mRequireIssuer || mPaymentMethod == null || !mPaymentMethod.isIssuerRequired())
            mIssuerLayout.setVisibility(View.GONE);
        else {
            getIssuersAsync();
            mIssuerLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void getIssuersAsync() {
        if (mPaymentMethod != null) {
            mMercadoPago.getIssuers(mPaymentMethod.getId(), "", new Callback<List<Issuer>>() {
                @Override
                public void success(List<Issuer> issuers) {
                    populateIssuerSpinner(issuers);
                }

                @Override
                public void failure(ApiException error) {

                }
            });
        }
    }

    protected void populateIssuerSpinner(final List<Issuer> issuers) {

        mSpinnerIssuers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mIssuer = (Issuer) mSpinnerIssuers.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mIssuer = null;
            }
        });

        mSpinnerIssuers.setAdapter(new IssuersSpinnerAdapter(mActivity, issuers));
    }

    @Override
    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Set cards token
        CardToken cardToken = new CardToken(getCardNumber(), getMonth(), getYear(), getSecurityCode(), getCardHolderName(),
                getIdentificationTypeId(getIdentificationType()), getIdentificationNumber());

        if (validateForm(cardToken)) {
            // Return to parent
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cardToken", cardToken);
            returnIntent.putExtra("paymentMethod", mPaymentMethod);
            if(mRequireIssuer)
                returnIntent.putExtra("issuer", mIssuer);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

}