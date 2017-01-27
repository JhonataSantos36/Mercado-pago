package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mercadopago.callbacks.card.CardSecurityCodeEditTextCallback;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.card.CardSecurityCodeTextWatcher;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.SecurityCodeActivityView;


/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodeActivity extends AppCompatActivity implements SecurityCodeActivityView, TimerObserver {

    protected SecurityCodePresenter mPresenter;
    protected Activity mActivity;

    //View controls
    protected ProgressBar mProgressBar;
    protected DecorationPreference mDecorationPreference;
    protected MPEditText mSecurityCodeEditText;
    protected FrameLayout mContinueButton;
    protected MPTextView mErrorText;
    protected FrameLayout mBackground;
    //ViewMode
    protected boolean mLowResActive;
    //Normal View
    protected FrameLayout mCardContainer;
    protected CardView mCardView;
    protected MPTextView mTimerTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new SecurityCodePresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        analyzeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void getActivityParameters() {
        String publicKey = MercadoPagoContext.getInstance().getPublicKey();
        mDecorationPreference = MercadoPagoContext.getInstance().getDecorationPreference();

        CardInfo cardInfo = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("cardInfo"), CardInfo.class);
        Card card = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("card"), Card.class);
        Token token = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        mPresenter.setPublicKey(publicKey);
        mPresenter.setToken(token);
        mPresenter.setCard(card);
        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setCardInfo(cardInfo);
    }

    private void analyzeLowRes() {
        this.mLowResActive = ScaleUtil.isLowRes(this);
    }

    public void setContentView() {
        MPTracker.getInstance().trackScreen("SECURITY_CODE_CARD", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        setContentViewNormal();
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_security_code);
    }

    private void initializeViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mSecurityCodeEditText = (MPEditText) findViewById(R.id.mpsdkCardSecurityCode);
        mContinueButton = (FrameLayout) findViewById(R.id.mpsdkSecurityCodeNextButton);
        mErrorText = (MPTextView) findViewById(R.id.mpsdkSecurityCodeErrorText);
        mBackground = (FrameLayout) findViewById(R.id.mpsdkSecurityCodeActivityBackground);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkCardViewContainer);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mContinueButton.setVisibility(View.GONE);
    }

    @Override
    public void stopLoadingView() {
        mProgressBar.setVisibility(View.GONE);
        mContinueButton.setVisibility(View.VISIBLE);
    }


    private void loadViews() {
        loadNormalViews();
    }

    private void loadNormalViews() {
        mCardView = new CardView(mActivity);
        mCardView.setSize(CardRepresentationModes.BIG_SIZE);
        mCardView.inflateInParent(mCardContainer, true);
        mCardView.initializeControls();
        mCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        mCardView.setSecurityCodeLength(mPresenter.getSecurityCodeLength());
        mCardView.setSecurityCodeLocation(mPresenter.getSecurityCodeLocation());
        mCardView.setCardNumberLength(mPresenter.getCardNumberLength());
        mCardView.setLastFourDigits(mPresenter.getCardInfo().getLastFourDigits());
        if (mPresenter.getSecurityCodeLocation().equals(CardView.CARD_SIDE_BACK)) {
            mCardView.draw(CardView.CARD_SIDE_BACK);
        } else {
            mCardView.draw(CardView.CARD_SIDE_FRONT);
            mCardView.drawFullCard();
            mCardView.drawEditingSecurityCode("");
        }
    }

    private void decorate() {
        if (isDecorationEnabled()) {
            mBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
            if(mTimerTextView != null) {
                ColorsUtil.decorateTextView(mDecorationPreference, mTimerTextView, this);
            }
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    @Override
    public void onValidStart() {
        mPresenter.initializeMercadoPago();
        initializeViews();
        mPresenter.initializeSecurityCodeSettings();
        loadViews();
        decorate();
        showTimer();
        setSecurityCodeListeners();
        setContinueButtonListeners();
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()){
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
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
                mPresenter.saveSecurityCode(s.toString());
                mCardView.setSecurityCodeLocation(mPresenter.getSecurityCodeLocation());
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

    private void setContinueButtonListeners() {
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.validateSecurityCodeInput();
            }
        });
    }

    @Override
    public void setErrorView(String message) {
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(message);
        mSecurityCodeEditText.toggleLineColorOnError(true);
        mSecurityCodeEditText.requestFocus();
    }

    @Override
    public void clearErrorView() {
        mErrorText.setText("");
        mErrorText.setVisibility(View.INVISIBLE);
        mSecurityCodeEditText.toggleLineColorOnError(false);
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
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mPresenter.getToken()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        this.finish();
    }
}
