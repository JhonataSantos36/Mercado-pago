package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.CardBackFragment;
import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.fragments.CardIdentificationFragment;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GuessingNewCardActivity extends FrontCardActivity {


    // Activity parameters
    private String mPublicKey;
    private MPTextView mToolbarTitle;

    // Input controls
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardNumberEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mCardSecurityCodeEditText;
    private MPEditText mCardIdentificationNumberEditText;
    private MPTextView mBackButtonText;
    private MPTextView mErrorTextView;
    private LinearLayout mSecurityCodeEditView;
    private LinearLayout mInputContainer;
    private Spinner mIdentificationTypeSpinner;
    private LinearLayout mIdentificationTypeContainer;
    private LinearLayout mIdentificationNumberContainer;
    private HorizontalScrollView mHorizontalScrollView;
    private ProgressBar mProgressBar;
    private FrameLayout mBackButton;
    private FrameLayout mNextButton;
    private FrameLayout mErrorContainer;
    private LinearLayout mButtonContainer;

    //Card container
    private CardFrontFragment mFrontFragment;
    private CardBackFragment mBackFragment;
    private CardIdentificationFragment mCardIdentificationFragment;


    // Local vars
    private Activity mActivity;
    private MercadoPago mMercadoPago;
    private PaymentPreference mPaymentPreference;
    private CardToken mCardToken;
    private Token mToken;
    private Identification mIdentification;
    private Issuer mSelectedIssuer;
    private IdentificationType mSelectedIdentificationType;
    private boolean mIdentificationNumberRequired;
    private String mCardSideState;
    private String mCurrentEditingEditText;
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;
    private boolean mIsSecurityCodeRequired;
    private int mCardSecurityCodeLength;
    private int mCardNumberLength;
    private String mSecurityCodeLocation;
    private FailureRecovery mFailureRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeLayout(savedInstanceState);
        setInputControls();
        initializeToolbar();
        getActivityParameters();
        verifyValidCreate();
        setListeners();
        openKeyboard(mCardNumberEditText);
        mCurrentEditingEditText = CardInterface.CARD_NUMBER_INPUT;

        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mPublicKey)
                .build();

        getPaymentMethodsAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showingFront()) {
            mCardNumberEditText.requestFocus();
            mCurrentEditingEditText = CardInterface.CARD_NUMBER_INPUT;
        }
    }

    @Override
    protected void onDestroy() {
        if (showingFront() && mFrontFragment != null) {
            mFrontFragment.setCardColor(CardInterface.NEUTRAL_CARD_COLOR);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        checkFlipCardToFront();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        LayoutUtil.showProgressLayout(this);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_new_card_form);
    }

    private void initializeLayout(Bundle savedInstanceState) {
        mActivity = this;
        mErrorState = CardInterface.NORMAL_STATE;
        mCardToken = new CardToken("", null, null, "", "", "", "");
        mIsSecurityCodeRequired = true;
        mCardSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        mSecurityCodeLocation = null;
        if (mFrontFragment == null) {
            mFrontFragment = new CardFrontFragment();
        }
        if (mBackFragment == null) {
            mBackFragment = new CardBackFragment();
        }
        if (mCardIdentificationFragment == null) {
            mCardIdentificationFragment = new CardIdentificationFragment();
        }
        if (savedInstanceState == null) {
            mCardSideState = CARD_SIDE_FRONT;
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_new_card_container, mFrontFragment)
                    .commit();
        }
    }

    protected void setInputControls() {
        mCardNumberEditText = (MPEditText) findViewById(R.id.cardNumber);
        mCardHolderNameEditText = (MPEditText) findViewById(R.id.cardholderName);
        mCardExpiryDateEditText = (MPEditText) findViewById(R.id.cardExpiryDate);
        mCardSecurityCodeEditText = (MPEditText) findViewById(R.id.cardSecurityCode);
        mCardIdentificationNumberEditText = (MPEditText) findViewById(R.id.cardIdentificationNumber);
        mSecurityCodeEditView = (LinearLayout) findViewById(R.id.cardSecurityCodeContainer);
        mInputContainer = (LinearLayout) findViewById(R.id.newCardInputContainer);
        mIdentificationTypeSpinner = (Spinner) findViewById(R.id.cardIdentificationType);
        mIdentificationTypeContainer = (LinearLayout) findViewById(R.id.cardIdentificationTypeContainer);
        mIdentificationNumberContainer = (LinearLayout) findViewById(R.id.cardIdentificationNumberContainer);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBackButton = (FrameLayout) findViewById(R.id.backButton);
        mNextButton = (FrameLayout) findViewById(R.id.nextButton);
        mButtonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        mErrorContainer = (FrameLayout) findViewById(R.id.errorContainer);
        mBackButtonText = (MPTextView) findViewById(R.id.backButtonText);
        mErrorTextView = (MPTextView) findViewById(R.id.errorTextView);
        mProgressBar.setVisibility(View.GONE);
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mIdentificationNumberContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.VISIBLE);
    }

    protected void getActivityParameters() {
        mPublicKey = this.getIntent().getStringExtra("publicKey");
        mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        mToken = (Token) this.getIntent().getSerializableExtra("token");
        mIdentification = new Identification();
        mIdentificationNumberRequired = false;
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void verifyValidCreate() {
        if (mPublicKey == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setListeners() {
        setCardNumberFocusListener();
        setCardNameFocusListener();
        setCardExpiryDateFocusListener();
        setCardSecurityCodeFocusListener();
        setCardIdentificationFocusListener();
        setNavigationButtonsListeners();
        setSecurityCodeTextWatcher();
        setIdentificationNumberTextWatcher();
        setCardholderNameTextWatcher();
        setExpiryDateTextWatcher();
    }

    public void openKeyboard(MPEditText ediText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setNavigationButtonsListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCurrentEditText(true);
            }
        });
        mBackButtonText.setTextColor(ContextCompat.getColor(this, R.color.mpsdk_active_button));
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!mCurrentEditingEditText.equals(CARD_NUMBER_INPUT)) {
                checkIsEmptyOrValid();
            }
            }
        });
    }

    private boolean validateCurrentEditText(boolean onFinish) {
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                if (validateCardNumber(true)){
                    mCardHolderNameEditText.requestFocus();
                    return true;
                }
                return false;
            case CARDHOLDER_NAME_INPUT:
                if (validateCardName(true)) {
                    mCardExpiryDateEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (validateExpiryDate(true)) {
                    if (isSecurityCodeRequired()) {
                        mCardSecurityCodeEditText.requestFocus();
                    } else if (mIdentificationNumberRequired) {
                        mCardIdentificationNumberEditText.requestFocus();
                    }
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (validateSecurityCode(true)) {
                    if (mIdentificationNumberRequired) {
                        mCardIdentificationNumberEditText.requestFocus();
                    } else {
                        mCurrentEditingEditText = CARD_INPUT_FINISH;
                      if (onFinish) {
                          submitForm();
                      }
                    }
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (validateIdentificationNumber(true)) {
                    mCurrentEditingEditText = CARD_INPUT_FINISH;
                    if (onFinish) {
                        submitForm();
                    }
                    return true;
                }
                return false;
            case CARD_INPUT_FINISH:
                if (onFinish) {
                    submitForm();
                }
                return true;
        }
        return false;
    }

    private boolean checkIsEmptyOrValid() {
        switch (mCurrentEditingEditText) {
            case CARDHOLDER_NAME_INPUT:
                if (TextUtils.isEmpty(mCardHolderName) || validateCardName(true)) {
                    mCardNumberEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (mExpiryMonth == null || validateExpiryDate(true)) {
                    mCardHolderNameEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (TextUtils.isEmpty(mSecurityCode) || validateSecurityCode(true)) {
                    mCardExpiryDateEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (TextUtils.isEmpty(mCardIdentificationNumber) || validateIdentificationNumber(true)) {
                   if (isSecurityCodeRequired()) {
                       mCardSecurityCodeEditText.requestFocus();
                   } else {
                       mCardExpiryDateEditText.requestFocus();
                   }
                    return true;
                }
                return false;
            case CARD_INPUT_FINISH:
                if (mIdentificationNumberRequired) {
                    mCardSecurityCodeEditText.requestFocus();
                } else if (isSecurityCodeRequired()) {
                    mCardExpiryDateEditText.requestFocus();
                } else {
                    mCardHolderNameEditText.requestFocus();
                }
                return true;
        }
        return false;
    }

    private void submitForm() {
        if (canCreateCardToken()) {
            createCardToken();
        }
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {
                initializeGuessingCardNumberController(paymentMethods);
                setCardNumberListener();
                if (mToken != null) {
                    initializeCardByToken();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mFailureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodsAsync();
                    }
                };
                ApiUtil.showApiExceptionError(mActivity, error);
            }
        });
    }

    protected void initializeCardByToken() {
        if (mToken.getFirstSixDigits() != null) {
            mCardNumberEditText.setText(mToken.getFirstSixDigits());
        }
        if (mToken.getCardholder() != null && mToken.getCardholder().getName() != null) {
            mCardHolderNameEditText.setText(mToken.getCardholder().getName());
        }
        if (mToken.getExpirationMonth() != null && mToken.getExpirationYear() != null) {
            mCardExpiryDateEditText.append(mToken.getExpirationMonth().toString());
            mCardExpiryDateEditText.append(mToken.getExpirationYear().toString().substring(2,4));
        }
        if (mToken.getCardholder() != null && mToken.getCardholder().getIdentification() != null) {
            String number = mToken.getCardholder().getIdentification().getNumber();
            if (number != null) {
                saveCardIdentificationNumber(number);
                mCardIdentificationNumberEditText.setText(number);
            }
        }
        mCardNumberEditText.requestFocus();
    }

    protected void initializeGuessingCardNumberController(List<PaymentMethod> paymentMethods) {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(paymentMethods);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }

    public void setCardNumberListener() {
        mCardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardNumberEditText.toggleLineColorOnError(false);
            }
        });
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
                mPaymentMethodGuessingController,
                new PaymentMethodSelectionCallback() {
                    @Override
                    public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
                        if (paymentMethodList.size() == 0) {
                            blockCardNumbersInput(mCardNumberEditText);
                            setErrorView(getString(R.string.mpsdk_invalid_payment_method));
                        } else if (paymentMethodList.size() == 1) {
                            onPaymentMethodSet(paymentMethodList.get(0));
                        } else {
                            //TODO muchos metodos de pago posibles, o no setearon el payment type
                        }
                    }

                    @Override
                    public void onPaymentMethodSet(PaymentMethod paymentMethod) {
                        if (mCurrentPaymentMethod == null) {
                            changeCardColor(getCardColor(paymentMethod), getCardFontColor(paymentMethod));
                            changeCardImage(getCardImage(paymentMethod));
                            mCurrentPaymentMethod = paymentMethod;
                            manageSettings();
                            manageAdditionalInfoNeeded();
                        }
                    }

                    @Override
                    public void onPaymentMethodCleared() {
                        clearErrorView();
                        if (mCurrentPaymentMethod == null) return;
                        mCurrentPaymentMethod = null;
                        setSecurityCodeLocation(null);
                        changeCardColor(CardInterface.NEUTRAL_CARD_COLOR, CardInterface.FULL_TEXT_VIEW_COLOR);
                        clearCardImage();
                        clearSecurityCodeFront();
                    }

                }));
    }

    protected boolean showingIdentification() {
        return mCardSideState.equals(CARD_IDENTIFICATION);
    }

    protected boolean showingBack() {
        return mCardSideState.equals(CARD_SIDE_BACK);
    }

    protected boolean showingFront() {
        return mCardSideState.equals(CARD_SIDE_FRONT);
    }

    public void checkFlipCardToFront() {
        if (showingBack() || showingIdentification()) {
            getSupportFragmentManager().popBackStack();
            mCardSideState = CARD_SIDE_FRONT;
        }
    }

    public void checkFlipCardToBack() {
        if (showingFront()) {
            startBackFragment();
        } else if (showingIdentification()) {
            getSupportFragmentManager().popBackStack();
            startBackFragment();
        }
    }

    public void checkTransitionCardToId() {
        if (!mIdentificationNumberRequired) {
            return;
        }
        if (showingFront()) {
            startIdentificationFragment();
        } else if (showingBack()) {
            getSupportFragmentManager().popBackStack();
            startIdentificationFragment();
        }
    }

    private void startIdentificationFragment() {
        mCardSideState = CARD_IDENTIFICATION;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_to_left_in_slower, R.anim.slide_right_to_left_out_slower,
                        R.anim.slide_left_to_right_in_slower, R.anim.slide_left_to_right_out_slower)
                .replace(R.id.activity_new_card_container, mCardIdentificationFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startBackFragment() {
        mCardSideState = CARD_SIDE_BACK;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.from_middle_left, R.anim.to_middle_left,
                        R.anim.from_middle_left, R.anim.to_middle_left)
                .replace(R.id.activity_new_card_container, mBackFragment)
                .addToBackStack(null)
                .commit();
    }

    public void manageSettings() {
        String bin = mPaymentMethodGuessingController.getSavedBin();
        List<Setting> settings = mCurrentPaymentMethod.getSettings();
        Setting setting = Setting.getSettingByBin(settings, bin);
        setCardNumberLength(setting.getCardNumber().getLength());

        if (mCurrentPaymentMethod.isSecurityCodeRequired(bin)) {
            SecurityCode securityCode = setting.getSecurityCode();
            setSecurityCodeRestrictions(true, securityCode);
            setSecurityCodeViewRestrictions(securityCode);
            showSecurityCodeView();
        } else {
            mSecurityCode = null;
            setSecurityCodeRestrictions(false, null);
            hideSecurityCodeView();
        }
    }

    public void manageAdditionalInfoNeeded() {
        mIdentificationNumberRequired = mCurrentPaymentMethod.isIdentificationNumberRequired();
        if (mIdentificationNumberRequired) {
            mIdentificationNumberContainer.setVisibility(View.VISIBLE);
            mMercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
                @Override
                public void success(List<IdentificationType> identificationTypes, Response response) {
                    if (!identificationTypes.isEmpty()) {
                        mSelectedIdentificationType = identificationTypes.get(0);
                        mIdentificationTypeSpinner.setAdapter(new IdentificationTypesAdapter(mActivity, identificationTypes));
                        mIdentificationTypeContainer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    mFailureRecovery = new FailureRecovery() {
                        @Override
                        public void recover() {
                            manageAdditionalInfoNeeded();
                        }
                    };
                    ApiUtil.showApiExceptionError(mActivity, error);
                }
            });
        }
    }

    private void showSecurityCodeView() {
        mSecurityCodeEditView.setVisibility(View.VISIBLE);
    }

    private void hideSecurityCodeView() {
        clearSecurityCodeFront();
        mSecurityCodeEditView.setVisibility(View.GONE);
    }

    public void blockCardNumbersInput(MPEditText text) {
        int maxLength = MercadoPago.BIN_LENGTH;
        setInputMaxLength(text, maxLength);
    }

    public void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }


    private void setCardNumberLength(int maxLength) {
        mCardNumberLength = maxLength;
        setInputMaxLength(mCardNumberEditText, maxLength);
    }

    private void setSecurityCodeViewRestrictions(SecurityCode securityCode) {
        //Location
        if (securityCode.getCardLocation().equals(CardInterface.CARD_SIDE_BACK)) {
            clearSecurityCodeFront();
            mSecurityCode = mCardSecurityCodeEditText.getText().toString();
        } else if (securityCode.getCardLocation().equals(CardInterface.CARD_SIDE_FRONT)) {
            mCardSecurityCodeEditText.setOnClickListener(null);
            mFrontFragment.setCardSecurityView();
        }

        //Length
        setEditTextMaxLength(mCardSecurityCodeEditText, securityCode.getLength());

    }

    private void setEditTextMaxLength(MPEditText editText, int maxLength) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(filters);
    }

    private void setCardSecurityCodeErrorView(String message, boolean requestFocus) {
        setErrorView(message);
        if (mSecurityCodeLocation != null) {
            if (mSecurityCodeLocation.equals(CardInterface.CARD_SIDE_BACK)) {
                if (showingBack() && mBackFragment != null) {
                    checkFlipCardToBack();
                }
            } else if (mSecurityCodeLocation.equals(CardInterface.CARD_SIDE_FRONT)) {
                if (showingFront() && mFrontFragment != null) {
                    checkFlipCardToFront();
                }
            }
        }
        if (requestFocus) {
            mCardSecurityCodeEditText.toggleLineColorOnError(true);
            mCardSecurityCodeEditText.requestFocus();
        }
    }

    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mSecurityCodeLocation == null) {
            return CardInterface.CARD_SIDE_BACK;
        }
        return mSecurityCodeLocation;
    }

    private void setCardIdentificationErrorView(String message, boolean requestFocus) {
        setErrorView(message);
        if (requestFocus) {
            mCardIdentificationNumberEditText.toggleLineColorOnError(true);
            mCardIdentificationNumberEditText.requestFocus();
            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
    }

    private void clearSecurityCodeFront() {
        mFrontFragment.hideCardSecurityView();
        setCardSecurityCodeFocusListener();
    }

    public void changeCardColor(int color, int font) {
        if (!showingBack() && mFrontFragment != null) {
            mFrontFragment.transitionColor(color, font);
        }
    }

    public void changeCardImage(int image) {
        mFrontFragment.transitionImage(image);
    }

    public void clearCardImage() {
        mFrontFragment.clearImage();
    }

    private void setCardNumberFocusListener() {
        mCardNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateCurrentEditText(true);
                    return true;
                }
                return false;
            }
        });
        mCardNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardNumberEditText);
                }
                return true;
            }
        });
        mCardNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openKeyboard(mCardNumberEditText);
                    checkFlipCardToFront();
                    mCurrentEditingEditText = CARD_NUMBER_INPUT;
                    mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                }
            }
        });
    }

    private void setCardNameFocusListener() {
        mCardHolderNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateCurrentEditText(true);
                    return true;
                }
                return false;
            }
        });
        mCardHolderNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardHolderNameEditText);
                }
                return true;
            }
        });
        mCardHolderNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openKeyboard(mCardHolderNameEditText);
                    checkFlipCardToFront();
                    mCurrentEditingEditText = CARDHOLDER_NAME_INPUT;
                }
            }
        });
    }

    private void setCardExpiryDateFocusListener() {
        mCardExpiryDateEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateCurrentEditText(true);
                    return true;
                }
                return false;
            }
        });
        mCardExpiryDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardExpiryDateEditText);
                }
                return true;
            }
        });
        mCardExpiryDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openKeyboard(mCardExpiryDateEditText);
                    checkFlipCardToFront();
                    mCurrentEditingEditText = CARD_EXPIRYDATE_INPUT;
                }
            }
        });
    }

    private void setCardSecurityCodeFocusListener() {
        mCardSecurityCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateCurrentEditText(true);
                    return true;
                }
                return false;
            }
        });
        mCardSecurityCodeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardSecurityCodeEditText);
                }
                return true;
            }
        });
        mCardSecurityCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openKeyboard(mCardSecurityCodeEditText);
                    mCurrentEditingEditText = CARD_SECURITYCODE_INPUT;
                    if (mSecurityCodeLocation == null || mSecurityCodeLocation.equals(CardInterface.CARD_SIDE_BACK)) {
                        checkFlipCardToBack();
                    } else {
                        checkFlipCardToFront();
                    }
                }
            }
        });
        mCardSecurityCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (canCreateCardToken()) {
                        createCardToken();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void setCardIdentificationFocusListener() {
        mIdentificationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedIdentificationType = (IdentificationType) mIdentificationTypeSpinner.getSelectedItem();
                if (mSelectedIdentificationType != null) {
                    mIdentification.setType(mSelectedIdentificationType.getId());
                    setEditTextMaxLength(mCardIdentificationNumberEditText, mSelectedIdentificationType.getMaxLength());
                    if (mSelectedIdentificationType.getType().equals("number")) {
                        mCardIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        mCardIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mIdentificationTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCurrentEditingEditText.equals(CARD_SECURITYCODE_INPUT)) {
                    return false;
                }
                checkTransitionCardToId();
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                mCardIdentificationNumberEditText.requestFocus();
                return false;
            }
        });
        mCardIdentificationNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateCurrentEditText(true);
                    return true;
                }
                return false;
            }
        });
        mCardIdentificationNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardIdentificationNumberEditText);
                }
                return true;
            }
        });
        mCardIdentificationNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openKeyboard(mCardIdentificationNumberEditText);
                    checkTransitionCardToId();
                    mCurrentEditingEditText = CARD_IDENTIFICATION_INPUT;
                }
            }
        });
    }

    public void setSecurityCodeTextWatcher() {
        mCardSecurityCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (showingBack() && mBackFragment != null) {
                    mBackFragment.onSecurityTextChanged(s, start, before, count);
                } else if (mFrontFragment != null) {
                    mFrontFragment.onSecurityTextChanged(s, start, before, count);
                }

                if (s.length() == mCardSecurityCodeLength) {
                    mSecurityCode = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardSecurityCodeEditText.toggleLineColorOnError(false);
                if (showingBack() && mBackFragment != null) {
                    mBackFragment.afterSecurityTextChanged(s);
                } else if (mFrontFragment != null) {
                    mFrontFragment.afterSecurityTextChanged(s);
                }
            }
        });
    }

    public void setIdentificationNumberTextWatcher() {
        mCardIdentificationNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (showingIdentification() && mCardIdentificationFragment != null) {
                    mCardIdentificationFragment.onNumberTextChanged(s, start, before, count);
                }
                if (mSelectedIdentificationType != null && mSelectedIdentificationType.getMaxLength() != null) {
                    if (s.length() == mSelectedIdentificationType.getMaxLength()) {
                        mIdentification.setNumber(s.toString());
                        validateIdentificationNumber(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardIdentificationNumberEditText.toggleLineColorOnError(false);
                if (showingIdentification() && mCardIdentificationFragment != null) {
                    mCardIdentificationFragment.afterNumberTextChanged(s);
                }
            }
        });
    }

    public void setCardholderNameTextWatcher() {
        mCardHolderNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardHolderNameEditText.toggleLineColorOnError(false);
            }
        });
    }

    public void setExpiryDateTextWatcher() {
        mCardExpiryDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardExpiryDateEditText.toggleLineColorOnError(false);
            }
        });
    }


    public boolean canCreateCardToken() {
        boolean result = true;
        boolean requestFocus = true;
        if (!validateCardNumber(requestFocus)) {
            result = false;
            requestFocus = false;
        }
        if (!validateCardName(requestFocus)) {
            result = false;
            requestFocus = false;
        }
        if (!validateExpiryDate(requestFocus)) {
            result = false;
            requestFocus = false;
        }
        if (isSecurityCodeRequired() &&
                !validateSecurityCode(requestFocus)) {
            result = false;
            requestFocus= false;
        }
        if (mIdentificationNumberRequired) {
            if (!validateIdentificationNumber(requestFocus)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return mIsSecurityCodeRequired;
    }

    public void setSecurityCodeRequired(boolean required) {
        this.mIsSecurityCodeRequired = required;
    }

    public void setSecurityCodeLength(int length) {
        this.mCardSecurityCodeLength = length;
    }

    public void setSecurityCodeLocation(String location) {
        this.mSecurityCodeLocation = location;
    }

    public void setSecurityCodeRestrictions(boolean isRequired, SecurityCode securityCode) {
        setSecurityCodeRequired(isRequired);
        if (securityCode == null) {
            setSecurityCodeLocation(null);
            setSecurityCodeLength(CARD_DEFAULT_SECURITY_CODE_LENGTH);
            return;
        }
        setSecurityCodeLocation(securityCode.getCardLocation());
        setSecurityCodeLength(securityCode.getLength());
    }

    private void createCardToken() {
        Integer month = (mExpiryMonth == null ? null : Integer.valueOf(mExpiryMonth));
        Integer year = (mExpiryYear == null ? null : Integer.valueOf(mExpiryYear));
        String identificationTypeId = (mSelectedIdentificationType == null ? null :
            mSelectedIdentificationType.getId());
        String identificationNumber = (mIdentification == null ? null :
            mIdentification.getNumber());

        CardToken cardToken = new CardToken(getCardNumber(), month, year, getSecurityCode(),
                getCardHolderName(), identificationTypeId, identificationNumber);
        if (isSecurityCodeRequired()) {
            try {
                cardToken.validateSecurityCode(this, mCurrentPaymentMethod);
                clearErrorView();
                createToken();
            } catch (Exception e) {
                setCardSecurityCodeErrorView(e.getMessage(), true);
            }
        }
    }

    private void createToken() {
        checkFlipCardToFront();
        LayoutUtil.hideKeyboard(this);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mBackButton.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        mMercadoPago.createToken(mCardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                //TODO: solo en credit card??
                checkStartIssuersActivity();
            }

            @Override
            public void failure(RetrofitError error) {
                mFailureRecovery = new FailureRecovery() {
                    @Override
                    public void recover() {
                        createToken();
                    }
                };
                ApiUtil.showApiExceptionError(mActivity, error);
            }
        });
    }


    private boolean validateCardNumber(boolean requestFocus) {
        mCardToken.setCardNumber(getCardNumber());
        try {
            if (mCurrentPaymentMethod == null) {
                if (getCardNumber() == null || getCardNumber().length() < MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_card_number_incomplete));
                } else if (getCardNumber().length() == MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_payment_method));
                } else {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_payment_method));
                }
            }
            mCardToken.validateCardNumber(this, mCurrentPaymentMethod);
            clearErrorView();
            return true;
        } catch (Exception e) {
            setErrorView(e.getMessage());
            if (requestFocus) {
                mCardNumberEditText.toggleLineColorOnError(true);
                mCardNumberEditText.requestFocus();
            }
            return false;
        }
    }

    private boolean validateCardName(boolean requestFocus) {
        Cardholder cardHolder = new Cardholder();
        cardHolder.setName(mCardHolderName);
        cardHolder.setIdentification(mIdentification);
        mCardToken.setCardholder(cardHolder);
        if (mCardToken.validateCardholderName()) {
            clearErrorView();
            return true;
        } else {
            setErrorView(getString(R.string.mpsdk_invalid_empty_name));
            if (requestFocus) {
                mCardHolderNameEditText.toggleLineColorOnError(true);
                mCardHolderNameEditText.requestFocus();
            }
            return false;
        }
    }

    private boolean validateExpiryDate(boolean requestFocus) {
        Integer month = (mExpiryMonth == null ? null : Integer.valueOf(mExpiryMonth));
        Integer year = (mExpiryYear == null ? null : Integer.valueOf(mExpiryYear));
        mCardToken.setExpirationMonth(month);
        mCardToken.setExpirationYear(year);
        if (mCardToken.validateExpiryDate()) {
            clearErrorView();
            return true;
        } else {
            setErrorView(getString(R.string.mpsdk_invalid_expiry_date));
            if (requestFocus) {
                mCardExpiryDateEditText.toggleLineColorOnError(true);
                mCardExpiryDateEditText.requestFocus();
            }
            return false;
        }
    }

    public boolean validateSecurityCode(boolean requestFocus) {
        mCardToken.setSecurityCode(mSecurityCode);
        try {
            mCardToken.validateSecurityCode(this, mCurrentPaymentMethod);
            clearErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage(), requestFocus);
            return false;
        }
    }

    public boolean validateIdentificationNumber(boolean requestFocus) {
        mIdentification.setNumber(getCardIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentificationNumber(mSelectedIdentificationType);
        if (ans) {
            clearErrorView();
        } else {
            setCardIdentificationErrorView(getString(R.string.mpsdk_invalid_identification_number),
                    requestFocus);
        }
        return ans;
    }

    public void checkChangeErrorView() {
        if (mErrorState.equals(ERROR_STATE)) {
            clearErrorView();
        }
    }

    public void setErrorView(String message) {
        mButtonContainer.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTextView.setText(message);
        mErrorState = CardInterface.ERROR_STATE;
    }

    public void clearErrorView() {
        mButtonContainer.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText("");
        mErrorState = CardInterface.NORMAL_STATE;
    }

    public void checkStartIssuersActivity() {
        mMercadoPago.getIssuers(mCurrentPaymentMethod.getId(), mPaymentMethodGuessingController.getSavedBin(),
                new Callback<List<Issuer>>() {
                    @Override
                    public void success(List<Issuer> issuers, Response response) {
                        if (issuers.isEmpty()) {
                            //error
                        } else if (issuers.size() == 1) {
                            mSelectedIssuer = issuers.get(0);
                            checkFlipCardToFront();
                            finishWithResult();
                        } else {
                            fadeInIssuersActivity(issuers);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mFailureRecovery = new FailureRecovery() {
                            @Override
                            public void recover() {
                                checkStartIssuersActivity();
                            }
                        };
                        ApiUtil.showApiExceptionError(mActivity, error);
                    }
                });
    }

    public void fadeInIssuersActivity(final List<Issuer> issuers) {
        checkFlipCardToFront();
        startIssuersActivity(issuers);
    }

    public void startIssuersActivity(final List<Issuer> issuers) {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
                        .setPublicKey(mPublicKey)
                        .setPaymentMethod(mCurrentPaymentMethod)
                        .setToken(mToken)
                        .setIssuers(issuers)
                        .startCardIssuersActivity();
                overridePendingTransition(R.anim.fade_in_seamless, R.anim.fade_out_seamless);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                mSelectedIssuer = (Issuer) bundle.getSerializable("issuer");
                checkFlipCardToFront();
                finishWithResult();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
        else if(requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                recoverFromFailure();
            }
            else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    private void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", mCurrentPaymentMethod);
        returnIntent.putExtra("token", mToken);
        returnIntent.putExtra("issuer", mSelectedIssuer);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private static class CardNumberTextWatcher implements TextWatcher {

        private PaymentMethodGuessingController mController;
        private PaymentMethodSelectionCallback mCallback;
        private String mBin;

        public CardNumberTextWatcher(PaymentMethodGuessingController controller,
                                     PaymentMethodSelectionCallback callback) {
            this.mController = controller;
            this.mCallback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mController == null) return;
            if (s.length() == MercadoPago.BIN_LENGTH - 1) {
                mCallback.onPaymentMethodCleared();
            } else if (s.length() >= MercadoPago.BIN_LENGTH) {
                mBin = s.subSequence(0, MercadoPago.BIN_LENGTH).toString();
                List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
                mCallback.onPaymentMethodListSet(list);
            }
        }
    }

    private void recoverFromFailure() {
        if(mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
