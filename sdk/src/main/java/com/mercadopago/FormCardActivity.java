package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.callbacks.card.CardExpiryDateEditTextCallback;
import com.mercadopago.callbacks.card.CardNumberEditTextCallback;
import com.mercadopago.callbacks.card.CardholderNameEditTextCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.card.CardExpiryDateTextWatcher;
import com.mercadopago.listeners.card.CardNumberTextWatcher;
import com.mercadopago.listeners.card.CardholderNameTextWatcher;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.FormCardPresenter;
import com.mercadopago.uicontrollers.card.BackCardView;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.FormCardActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class FormCardActivity extends AppCompatActivity implements FormCardActivityView {

    public static final String CARD_NUMBER_INPUT = "cardNumber";
    public static final String CARDHOLDER_NAME_INPUT = "cardHolderName";
    public static final String CARD_EXPIRYDATE_INPUT = "cardExpiryDate";
    public static final String CARD_SECURITYCODE_INPUT = "cardSecurityCode";
    public static final String CARD_IDENTIFICATION_INPUT = "cardIdentification";

    protected FormCardPresenter mPresenter;
    private Activity mActivity;

    //View controls
    private DecorationPreference mDecorationPreference;

    //ViewMode
    protected boolean mLowResActive;
    //View Low Res
    private Toolbar mLowResToolbar;
    private MPTextView mLowResTitleToolbar;
    //View Normal
    private Toolbar mNormalToolbar;
    private MPTextView mBankDealsTextView;
    private FrameLayout mCardBackground;
    private FrameLayout mCardContainer;
    private FrontCardView mFrontCardView;
    private BackCardView mBackCardView;

    //Input Views
    private ProgressBar mProgressBar;
    private LinearLayout mInputContainer;
    private Spinner mIdentificationTypeSpinner;
    private LinearLayout mIdentificationTypeContainer;
    private FrameLayout mNextButton;
    private FrameLayout mBackButton;
    private FrameLayout mBackInactiveButton;
    private LinearLayout mButtonContainer;
    private MPEditText mCardNumberEditText;
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mSecurityCodeEditText;
    private LinearLayout mCardNumberInput;
    private LinearLayout mCardholderNameInput;
    private LinearLayout mCardExpiryDateInput;
    private LinearLayout mCardIdentificationInput;
    private LinearLayout mCardSecurityCodeInput;

    //Input Controls
    private String mCurrentEditingEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new FormCardPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        analizeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private void getActivityParameters() {
        String publicKey = this.getIntent().getStringExtra("publicKey");
        PaymentRecovery paymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);
        Token token = null;
        Card card = null;
        PaymentMethod paymentMethod = null;
        Issuer issuer = null;
        if (paymentRecovery == null){
            token = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
            card = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
            if(card != null) {
                paymentMethod = card.getPaymentMethod();
            }
        } else {
            issuer = paymentRecovery.getIssuer();
            token = paymentRecovery.getToken();
        }
        List<PaymentMethod> paymentMethodList;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        Identification identification = new Identification();
        boolean identificationNumberRequired = false;
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }

        mPresenter.setPublicKey(publicKey);
        mPresenter.setPaymentRecovery(paymentRecovery);
        mPresenter.setToken(token);
        mPresenter.setCard(card);
        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setIssuer(issuer);
        mPresenter.setPaymentMethodList(paymentMethodList);
        mPresenter.setIdentification(identification);
        mPresenter.setIdentificationNumberRequired(identificationNumberRequired);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setCardInformation();
    }

    private void analizeLowRes() {
        this.mLowResActive = ScaleUtil.isLowRes(this);
    }

    private void setContentView() {
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_form_card_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_form_card_normal);
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onValidStart() {
        mPresenter.initializeMercadoPago();
        initializeViews();
        loadViews();
        decorate();
        mPresenter.loadPaymentMethods();
    }

    private void initializeViews() {
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkLowResToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkTransparentToolbar);
            mCardBackground = (FrameLayout) findViewById(R.id.mpsdkCardBackground);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
        }
        mIdentificationTypeContainer = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationTypeContainer);
        mIdentificationTypeSpinner = (Spinner) findViewById(R.id.mpsdkCardIdentificationType);
        mBankDealsTextView = (MPTextView) findViewById(R.id.mpsdkBankDealsText);
        mCardNumberEditText = (MPEditText) findViewById(R.id.mpsdkCardNumber);
        mCardHolderNameEditText = (MPEditText) findViewById(R.id.mpsdkCardholderName);
        mCardExpiryDateEditText = (MPEditText) findViewById(R.id.mpsdkCardExpiryDate);
        mSecurityCodeEditText = (MPEditText) findViewById(R.id.mpsdkCardSecurityCode);
        mInputContainer = (LinearLayout) findViewById(R.id.mpsdkInputContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mBackInactiveButton = (FrameLayout) findViewById(R.id.mpsdkBackInactiveButton);
        mButtonContainer = (LinearLayout) findViewById(R.id.mpsdkButtonContainer);
        mCardNumberInput = (LinearLayout) findViewById(R.id.mpsdkCardNumberInput);
        mCardholderNameInput = (LinearLayout) findViewById(R.id.mpsdkCardholderNameInput);
        mCardExpiryDateInput = (LinearLayout) findViewById(R.id.mpsdkExpiryDateInput);
        mCardIdentificationInput = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationInput);
        mCardSecurityCodeInput = (LinearLayout) findViewById(R.id.mpsdkCardSecurityCodeContainer);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInputContainer() {
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mInputContainer.setVisibility(View.VISIBLE);
        requestCardNumberFocus();
    }

    private void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        //TODO poner el payment type, y cambiar el titulo en documento
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_form_card_title, "Crédito"));
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.EDIT_FRONT);
        mFrontCardView.setSize(CardRepresentationModes.EXTRA_BIG_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInformation() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInformation().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();

        mBackCardView = new BackCardView(mActivity);
        mBackCardView.setSize(CardRepresentationModes.EXTRA_BIG_SIZE);
        mBackCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInformation() != null) {
            mBackCardView.setSecurityCodeLength(mPresenter.getSecurityCodeLength());
        }
    }

    private void loadToolbarArrow(Toolbar toolbar) {
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
    }

    private void decorate() {
        if (isDecorationEnabled()) {
            if (mLowResActive) {
                decorateLowRes();
            } else {
                decorateNormal();
            }
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void decorateLowRes() {
        ColorsUtil.decorateLowResToolbar(mLowResToolbar, mLowResTitleToolbar, mDecorationPreference,
                getSupportActionBar(), this);
        ColorsUtil.decorateTextView(mDecorationPreference, mBankDealsTextView, this);
    }

    private void decorateNormal() {
        ColorsUtil.decorateTransparentToolbar(mNormalToolbar, mBankDealsTextView, mDecorationPreference,
                getSupportActionBar(), this);
        mFrontCardView.decorateCardBorder(mDecorationPreference.getLighterColor());
        mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
    }

    private String getCardNumberTextTrimmed() {
        return mCardNumberEditText.getText().toString().replaceAll("\\s", "");
    }

    @Override
    public void setCardNumberListeners(PaymentMethodGuessingController controller) {
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
            controller,
            new PaymentMethodSelectionCallback() {
                @Override
                public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
                    if (paymentMethodList.size() == 0 || paymentMethodList.size() > 1) {
//                        blockCardNumbersInput(mCardNumberEditText);
//                        setErrorView(getString(R.string.mpsdk_invalid_payment_method));
                    } else {
                        onPaymentMethodSet(paymentMethodList.get(0));
                    }
                }

                @Override
                public void onPaymentMethodSet(PaymentMethod paymentMethod) {
                    if (mPresenter.getPaymentMethod() == null) {
                        mPresenter.setPaymentMethod(paymentMethod);
                        mPresenter.configureWithSettings();
                        mPresenter.loadIdentificationTypes();
                        mFrontCardView.setPaymentMethod(paymentMethod);
                        mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
                        mFrontCardView.setSecurityCodeLength(mPresenter.getSecurityCodeLength());
                        mFrontCardView.updateCardNumberMask(getCardNumberTextTrimmed());
                        mFrontCardView.transitionPaymentMethodSet();
                    }
                }

                @Override
                public void onPaymentMethodCleared() {
//                    clearErrorView();
                    clearCardNumberInputLength();
                    if (mPresenter.getPaymentMethod() == null) return;
                    mPresenter.setPaymentMethod(null);
                    mSecurityCodeEditText.getText().clear();
//                    mCardToken = new CardToken("", null, null, "", "", "", "");
                    mPresenter.setIdentificationNumberRequired(true);
                    mPresenter.setSecurityCodeRequired(true);
                    mFrontCardView.transitionClearPaymentMethod();
                }
            },
            new CardNumberEditTextCallback() {
                @Override
                public void openKeyboard() {
//                    openKeyboard(mCardNumberEditText);
                }

                @Override
                public void saveCardNumber(CharSequence s) {
                    mPresenter.saveCardNumber(s.toString());
                    mFrontCardView.drawEditingCardNumber(s.toString());
                }

                @Override
                public void appendSpace(CharSequence s) {
                    if (MPCardMaskUtil.needsMask(s, mPresenter.getCardNumberLength())) {
                        mCardNumberEditText.append(" ");
                    }
                }

                @Override
                public void deleteChar(CharSequence s) {
                    if (MPCardMaskUtil.needsMask(s, mPresenter.getCardNumberLength())) {
                        mCardNumberEditText.getText().delete(s.length() - 1, s.length());
                    }
                }

                @Override
                public void checkChangeErrorView() {

                }

                @Override
                public void toggleLineColorOnError(boolean toggle) {

                }
            }));
    }

    @Override
    public void setNextButtonListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCurrentEditText();
            }
        });
    }

    @Override
    public void setBackButtonListeners() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentEditingEditText.equals(CARD_NUMBER_INPUT)) {
                    checkIsEmptyOrValid();
                }
            }
        });
    }

    @Override
    public void setCardholderNameListeners() {
        mCardHolderNameEditText.addTextChangedListener(new CardholderNameTextWatcher(new CardholderNameEditTextCallback() {
            @Override
            public void openKeyboard() {

            }

            @Override
            public void saveCardholderName(CharSequence s) {
                mPresenter.saveCardholderName(s.toString());
                mFrontCardView.drawEditingCardHolderName(s.toString());
            }

            @Override
            public void checkChangeErrorView() {

            }

            @Override
            public void toggleLineColorOnError(boolean toggle) {

            }
        }));
    }

    @Override
    public void setExpiryDateListeners() {
        mCardExpiryDateEditText.addTextChangedListener(new CardExpiryDateTextWatcher(new CardExpiryDateEditTextCallback() {
            @Override
            public void openKeyboard() {

            }

            @Override
            public void saveExpiryMonth(CharSequence s) {
                mPresenter.saveExpiryMonth(s.toString());
                mFrontCardView.drawEditingExpiryMonth(s.toString());
            }

            @Override
            public void saveExpiryYear(CharSequence s) {
                mPresenter.saveExpiryYear(s.toString());
                mFrontCardView.drawEditingExpiryYear(s.toString());
            }

            @Override
            public void checkChangeErrorView() {

            }

            @Override
            public void toggleLineColorOnError(boolean toggle) {

            }

            @Override
            public void appendDivider() {
                mCardExpiryDateEditText.append("/");
            }

            @Override
            public void deleteChar(CharSequence s) {
                mCardExpiryDateEditText.getText().delete(s.length() - 1, s.length());
            }
        }));
    }

    @Override
    public void initializeIdentificationTypes(List<IdentificationType> identificationTypes) {
        mIdentificationTypeSpinner.setAdapter(new IdentificationTypesAdapter(this, identificationTypes));
        mIdentificationTypeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSecurityCodeViewLocation(String location) {
        if (location.equals(FormCardPresenter.CARD_SIDE_FRONT)) {
            mFrontCardView.hasToShowSecurityCode(true);
        }
    }

    @Override
    public void setSecurityCodeInputMaxLength(int length) {
        setInputMaxLength(mSecurityCodeEditText, length);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    @Override
    public void setCardNumberInputMaxLength(int length) {
        setInputMaxLength(mCardNumberEditText, length);
    }

    private void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    private void clearCardNumberInputLength() {
        int maxLength = CardInterface.CARD_NUMBER_MAX_LENGTH;
        setInputMaxLength(mCardNumberEditText, maxLength);
    }

    private void openKeyboard(MPEditText ediText) {
        ediText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
//        fullScrollDown();
    }

    private void requestCardNumberFocus() {
        MPTracker.getInstance().trackScreen("CARD_NUMBER", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        disableBackInputButton();
        mCurrentEditingEditText = CARD_NUMBER_INPUT;
        openKeyboard(mCardNumberEditText);
        mFrontCardView.drawEditingCardNumber(mPresenter.getCardNumber());
    }

    private void requestCardHolderNameFocus() {
        if (!mPresenter.validateCardNumber()) {
            return;
        }
        MPTracker.getInstance().trackScreen("CARD_HOLDER_NAME", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        enableBackInputButton();
        mCurrentEditingEditText = CARDHOLDER_NAME_INPUT;
        openKeyboard(mCardHolderNameEditText);
        mFrontCardView.drawEditingCardHolderName(mPresenter.getCardholderName());
    }

    private void requestExpiryDateFocus() {
        if (!mPresenter.validateCardName()) {
            return;
        }
        MPTracker.getInstance().trackScreen("CARD_EXPIRY_DATE", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        enableBackInputButton();
        mCurrentEditingEditText = CARD_EXPIRYDATE_INPUT;
        openKeyboard(mCardExpiryDateEditText);
        mFrontCardView.drawEditingExpiryMonth(mPresenter.getExpiryMonth());
        mFrontCardView.drawEditingExpiryYear(mPresenter.getExpiryYear());
    }

    private void requestSecurityCodeFocus() {
        if (!mPresenter.validateExpiryDate()) {
            return;
        }
        if (mCurrentEditingEditText.equals(CARD_EXPIRYDATE_INPUT) ||
                mCurrentEditingEditText.equals(CARD_IDENTIFICATION_INPUT) ||
                mCurrentEditingEditText.equals(CARD_SECURITYCODE_INPUT)) {
            MPTracker.getInstance().trackScreen("CARD_SECURITY_CODE", "2", mPresenter.getPublicKey(),
                    BuildConfig.VERSION_NAME, this);
            enableBackInputButton();
            mCurrentEditingEditText = CARD_SECURITYCODE_INPUT;
            openKeyboard(mSecurityCodeEditText);
            if (mPresenter.getSecurityCodeLocation().equals(FormCardPresenter.CARD_SIDE_BACK)) {
                checkFlipCardToBack(true);
            } else {
//                checkFlipCardToFront(true);
            }
        }
    }

    private void requestIdentificationFocus() {
        if ((mPresenter.isSecurityCodeRequired() && !mPresenter.validateSecurityCode()) ||
                (!mPresenter.isSecurityCodeRequired() && !mPresenter.validateExpiryDate())) {
            return;
        }
        MPTracker.getInstance().trackScreen("IDENTIFICATION_NUMBER", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        enableBackInputButton();
        mCurrentEditingEditText = CARD_IDENTIFICATION_INPUT;
        openKeyboard(mCardNumberEditText);

//        +        checkTransitionCardToId();
    }

    private void disableBackInputButton() {
        mBackButton.setVisibility(View.GONE);
        mBackInactiveButton.setVisibility(View.VISIBLE);
    }

    private void enableBackInputButton() {
        mBackButton.setVisibility(View.VISIBLE);
        mBackInactiveButton.setVisibility(View.GONE);
    }

    @Override
    public void hideIdentificationInput() {
        mCardIdentificationInput.setVisibility(View.GONE);
    }

    @Override
    public void hideSecurityCodeInput() {
        mCardSecurityCodeInput.setVisibility(View.GONE);
    }

    @Override
    public void showIdentificationInput() {
        mCardIdentificationInput.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSecurityCodeInput() {
        mCardSecurityCodeInput.setVisibility(View.VISIBLE);
    }

    private boolean validateCurrentEditText() {
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                if (mPresenter.validateCardNumber()) {
                    mCardNumberInput.setVisibility(View.GONE);
                    requestCardHolderNameFocus();
                    return true;
                }
                return false;
            case CARDHOLDER_NAME_INPUT:
                if (mPresenter.validateCardName()) {
                    mCardholderNameInput.setVisibility(View.GONE);
                    requestExpiryDateFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (mPresenter.validateExpiryDate()) {
                    mCardExpiryDateInput.setVisibility(View.GONE);
                    if (mPresenter.isSecurityCodeRequired()) {
                        requestSecurityCodeFocus();
                    } else if (mPresenter.isIdentificationNumberRequired()) {
                        requestIdentificationFocus();
                    } else {
                        finishWithCardToken();
                    }
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (mPresenter.validateSecurityCode()) {
                    mCardSecurityCodeInput.setVisibility(View.GONE);
                    if (mPresenter.isIdentificationNumberRequired()) {
                        requestIdentificationFocus();
                    } else {
                        finishWithCardToken();
                    }
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (mPresenter.validateIdentificationNumber()) {
                    finishWithCardToken();
                    return true;
                }
                return false;
        }
        return false;
    }

    private boolean checkIsEmptyOrValid() {
        switch (mCurrentEditingEditText) {
            case CARDHOLDER_NAME_INPUT:
                if (mPresenter.checkIsEmptyOrValidCardholderName()) {
                    mCardNumberInput.setVisibility(View.VISIBLE);
                    requestCardNumberFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (mPresenter.checkIsEmptyOrValidExpiryDate()) {
                    mCardholderNameInput.setVisibility(View.VISIBLE);
                    requestCardHolderNameFocus();
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (mPresenter.checkIsEmptyOrValidSecurityCode()) {
                    mCardExpiryDateInput.setVisibility(View.VISIBLE);
                    requestExpiryDateFocus();
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (mPresenter.checkIsEmptyOrValidIdentificationNumber()) {
                    if (mPresenter.isSecurityCodeRequired()) {
                        mCardSecurityCodeInput.setVisibility(View.VISIBLE);
                        requestSecurityCodeFocus();
                    } else {
                        mCardExpiryDateInput.setVisibility(View.VISIBLE);
                        requestExpiryDateFocus();
                    }
                    return true;
                }
                return false;
        }
        return false;
    }

    private void checkFlipCardToBack(boolean showBankDeals) {
//        if (showingFront()) {
            flipCardToBack();
//        } else if (showingIdentification()) {
//            getSupportFragmentManager().popBackStack();
//            mCardSideState = CARD_SIDE_BACK;
//            if (showBankDeals) {
//                mToolbarButton.setVisibility(View.VISIBLE);
//            }
//        }
    }

    private void flipCardToBack() {


        mBackCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInformation() != null) {
            mBackCardView.setSecurityCodeLength(mPresenter.getSecurityCodeLength());
        }


        Animation animFadeIn, animFadeOut;
        animFadeIn= AnimationUtils.loadAnimation(this, R.anim.mpsdk_to_middle_left);
        animFadeOut=AnimationUtils.loadAnimation(this, R.anim.mpsdk_to_middle_left);



        mFrontCardView.getView().startAnimation(animFadeOut);

//        mFrontCardView.hide();

        mBackCardView.inflateInParent(mCardContainer, true);
        mBackCardView.initializeControls();
//
        mBackCardView.draw();
//        mBackCardView.show();

//        mBackCardView.getView().startAnimation(animFadeIn);


    }

    //TODO
    private void finishWithCardToken() {

    }
}
