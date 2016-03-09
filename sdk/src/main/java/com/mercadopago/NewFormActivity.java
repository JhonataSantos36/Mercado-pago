package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewFormActivity extends FrontCardActivity {


    // Activity parameters
    private String mKey;
    private Boolean mRequireSecurityCode;
    private Boolean mRequireIssuer;
    private Boolean mShowBankDeals;
    private MPTextView mToolbarTitle;
    private Boolean hasToFlipCard;

    // Input controls
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardNumberEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mCardSecurityCodeEditText;
    private MPEditText mCardIdentificationNumberEditText;
//    private FrameLayout mSubmitButtonContainer;
    private LinearLayout mSecurityCodeEditView;
    private FrameLayout mSecurityCodeErrorContainer;
    private LinearLayout mInputContainer;
    private Spinner mIdentificationTypeSpinner;
    private LinearLayout mIdentificationTypeContainer;
    private LinearLayout mIdentificationNumberContainer;
    private HorizontalScrollView mHorizontalScrollView;
    private ProgressBar mProgressBar;
    private FrameLayout mBackButton;
    private FrameLayout mNextButton;
    private MPTextView mBackButtonText;

    // Input error Views
    private MPTextView mCardNumberError;
    private MPTextView mCardholderNameError;
    private MPTextView mCardExpiryDateError;
    private MPTextView mCardSecurityCodeError;
    private MPTextView mCardIdentificationTypeError;
    private MPTextView mCardIdentificationNumberError;

    //Card container
    private int mCurrentColor;
    private int mCurrentImage;
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
    private boolean mIdentificationTypeRequired;
    private String mCardSideState;
    private String mCurrentEditingEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeLayout(savedInstanceState);
        setInputControls();
        setCardInputViews();
        initializeToolbar();
        getActivityParameters();
        verifyValidCreate();
        setListeners();
        setSecurityCodeTextWatcher();
        setIdentificationNumberTextWatcher();
        openKeyboard(mCardNumberEditText);
        mCurrentEditingEditText = CardInterface.CARD_NUMBER_INPUT;

        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mKey)
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
        mToolbarTitle = (MPTextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarTitle.setText(getString(R.string.mpsdk_card_data_title));
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
        mCurrentColor = ContextCompat.getColor(this, CardInterface.NEUTRAL_CARD_COLOR);
        mCardNumberState = CardInterface.NORMAL_STATE;
        mCardNameState = CardInterface.NORMAL_STATE;
        mExpiryDateState = CardInterface.NORMAL_STATE;
        mSecurityCodeState = CardInterface.NORMAL_STATE;
        mCardIdentificationNumberState = CardInterface.NORMAL_STATE;
        mCardToken = new CardToken("", null, null, "", "", "", "");
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
        mSecurityCodeErrorContainer = (FrameLayout) findViewById(R.id.cardSecurityErrorContainer);
        mInputContainer = (LinearLayout) findViewById(R.id.newCardInputContainer);
        mIdentificationTypeSpinner = (Spinner) findViewById(R.id.cardIdentificationType);
        mIdentificationTypeContainer = (LinearLayout) findViewById(R.id.cardIdentificationTypeContainer);
        mIdentificationNumberContainer = (LinearLayout) findViewById(R.id.cardIdentificationNumberContainer);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollViewContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBackButton = (FrameLayout) findViewById(R.id.backButton);
        mNextButton = (FrameLayout) findViewById(R.id.nextButton);
        mBackButtonText = (MPTextView) findViewById(R.id.backButtonText);
        mProgressBar.setVisibility(View.GONE);
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mIdentificationNumberContainer.setVisibility(View.GONE);
    }

    protected void setCardInputViews() {
        mCardNumberError = (MPTextView) findViewById(R.id.cardNumberError);
        mCardholderNameError = (MPTextView) findViewById(R.id.cardholderNameError);
        mCardExpiryDateError = (MPTextView) findViewById(R.id.cardExpiryDateError);
        mCardSecurityCodeError = (MPTextView) findViewById(R.id.cardSecurityCodeError);
        mCardIdentificationTypeError = (MPTextView) findViewById(R.id.cardIdentificationTypeError);
        mCardIdentificationNumberError = (MPTextView) findViewById(R.id.cardIdentificationNumberCodeError);
    }

    protected void getActivityParameters() {
        mKey = this.getIntent().getStringExtra("publicKey");
        mRequireSecurityCode = this.getIntent().getBooleanExtra("requireSecurityCode", true);
        mPaymentPreference = (PaymentPreference) this.getIntent().getSerializableExtra("paymentPreference");
        mToken = (Token) this.getIntent().getSerializableExtra("token");
        hasToFlipCard = (mToken == null);
        mIdentification = new Identification();
        mIdentificationNumberRequired = false;
        mIdentificationTypeRequired = false;
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void verifyValidCreate() {
        if (mKey == null) {
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
                error.printStackTrace();
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

    public boolean hasToFlipCard() {
        return hasToFlipCard;
    }

    protected void initializeGuessingCardNumberController(List<PaymentMethod> paymentMethods) {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(paymentMethods);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(this,
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }

    public void setCardNumberListener() {
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
                mPaymentMethodGuessingController,
                new PaymentMethodSelectionCallback() {
                    @Override
                    public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
                        if (paymentMethodList.size() == 0) {
                            mPaymentMethodGuessingController.blockCardNumbersInput(mCardNumberEditText);
                            setCardNumberError(getString(R.string.mpsdk_invalid_payment_method));
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
                        clearCardNumberError();
                        if (mCurrentPaymentMethod == null) return;
                        mCurrentPaymentMethod = null;
                        mPaymentMethodGuessingController.setSecurityCodeLocation(null);
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

    public void checkFocusOnSecurityCode() {
        if (mPaymentMethodGuessingController.isSecurityCodeRequired()) {
            focusOnSecurityCode();
        }
    }

    public void manageSettings() {
        String bin = mPaymentMethodGuessingController.getSavedBin();
        List<Setting> settings = mCurrentPaymentMethod.getSettings();
        Setting setting = Setting.getSettingByBin(settings, bin);
        setCardNumberLength(setting.getCardNumber().getLength());

        if (mCurrentPaymentMethod.isSecurityCodeRequired(bin)) {
            SecurityCode securityCode = setting.getSecurityCode();
            mPaymentMethodGuessingController.setSecurityCodeRestrictions(true, securityCode);
            setSecurityCodeViewRestrictions(securityCode);
            showSecurityCodeView();
        } else {
            mSecurityCode = null;
            mPaymentMethodGuessingController.setSecurityCodeRestrictions(false, null);
            hideSecurityCodeView();
        }
    }

    public void manageAdditionalInfoNeeded() {
        mIdentificationTypeRequired = mCurrentPaymentMethod.isIdentificationTypeRequired();
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
                    Log.d("lala",error.getMessage());
                }
            });
        }
    }

    private void showSecurityCodeView() {
        mSecurityCodeEditView.setVisibility(View.VISIBLE);
        mSecurityCodeErrorContainer.setVisibility(View.VISIBLE);
    }

    private void hideSecurityCodeView() {
        clearSecurityCodeFront();
        mSecurityCodeEditView.setVisibility(View.GONE);
        mSecurityCodeErrorContainer.setVisibility(View.GONE);
    }

    private void setCardNumberLength(int maxLength) {
        mPaymentMethodGuessingController.setCardNumberLength(maxLength);
        mPaymentMethodGuessingController.setInputMaxLength(mCardNumberEditText, maxLength);
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
        mCardSecurityCodeError.setText(message);
        mSecurityCodeState = CardInterface.ERROR_STATE;
        String location = mPaymentMethodGuessingController.getSecurityCodeLocation();
        if (location != null) {
            if (location.equals(CardInterface.CARD_SIDE_BACK)) {
                if (showingBack() && mBackFragment != null) {
                    checkFlipCardToBack();
                }
            } else if (location.equals(CardInterface.CARD_SIDE_FRONT)) {
                if (showingFront() && mFrontFragment != null) {
                    checkFlipCardToFront();
                }
            }
        }
        if (requestFocus) {
            mCardSecurityCodeEditText.requestFocus();
        }
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mPaymentMethodGuessingController == null) {
            return CardInterface.CARD_SIDE_BACK;
        }
        return mPaymentMethodGuessingController.getSecurityCodeLocation();
    }

    private void clearCardSecurityCodeErrorView() {
        mCardSecurityCodeError.setText("");
        mSecurityCodeState = CardInterface.NORMAL_STATE;
    }

    private void setCardIdentificationErrorView(String message, boolean requestFocus) {
        mCardIdentificationNumberError.setText(message);
        saveCardIdentificationNumberState(CardInterface.ERROR_STATE);
        if (requestFocus) {
            mCardIdentificationNumberEditText.requestFocus();
            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
    }

    private void clearCardIdentificationErrorView() {
        if (getCardIdentificationNumberState().equals(CardInterface.NORMAL_STATE)) {
            return;
        }
        mCardIdentificationNumberError.setText("");
        saveCardIdentificationNumberState(CardInterface.NORMAL_STATE);
    }


    private void clearSecurityCodeFront() {
        mFrontFragment.hideCardSecurityView();
        setCardSecurityCodeFocusListener();
    }

    public void focusOnSecurityCode() {
        String location = mPaymentMethodGuessingController.getSecurityCodeLocation();
        if (location == null || location.equals(CardInterface.CARD_SIDE_BACK)) {
            checkFlipCardToBack();
        } else {
            checkFlipCardToFront();
        }
        mCardSecurityCodeEditText.requestFocus();
    }

    public void changeCardColor(int color, int font) {
        if (!showingBack() && mFrontFragment != null) {
            mFrontFragment.transitionColor(color, font);
        }
        mCurrentColor = color;
    }

    public void changeCardImage(int image) {
        mFrontFragment.transitionImage(image);
        mCurrentImage = image;
    }

    public void clearCardImage() {
        mFrontFragment.clearImage();
    }

    @Override
    public int getCardColor(PaymentMethod paymentMethod) {
        String colorName = "mpsdk_" + paymentMethod.getId().toLowerCase();
        int color = getResources().getIdentifier(colorName, "color", getPackageName());
        mCurrentColor = color;
        return color;
    }

    @Override
    public int getCardFontColor(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR);
        }
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        int color = getResources().getIdentifier(colorName, "color", getPackageName());
        return color;
    }

    private void setCardNumberFocusListener() {
        mCardNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                } else {
                    validateCardNumber(false);
                }
            }
        });
    }

    private void setCardNameFocusListener() {
        mCardHolderNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                } else {
                    validateCardName(false);
                }
            }
        });
    }

    private void setCardExpiryDateFocusListener() {
        mCardExpiryDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                } else {
                    validateExpiryDate(false);
                }
            }
        });
    }

    private void setCardSecurityCodeFocusListener() {
        mCardSecurityCodeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mCardSecurityCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String location = mPaymentMethodGuessingController.getSecurityCodeLocation();
                if (hasFocus) {
                    openKeyboard(mCardSecurityCodeEditText);
                    mCurrentEditingEditText = CARD_SECURITYCODE_INPUT;
                    if (location == null || location.equals(CardInterface.CARD_SIDE_BACK)) {
                        checkFlipCardToBack();
                    } else {
                        checkFlipCardToFront();
                    }
                } else {
                    validateSecurityCode(false);
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
                checkTransitionCardToId();
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                mCardIdentificationNumberEditText.requestFocus();
                return false;
            }
        });
        mCardIdentificationNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                } else {
                    validateIdentificationNumber(false);
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

                if (s.length() == mPaymentMethodGuessingController.getSecurityCodeLength()) {
                    mSecurityCode = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                if (showingIdentification() && mCardIdentificationFragment != null) {
                    mCardIdentificationFragment.afterNumberTextChanged(s);
                }
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
        if (mPaymentMethodGuessingController.isSecurityCodeRequired() &&
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
        return mPaymentMethodGuessingController == null ||
                mPaymentMethodGuessingController.isSecurityCodeRequired();
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
        if (mPaymentMethodGuessingController.isSecurityCodeRequired()) {
            try {
                cardToken.validateSecurityCode(this, mCurrentPaymentMethod);
                clearCardSecurityCodeErrorView();
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
                ApiUtil.finishWithApiException(getParent(), error);
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
                }
            }
            mCardToken.validateCardNumber(this, mCurrentPaymentMethod);
            clearCardNumberError();
            return true;
        } catch (Exception e) {
            setCardNumberError(e.getMessage());
            if (requestFocus) {
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
            clearCardNameError();
            return true;
        } else {
            setCardNameError();
            if (requestFocus) {
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
            clearCardDateError();
            return true;
        } else {
            setCardDateError();
            if (requestFocus) {
                mCardExpiryDateEditText.requestFocus();
            }
            return false;
        }
    }

    public boolean validateSecurityCode(boolean requestFocus) {
        mCardToken.setSecurityCode(mSecurityCode);
        try {
            mCardToken.validateSecurityCode(this, mCurrentPaymentMethod);
            clearCardSecurityCodeErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage(), requestFocus);
            return false;
        }
    }

    public boolean validateIdentificationNumber(boolean requestFocus) {
        mIdentification.setNumber(getCardIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentification();
        if (ans) {
            clearCardIdentificationErrorView();
        } else {
            setCardIdentificationErrorView(getString(R.string.mpsdk_invalid_identification_number), requestFocus);
        }
        return ans;
    }

    public String buildNumberWithMask(CharSequence s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= s.length(); i++) {
            sb.append(s.charAt(i - 1));
            if (i % 4 == 0) {
                sb.append("  ");
            }
        }
        return sb.toString();
    }

    public void clearCardNumberError() {
        mCardNumberError.setText("");
        mCardNumberState = CardInterface.NORMAL_STATE;
    }

    public void setCardNumberError(String message) {
        mCardNumberError.setText(message);
        mCardNumberState = CardInterface.ERROR_STATE;
    }

    public void clearCardNameError() {
        mCardholderNameError.setText("");
        mCardNameState = CardInterface.NORMAL_STATE;
    }

    public void setCardNameError() {
        mCardholderNameError.setText(getString(R.string.mpsdk_invalid_empty_name));
        mCardNameState = CardInterface.ERROR_STATE;
    }

    public void clearCardDateError() {
        mCardExpiryDateError.setText("");
        mExpiryDateState = CardInterface.NORMAL_STATE;
    }

    public void setCardDateError() {
        mCardExpiryDateError.setText(getString(R.string.mpsdk_invalid_expiry_date));
        mExpiryDateState = CardInterface.ERROR_STATE;
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
                            mPaymentMethodGuessingController.setIssuer(issuers.get(0));
                            checkFlipCardToFront();
                            finishWithResult();
                        } else {
                            fadeInIssuersActivity(issuers);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public void fadeInIssuersActivity(final List<Issuer> issuers) {
        checkFlipCardToFront();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(500);
                        startIssuersActivity(issuers);
                    }
                } catch (InterruptedException ex) {
                    //TODO
                }
            }
        };
        thread.start();
    }

    public void startIssuersActivity(final List<Issuer> issuers) {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                        .setActivity(mActivity)
                        .setPublicKey(mKey)
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
                mPaymentMethodGuessingController.setIssuer(mSelectedIssuer);
                checkFlipCardToFront();
                finishWithResult();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", mCurrentPaymentMethod);
        returnIntent.putExtra("token", mToken);
        returnIntent.putExtra("issuer", mSelectedIssuer);
        returnIntent.putExtra("cardHolderName", mCardHolderName);
        returnIntent.putExtra("securityCodeLocation",
                mPaymentMethodGuessingController.getSecurityCodeLocation());
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

}
