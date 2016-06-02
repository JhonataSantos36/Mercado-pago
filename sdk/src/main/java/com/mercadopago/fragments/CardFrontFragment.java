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

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class CardFrontFragment extends android.support.v4.app.Fragment {

    //Card input views
    private MPTextView mCardNumberTextView;
    private MPTextView mCardholderNameTextView;
    private MPTextView mCardExpiryMonthTextView;
    private MPTextView mCardExpiryYearTextView;
    private MPTextView mCardDateDividerTextView;
    private MPTextView mCardSecurityCodeTextView;
    private FrameLayout mCardSecurityClickableZone;
    private FrameLayout mBaseCard;
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

    public static String BASE_NUMBER_CARDHOLDER = "···· ···· ···· ····";
    public static String BASE_FRONT_SECURITY_CODE = "····";

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
        mAnimFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        mQuickAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.quick_anim);
        if (getView() != null) {
            mCardNumberTextView = (MPTextView) getView().findViewById(R.id.cardNumberTextView);
            mCardholderNameTextView = (MPTextView) getView().findViewById(R.id.cardholderNameView);
            mCardExpiryMonthTextView = (MPTextView) getView().findViewById(R.id.cardHolderExpiryMonth);
            mCardExpiryYearTextView = (MPTextView) getView().findViewById(R.id.cardHolderExpiryYear);
            mCardDateDividerTextView = (MPTextView) getView().findViewById(R.id.cardHolderDateDivider);
            mCardSecurityCodeTextView = (MPTextView) getView().findViewById(R.id.cardSecurityView);
            mCardSecurityClickableZone = (FrameLayout) getView().findViewById(R.id.cardSecurityClickableZone);
            mBaseCard = (FrameLayout) getView().findViewById(R.id.activity_new_card_form_basecolor_front);
            mColorCard = (FrameLayout) getView().findViewById(R.id.activity_new_card_form_color_front);
            mColorDrawableCard = (GradientDrawable) mColorCard.getBackground();
            mBaseImageCard = (FrameLayout) getView().findViewById(R.id.baseImageCard);
            mImageCardContainer = (ImageView) getView().findViewById(R.id.imageCardContainer);
        }
    }

    public void populateViews() {
        populateCardNumber(mActivity.getCardNumber());
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

    public void onSecurityTextChanged(CharSequence s) {
        mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s.toString()));
    }

    public void afterSecurityTextChanged(Editable s) {
        mActivity.saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s.toString()));
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
            mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), securityCode));
        } else {
            mBaseCard.setVisibility(View.INVISIBLE);
            setText(mCardSecurityCodeTextView, buildSecurityCode(mActivity.getSecurityCodeLength(), securityCode),
                    CardInterface.FULL_TEXT_VIEW_COLOR);
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
                            setText(mCardExpiryMonthTextView, month, color);
                            mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(),
                                    color));
                        } else {
                            setText(mCardExpiryMonthTextView, month, CardInterface.FULL_TEXT_VIEW_COLOR);
                            mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(),
                                    CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryMonth(month.toString());
                    } else {
                        CharSequence year = s.subSequence(3, s.length());
                        mCardExpiryYearTextView.setText(year);

                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryYearTextView.setTextColor(ContextCompat.getColor(getContext(), color));
                        } else {
                            mCardExpiryYearTextView.setTextColor(ContextCompat.getColor(getContext(),
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
                            setText(mCardExpiryYearTextView, getResources().getString(R.string.mpsdk_card_expiry_year_hint),
                                    color);
                        } else {
                            setText(mCardExpiryYearTextView, getResources().getString(R.string.mpsdk_card_expiry_year_hint),
                                    CardInterface.FULL_TEXT_VIEW_COLOR);
                        }
                        mActivity.saveCardExpiryYear(null);
                    } else if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardExpiryMonthTextView, getResources().getString(R.string.mpsdk_card_expiry_month_hint),
                                    color);
                            mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(), color));
                        } else {
                            setText(mCardExpiryMonthTextView, getResources().getString(R.string.mpsdk_card_expiry_month_hint),
                                    CardInterface.FULL_TEXT_VIEW_COLOR);
                            mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(),
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
                    populateCardNumber(s);
                }


                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardNumber(s.toString());
                    if (s.length() == 0) {
                        setText(mCardNumberTextView, BASE_NUMBER_CARDHOLDER, CardInterface.FULL_TEXT_VIEW_COLOR);
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
                        setText(mCardholderNameTextView, s, color);
                    } else {
                        setText(mCardholderNameTextView, s, CardInterface.FULL_TEXT_VIEW_COLOR);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardName(s.toString());
                    if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            setText(mCardholderNameTextView, getResources().getString(R.string.mpsdk_cardholder_name_short),
                                    color);
                        } else {
                            setText(mCardholderNameTextView, getResources().getString(R.string.mpsdk_cardholder_name_short),
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
        mCardNumberTextView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardholderNameTextView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardExpiryMonthTextView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardExpiryYearTextView.setTextColor(ContextCompat.getColor(getContext(), font));
        mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(), font));
        if (mActivity.getSecurityCodeLocation() != null &&
                mActivity.getSecurityCodeLocation().equals(CardInterface.CARD_SIDE_FRONT)) {
            mCardSecurityCodeTextView.setTextColor(ContextCompat.getColor(getContext(), font));
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

    public void populateCardNumber(CharSequence s) {
        String number;
        if (s == null || s.length() == 0) {
            number = BASE_NUMBER_CARDHOLDER;
        } else if ((s.length() < MercadoPago.BIN_LENGTH)
                || (s.length() == MercadoPago.BIN_LENGTH && mActivity.getCurrentPaymentMethod() == null)
                || (mActivity.getCurrentPaymentMethod() == null) ) {
            number = mActivity.buildNumberWithMask(CardInterface.CARD_NUMBER_MAX_LENGTH, s.toString());
        } else {
            int length = mActivity.getCardNumberLength();
            number = mActivity.buildNumberWithMask(length, s.toString());
        }
        mCardNumberTextView.setText(number);
    }

    private void populateCardName() {
        String cardName = mActivity.getCardHolderName();
        if (cardName == null) {
            mCardholderNameTextView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
        } else {
            mCardholderNameTextView.setText(cardName.toUpperCase());
        }
        mCardholderNameTextView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardMonth() {
        String cardMonth = mActivity.getExpiryMonth();
        if (cardMonth == null) {
            mCardExpiryMonthTextView.setText(getResources()
                    .getString(R.string.mpsdk_card_expiry_month_hint));
        } else {
            mCardExpiryMonthTextView.setText(cardMonth);
        }
        mCardExpiryMonthTextView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
        mCardDateDividerTextView.setTextColor(ContextCompat.getColor(getContext(),
                CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardYear() {
        String cardYear = mActivity.getExpiryYear();
        if (cardYear == null) {
            mCardExpiryYearTextView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        } else {
            mCardExpiryYearTextView.setText(cardYear);
        }
        mCardExpiryYearTextView.setTextColor(ContextCompat.getColor(getContext(),
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

    public String buildSecurityCode(int cardLength, String s) {
        StringBuffer sb = new StringBuffer();
        if (s == null || s.length() == 0) {
            return BASE_FRONT_SECURITY_CODE;
        }
        for (int i = 0; i < cardLength ; i++) {
            char c = getCharOfCard(s, i);
            sb.append(c);
        }
        return sb.toString();
    }

    private char getCharOfCard(String s, int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return "·".charAt(0);
        }
    }

}
