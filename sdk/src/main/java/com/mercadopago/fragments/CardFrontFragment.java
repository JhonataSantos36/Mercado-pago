package com.mercadopago.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class CardFrontFragment extends android.support.v4.app.Fragment {

    //Card input views
    private MPTextView mCardNumberView;
    private MPTextView mCardholderNameView;
    private MPTextView mCardExpiryMonthView;
    private MPTextView mCardExpiryYearView;
    private MPTextView mCardDateDividerView;
    private MPTextView mCardSecurityCodeView;
    private LinearLayout mBaseCardholderView;
    private FrameLayout mCardNumberClickableZone;
    private FrameLayout mCardSecurityClickableZone;
    private FrameLayout mBaseCard;
    private FrameLayout mBaseSecurity;
    private FrameLayout mColorCard;
    private FrameLayout mBaseImageCard;
    private ImageView mImageCardContainer;
    private GradientDrawable mColorDrawableCard;

    // Input controls
    protected MPEditText mCardHolderNameEditText;
    protected MPEditText mCardNumberEditText;
    protected MPEditText mCardExpiryDateEditText;
    protected MPEditText mCardSecurityEditText;

    //Local vars
    private Animation mAnimFadeIn;
    private Animation mQuickAnim;
    private boolean mAnimate;

    private CardInterface mActivity;

    public CardFrontFragment() {
        this.mAnimate = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_card_front, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCardInputViews();
        setEditTextListeners();
        setAnimationListener();
        mActivity = (CardInterface)getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViews();
        setFontColor();
    }

    public void setCardInputViews() {
        mCardNumberEditText = (MPEditText) getActivity().findViewById(R.id.cardNumber);
        mCardHolderNameEditText = (MPEditText) getActivity().findViewById(R.id.cardholderName);
        mCardExpiryDateEditText = (MPEditText) getActivity().findViewById(R.id.cardExpiryDate);
        mCardSecurityEditText = (MPEditText) getActivity().findViewById(R.id.cardSecurityCode);
        mBaseCardholderView = (LinearLayout) getActivity().findViewById(R.id.baseCardholderContainer);
        mAnimFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        mQuickAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.quick_anim);
        if (getView() != null) {
            mCardNumberView = (MPTextView) getView().findViewById(R.id.cardNumberTextView);
            mCardholderNameView = (MPTextView) getView().findViewById(R.id.cardholderNameView);
            mCardExpiryMonthView = (MPTextView) getView().findViewById(R.id.cardHolderExpiryMonth);
            mCardExpiryYearView = (MPTextView) getView().findViewById(R.id.cardHolderExpiryYear);
            mCardDateDividerView = (MPTextView) getView().findViewById(R.id.cardHolderDateDivider);
            mCardSecurityCodeView = (MPTextView) getView().findViewById(R.id.cardSecurityView);
            mCardNumberClickableZone = (FrameLayout) getView().findViewById(R.id.cardNumberClickableZone);
            mCardSecurityClickableZone = (FrameLayout) getView().findViewById(R.id.cardSecurityClickableZone);
            mBaseCard = (FrameLayout) getView().findViewById(R.id.activity_new_card_form_basecolor_front);
            mBaseSecurity = (FrameLayout) getView().findViewById(R.id.base_card_security_container);
            mColorCard = (FrameLayout) getView().findViewById(R.id.activity_new_card_form_color_front);
            mColorDrawableCard = (GradientDrawable) mColorCard.getBackground();
            mBaseImageCard = (FrameLayout) getView().findViewById(R.id.baseImageCard);
            mImageCardContainer = (ImageView) getView().findViewById(R.id.imageCardContainer);
        }
    }

    public void populateViews() {
        populateCardNumber();
        populateCardName();
        populateCardMonth();
        populateCardYear();
        populateCardImage();
        populateCardColor();
        setCardSecurityView();
    }

    public void disableAnimate() {
        this.mAnimate = false;
    }

    protected void setEditTextListeners() {
        setCardNumberListener();
        setCardholderNameListener();
        setCardExpiryDateListener();
    }

    protected void setAnimationListener() {
        mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mColorCard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideCardSecurityView() {
        mCardSecurityClickableZone.setVisibility(View.INVISIBLE);
    }

    public void onSecurityTextChanged(CharSequence s, int start, int before, int count) {
        mBaseSecurity.setVisibility(View.INVISIBLE);
        mCardSecurityCodeView.setVisibility(View.VISIBLE);
        mCardSecurityCodeView.setText(s);
    }

    public void afterSecurityTextChanged(Editable s) {
        mActivity.saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityCodeView.setVisibility(View.INVISIBLE);
            mBaseSecurity.setVisibility(View.VISIBLE);
            mActivity.saveCardSecurityCode(null);
        }
    }

    public void setCardSecurityView() {
        if (!mActivity.isSecurityCodeRequired() || mActivity.getSecurityCodeLocation() == null ||
                mActivity.getSecurityCodeLocation().equals(CardInterface.CARD_SIDE_BACK)) {
            return;
        }
        mCardSecurityClickableZone.setVisibility(View.VISIBLE);
        String securityCode = mActivity.getSecurityCode();
        if (securityCode == null || securityCode.equals("")) {
            mBaseSecurity.setVisibility(View.VISIBLE);
            mCardSecurityCodeView.setVisibility(View.INVISIBLE);
        } else {
            mBaseCard.setVisibility(View.INVISIBLE);
            mCardSecurityCodeView.setVisibility(View.VISIBLE);
            setText(mCardSecurityCodeView, securityCode, CardInterface.FULL_TEXT_VIEW_COLOR);
        }

    }

    protected void setCardExpiryDateListener() {
        if (mCardExpiryDateEditText != null) {
            mCardExpiryDateEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (start <= 2) {
                        CharSequence month = s;
                        if (s.length() == 3) {
                            month = s.subSequence(0,2);
                        }
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardExpiryMonthView, month, color);
                            mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(),
                                    color));
                        } else {
                            setText(mCardExpiryMonthView, month, CardInterface.FULL_TEXT_VIEW_COLOR);
                            mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(),
                                    CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryMonth(month.toString());
                    } else {
                        CharSequence year = s.subSequence(3, s.length());
                        mCardExpiryYearView.setText(year);

                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryYearView.setTextColor(ContextCompat.getColor(getContext(), color));
                        } else {
                            mCardExpiryYearView.setTextColor(ContextCompat.getColor(getContext(),
                                    CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryYear(year.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 3) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardExpiryYearView, getResources().getString(R.string.mpsdk_card_expiry_year_hint),
                                    color);
                        } else {
                            setText(mCardExpiryYearView, getResources().getString(R.string.mpsdk_card_expiry_year_hint),
                                    CardInterface.FULL_TEXT_VIEW_COLOR);
                        }
                        mActivity.saveCardExpiryYear(null);
                    } else if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardExpiryMonthView, getResources().getString(R.string.mpsdk_card_expiry_month_hint),
                                    color);
                            mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(), color));
                        } else {
                            setText(mCardExpiryMonthView, getResources().getString(R.string.mpsdk_card_expiry_month_hint),
                                    CardInterface.FULL_TEXT_VIEW_COLOR);
                            mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(),
                                    CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryMonth(null);
                    }
                }
            });
        }
    }


    protected void setCardNumberListener() {
        if (mCardNumberEditText != null) {
            mCardNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mBaseCardholderView.setVisibility(View.INVISIBLE);
                    mCardNumberView.setVisibility(View.VISIBLE);
                    String number = mActivity.buildNumberWithMask(s);
                    mCardNumberView.setText(number);
                }


                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardNumber(s.toString());
                    if (s.length() == 0) {
                        mCardNumberView.setVisibility(View.INVISIBLE);
                        mBaseCardholderView.setVisibility(View.VISIBLE);
                        mActivity.saveCardNumber(null);
                    }
                }
            });
        }
    }

    protected void setCardholderNameListener() {
        if (mCardHolderNameEditText != null) {
            mCardHolderNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    s = s.toString().toUpperCase();
                    if (mActivity.getCurrentPaymentMethod() != null) {
                        int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                        setText(mCardholderNameView, s, color);
                    } else {
                        setText(mCardholderNameView, s, CardInterface.FULL_TEXT_VIEW_COLOR);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardName(s.toString());
                    if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardholderNameView, getResources().getString(R.string.mpsdk_cardholder_name_short),
                                    color);
                        } else {
                            setText(mCardholderNameView, getResources().getString(R.string.mpsdk_cardholder_name_short),
                                    CardInterface.FULL_TEXT_VIEW_COLOR);
                        }
                        mActivity.saveCardName(null);
                    }
                }
            });
        }
    }

    public void transitionColor(int color) {
        setCardColor(color);
        mColorCard.startAnimation(mAnimFadeIn);
        setFontColor();
    }

    public void setFontColor() {
        PaymentMethod currentPaymentMethod = mActivity.getCurrentPaymentMethod();
        int font = 0;
        if (currentPaymentMethod != null) {
            font = mActivity.getCardFontColor(currentPaymentMethod);
        }
        if (font == 0) {
            font = CardInterface.FULL_TEXT_VIEW_COLOR;
        }
        mCardNumberView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardholderNameView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardExpiryMonthView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardExpiryYearView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(), font));
        if (mActivity.getSecurityCodeLocation() != null &&
                mActivity.getSecurityCodeLocation().equals(CardInterface.CARD_SIDE_FRONT)) {
            mCardSecurityCodeView.setTextColor(ContextCompat.getColor(getContext(), font));
        }
    }

    public void setCardColor(int color) {
        if (color == 0) {
            color = CardInterface.NEUTRAL_CARD_COLOR;
        }
        mColorDrawableCard.setColor(ContextCompat.getColor(getContext(), color));
    }

    public void quickTransition(int color) {
        setCardColor(color);
        mColorCard.startAnimation(mQuickAnim);
        setFontColor();
    }

    public void transitionImage(int image) {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mBaseImageCard.setVisibility(View.INVISIBLE);
        mImageCardContainer.setImageResource(image);
        mImageCardContainer.setVisibility(View.VISIBLE);
        if (mAnimate) {
            mImageCardContainer.startAnimation(mAnimFadeIn);
        }
    }

    public void clearImage() {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mImageCardContainer.setVisibility(View.INVISIBLE);
        if (mBaseImageCard.getVisibility() == View.INVISIBLE) {
            mBaseImageCard.setVisibility(View.VISIBLE);
            mBaseImageCard.startAnimation(mAnimFadeIn);
        }
    }

    private void populateCardNumber() {
        String cardNumber = mActivity.getCardNumber();
        if (cardNumber == null) {
            mCardNumberView.setVisibility(View.INVISIBLE);
            mBaseCardholderView.setVisibility(View.VISIBLE);
        } else {
            mBaseCardholderView.setVisibility(View.INVISIBLE);
            mCardNumberView.setVisibility(View.VISIBLE);
            int color = CardInterface.FULL_TEXT_VIEW_COLOR;
            String number = mActivity.buildNumberWithMask(cardNumber);
            setText(mCardNumberView, number, color);
        }
    }

    private void populateCardName() {
        String cardName = mActivity.getCardHolderName();
        if (cardName == null) {
            mCardholderNameView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
        } else {
            mCardholderNameView.setText(cardName.toUpperCase());
        }
        mCardholderNameView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardMonth() {
        String cardMonth = mActivity.getExpiryMonth();
        if (cardMonth == null) {
            mCardExpiryMonthView.setText(getResources()
                    .getString(R.string.mpsdk_card_expiry_month_hint));
        } else {
            mCardExpiryMonthView.setText(cardMonth);
        }
        mCardExpiryMonthView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
        mCardDateDividerView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardYear() {
        String cardYear = mActivity.getExpiryYear();
        if (cardYear == null) {
            mCardExpiryYearView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        } else {
            mCardExpiryYearView.setText(cardYear);
        }
        mCardExpiryYearView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardImage() {
        PaymentMethod currentPM = mActivity.getCurrentPaymentMethod();
        if (currentPM == null) {
            clearImage();
        } else {
            int image = mActivity.getCardImage(currentPM);
            transitionImage(image);
        }
    }
    
    private void populateCardColor() {
        if (mActivity.getCurrentPaymentMethod() != null) {
            int color = mActivity.getCardColor(mActivity.getCurrentPaymentMethod());
            quickTransition(color);
        } else {
            quickTransition(CardInterface.NEUTRAL_CARD_COLOR);
        }
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
        textView.setText(text);
    }

}
