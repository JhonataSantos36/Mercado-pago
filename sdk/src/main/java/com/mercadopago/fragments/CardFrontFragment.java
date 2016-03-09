package com.mercadopago.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import com.mercadopago.NewFormActivity;
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
    private Animation mAnimFadeOut;
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
    }

    public void setCardInputViews() {
        mCardNumberEditText = (MPEditText) getActivity().findViewById(R.id.cardNumber);
        mCardHolderNameEditText = (MPEditText) getActivity().findViewById(R.id.cardholderName);
        mCardExpiryDateEditText = (MPEditText) getActivity().findViewById(R.id.cardExpiryDate);
        mCardSecurityEditText = (MPEditText) getActivity().findViewById(R.id.cardSecurityCode);
        mBaseCardholderView = (LinearLayout) getActivity().findViewById(R.id.baseCardholderContainer);
        mAnimFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        mAnimFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        if (getView() != null) {
            mCardNumberView = (MPTextView) getView().findViewById(R.id.cardNumberView);
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
                    if (start < 2) {

                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryMonthView.setText(s);
                            mCardExpiryMonthView.setTextColor(getResources().getColor(color));
                            mCardDateDividerView.setTextColor(getResources().getColor(color));
                        } else {
                            mCardExpiryMonthView.setText(s);
                            mCardExpiryMonthView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                            mCardDateDividerView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                        }

                        mActivity.saveCardExpiryMonth(s.toString());
                    } else {
                        CharSequence year = s.subSequence(2, s.length());
                        mCardExpiryYearView.setText(year);

                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryYearView.setTextColor(getResources().getColor(color));
                        } else {
                            mCardExpiryYearView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryYear(year.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 2) {

                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryYearView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
                            mCardExpiryYearView.setTextColor(getResources().getColor(color));
                        } else {
                            mCardExpiryYearView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
                            mCardExpiryYearView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardExpiryYear(null);
                    } else if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardExpiryMonthView.setText(getResources().getString(R.string.mpsdk_card_expiry_month_hint));
                            mCardExpiryMonthView.setTextColor(getResources().getColor(color));
                            mCardDateDividerView.setTextColor(getResources().getColor(color));
                        } else {
                            mCardExpiryMonthView.setText(getResources().getString(R.string.mpsdk_card_expiry_month_hint));
                            mCardExpiryMonthView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                            mCardDateDividerView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
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
                        mCardholderNameView.setText(s);
                        mCardholderNameView.setTextColor(getResources().getColor(color));
                    } else {
                        mCardholderNameView.setText(s);
                        mCardholderNameView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardName(s.toString());
                    if (s.length() == 0) {
                        if (mActivity.getCurrentPaymentMethod() != null) {
                            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
                            mCardholderNameView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
                            mCardholderNameView.setTextColor(getResources().getColor(color));
                        } else {
                            mCardholderNameView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
                            mCardholderNameView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
                        }
                        mActivity.saveCardName(null);
                    }
                }
            });
        }
    }

    public void transitionColor(int color, int font) {
        setCardColor(CardInterface.NEUTRAL_CARD_COLOR);
        setCardColor(color);
        mColorCard.startAnimation(mAnimFadeIn);
        mCardNumberView.setTextColor(getResources().getColor(font));
        mCardholderNameView.setTextColor(getResources().getColor(font));
        mCardExpiryMonthView.setTextColor(getResources().getColor(font));
        mCardExpiryYearView.setTextColor(getResources().getColor(font));
        mCardDateDividerView.setTextColor(getResources().getColor(font));
        if (mActivity.getSecurityCodeLocation() != null &&
                mActivity.getSecurityCodeLocation().equals(CardInterface.CARD_SIDE_FRONT)) {
            mCardSecurityCodeView.setTextColor(getResources().getColor(font));
        }
    }

    public void setCardColor(int color) {
        if (color == 0) {
            color = CardInterface.NEUTRAL_CARD_COLOR;
        }
        mColorDrawableCard.setColor(getResources().getColor(color));
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
//            String state = mActivity.getCardNumberState();
            int color = CardInterface.FULL_TEXT_VIEW_COLOR;
            String number = mActivity.buildNumberWithMask(cardNumber);
            setText(mCardNumberView, number, color);
        }
    }

    private void populateCardName() {
        String cardName = mActivity.getCardHolderName();
//        String state = mActivity.getCardHolderNameState();
        if (cardName == null) {
            mCardholderNameView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
        } else {
            mCardholderNameView.setText(cardName.toUpperCase());
        }
        mCardholderNameView.setTextColor(getResources()
                        .getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardMonth() {
//        String dateState = mActivity.getExpiryDateState();
        String cardMonth = mActivity.getExpiryMonth();
        if (cardMonth == null) {
            mCardExpiryMonthView.setText(getResources()
                    .getString(R.string.mpsdk_card_expiry_month_hint));
        } else {
            mCardExpiryMonthView.setText(cardMonth);
        }
        mCardExpiryMonthView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
        mCardDateDividerView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
    }

    private void populateCardYear() {
//        String dateState = mActivity.getExpiryDateState();
        String cardYear = mActivity.getExpiryYear();
        if (cardYear == null) {
            mCardExpiryYearView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        } else {
            mCardExpiryYearView.setText(cardYear);
        }
        mCardExpiryYearView.setTextColor(getResources()
                .getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
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
        PaymentMethod currentPM = mActivity.getCurrentPaymentMethod();
        int color = 0;
        int font = CardInterface.FULL_TEXT_VIEW_COLOR;
        if (currentPM != null) {
            color = mActivity.getCardColor(currentPM);
            font = mActivity.getCardFontColor(currentPM);
        }
        transitionColor(color, font);
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(getResources().getColor(color));
        textView.setText(text);
    }

//    public void setCardDateDividerColor(int color) {
//        mCardDateDividerView.setTextColor(getResources().getColor(color));
//    }

    public void setCardNumberErrorView() {
        mCardNumberView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardNumberErrorView() {
        if (mActivity.getCurrentPaymentMethod() != null) {
            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
            mCardNumberView.setTextColor(getResources().getColor(color));
        } else {
            mCardNumberView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
        }
    }

    public void setCardNameErrorView() {
        mCardholderNameView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardNameErrorView() {
        if (mActivity.getCurrentPaymentMethod() != null) {
            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
            mCardholderNameView.setTextColor(getResources().getColor(color));
        } else {
            mCardholderNameView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
        }
    }

    public void setCardExpiryDateErrorView() {
        mCardExpiryMonthView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
        mCardExpiryYearView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
        mCardDateDividerView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardDateErrorView() {
        if (mActivity.getCurrentPaymentMethod() != null) {
            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
            mCardExpiryMonthView.setTextColor(getResources().getColor(color));
            mCardExpiryYearView.setTextColor(getResources().getColor(color));
            mCardDateDividerView.setTextColor(getResources().getColor(color));
        } else {
            mCardExpiryMonthView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
            mCardExpiryYearView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
            mCardDateDividerView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
        }
    }

    public void setCardSecurityCodeErrorView() {
        mCardSecurityCodeView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardSecurityCodeErrorView() {
        if (mActivity.getCurrentPaymentMethod() != null) {
            int color = mActivity.getCardFontColor(mActivity.getCurrentPaymentMethod());
            mCardSecurityCodeView.setTextColor(getResources().getColor(color));
        } else {
            mCardSecurityCodeView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
        }
    }

}
