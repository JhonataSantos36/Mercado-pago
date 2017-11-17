package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mercadopago.callbacks.card.CardSecurityCodeEditTextCallback;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.exceptions.CardTokenException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.listeners.card.CardSecurityCodeTextWatcher;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.providers.SecurityCodeProviderImpl;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MPCardUIUtils;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.SecurityCodeActivityView;


/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodeActivity extends MercadoPagoBaseActivity implements SecurityCodeActivityView, TimerObserver {

    private static final String PRESENTER_BUNDLE = "mSecurityCodePresenter";
    private static final String DECORATION_PREFERENCE_BUNDLE = "mDecorationPreference";
    private static final String PUBLIC_KEY_BUNDLE = "mMerchantPublicKey";
    private static final String PRIVATE_KEY_BUNDLE = "mPrivateKey";
    private static final String ESC_ENABLED_BUNDLE = "mEscEnabled";
    private static final String REASON_BUNDLE = "mReason";

    protected SecurityCodePresenter mSecurityCodePresenter;
    protected Activity mActivity;

    public static final String ERROR_STATE = "textview_error";
    public static final String NORMAL_STATE = "textview_normal";
    //Parameters
    protected String mMerchantPublicKey;
    protected String mPrivateKey;
    protected boolean mEscEnabled;
    protected String mReason;

    //View controls
    protected ProgressBar mProgressBar;
    protected DecorationPreference mDecorationPreference;
    protected MPEditText mSecurityCodeEditText;
    protected FrameLayout mNextButton;
    protected FrameLayout mBackButton;
    protected MPTextView mNextButtonText;
    protected MPTextView mBackButtonText;
    protected LinearLayout mButtonContainer;
    protected FrameLayout mErrorContainer;
    protected MPTextView mErrorTextView;
    protected String mErrorState;
    protected FrameLayout mBackground;
    protected ImageView mSecurityCodeCardIcon;
    //ViewMode
    protected boolean mLowResActive;
    //Normal View
    protected FrameLayout mCardContainer;
    protected CardView mCardView;
    protected MPTextView mTimerTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        if (savedInstanceState == null) {
            createPresenter();
            getActivityParameters();
            configurePresenter();
            setTheme();
            analyzeLowRes();
            setContentView();
            mSecurityCodePresenter.initialize();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DECORATION_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mDecorationPreference));
        outState.putString(PRESENTER_BUNDLE, JsonUtil.getInstance().toJson(mSecurityCodePresenter));
        outState.putString(PUBLIC_KEY_BUNDLE, mMerchantPublicKey);
        outState.putString(PRIVATE_KEY_BUNDLE, mPrivateKey);
        outState.putBoolean(ESC_ENABLED_BUNDLE, mEscEnabled);
        outState.putString(REASON_BUNDLE, mReason);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSecurityCodePresenter = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PRESENTER_BUNDLE), SecurityCodePresenter.class);
            mDecorationPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(DECORATION_PREFERENCE_BUNDLE), DecorationPreference.class);
            mMerchantPublicKey = savedInstanceState.getString(PUBLIC_KEY_BUNDLE);
            mPrivateKey = savedInstanceState.getString(PRIVATE_KEY_BUNDLE);
            mEscEnabled = savedInstanceState.getBoolean(ESC_ENABLED_BUNDLE);
            mReason = savedInstanceState.getString(REASON_BUNDLE);

            configurePresenter();
            setTheme();
            analyzeLowRes();
            setContentView();
            mSecurityCodePresenter.initialize();
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void showApiExceptionError(ApiException exception, String requestOrigin) {
        ApiUtil.showApiExceptionError(mActivity, exception, mMerchantPublicKey, requestOrigin);
    }

    private void setTheme() {
        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
    }

    private void createPresenter() {
        if (mSecurityCodePresenter == null) {
            mSecurityCodePresenter = new SecurityCodePresenter();
        }
    }

    private void configurePresenter() {
        if (mSecurityCodePresenter != null) {
            mSecurityCodePresenter.attachView(this);
            SecurityCodeProviderImpl provider = new SecurityCodeProviderImpl(this, mMerchantPublicKey, mPrivateKey, mEscEnabled);
            mSecurityCodePresenter.attachResourcesProvider(provider);
        }
    }

    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPrivateKey = getIntent().getStringExtra("payerAccessToken");
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        mEscEnabled = getIntent().getBooleanExtra("escEnabled", false);
        mReason = getIntent().getStringExtra("reason");

        CardInfo cardInfo = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("cardInfo"), CardInfo.class);
        Card card = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("card"), Card.class);
        Token token = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        PaymentRecovery paymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);

        mSecurityCodePresenter.setToken(token);
        mSecurityCodePresenter.setCard(card);
        mSecurityCodePresenter.setPaymentMethod(paymentMethod);
        mSecurityCodePresenter.setCardInfo(cardInfo);
        mSecurityCodePresenter.setPaymentRecovery(paymentRecovery);
    }

    private void analyzeLowRes() {
        this.mLowResActive = ScaleUtil.isLowRes(this);
    }

    public void setContentView() {
        setContentViewNormal();
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_security_code);
    }

    private void initializeControls() {
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mSecurityCodeEditText = (MPEditText) findViewById(R.id.mpsdkCardSecurityCode);
        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButtonText = (MPTextView) findViewById(R.id.mpsdkNextButtonText);
        mBackButtonText = (MPTextView) findViewById(R.id.mpsdkBackButtonText);
        mButtonContainer = (LinearLayout) findViewById(R.id.mpsdkButtonContainer);
        mErrorContainer = (FrameLayout) findViewById(R.id.mpsdkErrorContainer);
        mErrorTextView = (MPTextView) findViewById(R.id.mpsdkErrorTextView);
        mBackground = (FrameLayout) findViewById(R.id.mpsdkSecurityCodeActivityBackground);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkCardViewContainer);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
        mProgressBar.setVisibility(View.GONE);
        mSecurityCodeCardIcon = (ImageView) findViewById(R.id.mpsdkSecurityCodeCardIcon);
        setListeners();
    }

    private void setListeners() {
        setSecurityCodeListeners();
        setButtonsListeners();
    }

    @Override
    public void showLoadingView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
        mBackButton.setVisibility(View.INVISIBLE);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void stopLoadingView() {
        mProgressBar.setVisibility(View.GONE);
    }


    private void loadViews() {
        loadNormalViews();
    }

    private void loadNormalViews() {
        mCardView = new CardView(mActivity);
        mCardView.setSize(CardRepresentationModes.BIG_SIZE);
        mCardView.inflateInParent(mCardContainer, true);
        mCardView.initializeControls();
        mCardView.setPaymentMethod(mSecurityCodePresenter.getPaymentMethod());
        mCardView.setSecurityCodeLength(mSecurityCodePresenter.getSecurityCodeLength());
        mCardView.setSecurityCodeLocation(mSecurityCodePresenter.getSecurityCodeLocation());
        mCardView.setCardNumberLength(mSecurityCodePresenter.getCardNumberLength());
        mCardView.setLastFourDigits(mSecurityCodePresenter.getCardInfo().getLastFourDigits());
        mCardView.draw(CardView.CARD_SIDE_FRONT);
        mCardView.drawFullCard();
        mCardView.drawEditingSecurityCode("");
        mSecurityCodePresenter.setSecurityCodeCardType();
    }


    private void setSecurityCodeCardColorFilter() {
        int color = MPCardUIUtils.getCardColor(mSecurityCodePresenter.getPaymentMethod(), this);
        mSecurityCodeCardIcon.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.DST_OVER);
    }

    private void decorate() {
        if (isDecorationEnabled()) {
            decorateButtons();
            mBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
            if (mTimerTextView != null) {
                ColorsUtil.decorateTextView(mDecorationPreference, mTimerTextView, this);
            }
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void decorateButtons() {
        mNextButtonText.setTextColor(mDecorationPreference.getDarkFontColor(this));
        mBackButtonText.setTextColor(mDecorationPreference.getDarkFontColor(this));
    }

    @Override
    public void initialize() {
        initializeControls();
        mSecurityCodePresenter.initializeSecurityCodeSettings();
        loadViews();
        decorate();
    }

    @Override
    public void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    @Override
    public void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mMerchantPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .setTrackingStrategy(TrackingUtil.BATCH_STRATEGY)
                .build();

        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setScreenId(TrackingUtil.SCREEN_ID_CARD_FORM + mSecurityCodePresenter.getPaymentMethod().getPaymentTypeId() + TrackingUtil.CARD_SECURITY_CODE_VIEW)
                .setScreenName(TrackingUtil.SCREEN_NAME_SECURITY_CODE)
                .addMetaData(TrackingUtil.METADATA_SECURITY_CODE_REASON, mReason)
                .build();

        mpTrackingContext.trackEvent(event);
    }

    @Override
    public void showBackSecurityCodeCardView() {
        int id = getResources().getIdentifier("mpsdk_tiny_card_cvv_screen", "drawable", getPackageName());
        mSecurityCodeCardIcon.setImageResource(id);
        setSecurityCodeCardColorFilter();
    }

    @Override
    public void showFrontSecurityCodeCardView() {
        int id = getResources().getIdentifier("mpsdk_amex_tiny_card_cvv_screen", "drawable", getPackageName());
        mSecurityCodeCardIcon.setImageResource(id);
        setSecurityCodeCardColorFilter();
    }

    @Override
    public void setSecurityCodeInputMaxLength(int length) {
        setInputMaxLength(mSecurityCodeEditText, length);
    }


    private void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    private void setSecurityCodeListeners() {
        mSecurityCodeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEditText(mSecurityCodeEditText, event);
                return true;
            }
        });
        mSecurityCodeEditText.addTextChangedListener(new CardSecurityCodeTextWatcher(new CardSecurityCodeEditTextCallback() {
            @Override
            public void checkOpenKeyboard() {
                openKeyboard(mSecurityCodeEditText);
            }

            @Override
            public void saveSecurityCode(CharSequence s) {
                mSecurityCodePresenter.saveSecurityCode(s.toString());
                mCardView.setSecurityCodeLocation(mSecurityCodePresenter.getSecurityCodeLocation());
                mCardView.drawEditingSecurityCode(s.toString());
            }

            @Override
            public void changeErrorView() {
                clearErrorView();
            }

            @Override
            public void toggleLineColorOnError(boolean toggle) {
                mSecurityCodeEditText.toggleLineColorOnError(toggle);
            }
        }));
    }

    private void setButtonsListeners() {
        setNextButtonListeners();
        setBackButtonListeners();
    }

    private void setNextButtonListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSecurityCodePresenter.validateSecurityCodeInput();
            }
        });
    }

    public void setBackButtonListeners() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    private void setErrorState(String mErrorState) {
        this.mErrorState = mErrorState;
    }

    @Override
    public void setErrorView(CardTokenException exception) {
        mSecurityCodeEditText.toggleLineColorOnError(true);
        mButtonContainer.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
        String errorText = ExceptionHandler.getErrorMessage(this, exception);
        mErrorTextView.setText(errorText);
        setErrorState(ERROR_STATE);
    }

    @Override
    public void showError(MercadoPagoError error, String requestOrigin) {
        if (error.isApiException()) {
            showApiExceptionError(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error, mMerchantPublicKey);
        }
    }

    @Override
    public void clearErrorView() {
        mSecurityCodeEditText.toggleLineColorOnError(false);
        mButtonContainer.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText("");
        setErrorState(NORMAL_STATE);
    }

    private void onTouchEditText(MPEditText editText, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            openKeyboard(editText);
        }
    }

    private void openKeyboard(MPEditText ediText) {
        ediText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mSecurityCodePresenter.recoverFromFailure();
            } else {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mSecurityCodePresenter.getToken()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        setResult(MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE);
        this.finish();
    }

}
