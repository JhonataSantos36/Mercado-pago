package com.mercadopago.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MPAnimationUtils;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class CardFrontFragment extends android.support.v4.app.Fragment {

    int EDITING_TEXT_VIEW_ALPHA = 255;
    int NORMAL_TEXT_VIEW_ALPHA = 179;

    //Card input views
    private MPTextView mCardNumberTextView;
    private MPTextView mCardholderNameTextView;
    private MPTextView mCardExpiryMonthTextView;
    private MPTextView mCardExpiryYearTextView;
    private MPTextView mCardDateDividerTextView;
    private MPTextView mCardSecurityCodeTextView;
    private FrameLayout mCardSecurityClickableZone;
    private FrameLayout mBaseImageCard;
    private ImageView mImageCardContainer;
    private ImageView mCardLowApiImageView;
    private ImageView mCardLollipopImageView;

    // Input controls
    protected MPEditText mCardHolderNameEditText;
    protected MPEditText mCardNumberEditText;
    protected MPEditText mCardExpiryDateEditText;
    protected MPEditText mCardSecurityEditText;
    protected ImageView mCardBorder;
    //Local vars
    private Animation mAnimFadeIn;
    private boolean mAnimate;
    private DecorationPreference mDecorationPreference;

    private CardInterface mActivity;

    public static String BASE_NUMBER_CARDHOLDER = "•••• •••• •••• ••••";
    public static String BASE_FRONT_SECURITY_CODE = "••••";

    public CardFrontFragment() {
        this.mAnimate = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_new_card_front, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCardInputViews();
        setEditTextListeners();
        mActivity = (CardInterface) getActivity();
        mActivity.initializeCardByToken();
        decorate();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViews();
        setFontColor();
    }

    public void setCardInputViews() {
        mCardNumberEditText = (MPEditText) getActivity().findViewById(R.id.mpsdkCardNumber);
        mCardHolderNameEditText = (MPEditText) getActivity().findViewById(R.id.mpsdkCardholderName);
        mCardExpiryDateEditText = (MPEditText) getActivity().findViewById(R.id.mpsdkCardExpiryDate);
        mCardSecurityEditText = (MPEditText) getActivity().findViewById(R.id.mpsdkCardSecurityCode);
        mAnimFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.mpsdk_fade_in);
        if (getView() != null) {
            mCardNumberTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardNumberTextView);
            mCardholderNameTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardholderNameView);
            mCardExpiryMonthTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardHolderExpiryMonth);
            mCardExpiryYearTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardHolderExpiryYear);
            mCardDateDividerTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardHolderDateDivider);
            mCardSecurityCodeTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardSecurityView);
            mCardSecurityClickableZone = (FrameLayout) getView().findViewById(R.id.mpsdkCardSecurityClickableZone);
            mBaseImageCard = (FrameLayout) getView().findViewById(R.id.mpsdkBaseImageCard);
            mImageCardContainer = (ImageView) getView().findViewById(R.id.mpsdkImageCardContainer);
            mCardBorder = (ImageView) getView().findViewById(R.id.mpsdkCardShadowBorder);
            mCardLowApiImageView = (ImageView) getView().findViewById(R.id.mpsdkCardLowApiImageView);
            mCardLollipopImageView = (ImageView) getView().findViewById(R.id.mpsdkCardLollipopImageView);
        }
    }

    private void decorate() {
        int cardShadowColor = ContextCompat.getColor(getContext(), R.color.mpsdk_color_new_card_form_background);
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            cardShadowColor = mDecorationPreference.getLighterColor();
        }
        GradientDrawable cardShadowRounded = (GradientDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.mpsdk_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, getActivity()), cardShadowColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
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

    public void hideCardSecurityView() {
        mCardSecurityClickableZone.setVisibility(View.INVISIBLE);
    }

    public void onSecurityTextChanged(CharSequence s) {
        if(mCardSecurityCodeTextView != null) {
            mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s));
        }
    }

    public void afterSecurityTextChanged(Editable s) {
        if(mActivity != null) {
            mActivity.saveCardSecurityCode(s.toString());
            if (s.length() == 0) {
                mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s));
                mActivity.saveCardSecurityCode(null);
            }
        }
    }

    public void setCardSecurityView() {
        if (!mActivity.isSecurityCodeRequired() || mActivity.getSecurityCodeLocation() == null ||
                mActivity.getSecurityCodeLocation().equals(CardInterface.CARD_SIDE_BACK)) {
            return;
        }
        mCardSecurityClickableZone.setVisibility(View.VISIBLE);
        String securityCode = mActivity.getSecurityCode();
        mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), securityCode));
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
                        if (s.length() >= 3) {
                            month = s.subSequence(0, 2);
                        }
                        mCardExpiryMonthTextView.setText(month);
                        mActivity.saveCardExpiryMonth(month.toString());
                    } else {
                        CharSequence year = s.subSequence(3, s.length());
                        mCardExpiryYearTextView.setText(year);
                        mActivity.saveCardExpiryYear(year.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 3) {
                        mCardExpiryYearTextView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
                        mActivity.saveCardExpiryYear(null);
                    } else if (s.length() == 0) {
                        mCardExpiryMonthTextView.setText(getResources().getString(R.string.mpsdk_card_expiry_month_hint));
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
                    String number = s.toString().replaceAll("\\s", "");
                    mActivity.saveCardNumber(number);
                    populateCardNumber(number);
                }


                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        mCardNumberTextView.setText(BASE_NUMBER_CARDHOLDER);
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
                    mCardholderNameTextView.setText(s.toString().toUpperCase());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardName(s.toString());
                    if (s.length() == 0) {
                        mCardholderNameTextView.setText(getResources().getString(R.string.mpsdk_cardholder_name_short));
                        mActivity.saveCardName(null);
                    }
                }
            });
        }
    }

    public void fadeInColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeInLollipop(color, mCardLollipopImageView, getContext());
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeIn(color, mCardLowApiImageView, getContext());
        }
        setFontColor();
    }

    public void fadeOutColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeOutLollipop(color, mCardLollipopImageView, getContext());
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeOut(color, mCardLowApiImageView, getContext());
        }
        setFontColor();
    }

    public void setFontColor() {
        if(mActivity != null) {
            PaymentMethod currentPaymentMethod = mActivity.getCurrentPaymentMethod();
            int font = 0;
            if (currentPaymentMethod != null) {
                font = mActivity.getCardFontColor(currentPaymentMethod);
            }
            if (font == 0) {
                font = CardInterface.FULL_TEXT_VIEW_COLOR;
            }

            setFontColor(font, mCardNumberTextView, mCardNumberEditText);
            setFontColor(font, mCardholderNameTextView, mCardHolderNameEditText);
            setFontColor(font, mCardExpiryMonthTextView, mCardExpiryDateEditText);
            setFontColor(font, mCardExpiryYearTextView, mCardExpiryDateEditText);
            setFontColor(font, mCardDateDividerTextView, mCardExpiryDateEditText);

            String location = mActivity.getSecurityCodeLocation();
            if (location != null && location.equals(CardInterface.CARD_SIDE_FRONT)) {
                setFontColor(font, mCardSecurityCodeTextView, mCardSecurityEditText);
            }
        }
    }

    private void setFontColor(int font, MPTextView textView, MPEditText editText) {
        if(textView != null) {
            int alpha = NORMAL_TEXT_VIEW_ALPHA;
            if (editText != null && editText.hasFocus()) {
                alpha = EDITING_TEXT_VIEW_ALPHA;
            }
            int color = ContextCompat.getColor(getContext(), font);
            int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            textView.setTextColor(newColor);
        }
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
                || (mActivity.getCurrentPaymentMethod() == null)) {
            number = MPCardMaskUtil.buildNumberWithMask(CardInterface.CARD_NUMBER_MAX_LENGTH, s.toString());
        } else {
            int length = mActivity.getCardNumberLength();
            number = MPCardMaskUtil.buildNumberWithMask(length, s.toString());
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
    }

    private void populateCardMonth() {
        String cardMonth = mActivity.getExpiryMonth();
        if (cardMonth == null) {
            mCardExpiryMonthTextView.setText(getResources()
                    .getString(R.string.mpsdk_card_expiry_month_hint));
        } else {
            mCardExpiryMonthTextView.setText(cardMonth);
        }
    }

    private void populateCardYear() {
        String cardYear = mActivity.getExpiryYear();
        if (cardYear == null) {
            mCardExpiryYearTextView.setText(getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        } else {
            mCardExpiryYearTextView.setText(cardYear);
        }
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
        decorate();
        int color = CardInterface.NEUTRAL_CARD_COLOR;
        if (mActivity.getCurrentPaymentMethod() != null) {
            color = mActivity.getCardColor(mActivity.getCurrentPaymentMethod());
        }
        setCardColor(color);
    }

    public void setCardColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColorLollipop(mCardLollipopImageView, getContext(), color);
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColor(mCardLowApiImageView, getContext(), color);
        }
    }

    public String buildSecurityCode(int cardLength, String s) {
        StringBuffer sb = new StringBuffer();
        if (s == null || s.length() == 0) {
            return BASE_FRONT_SECURITY_CODE;
        }
        for (int i = 0; i < cardLength; i++) {
            char c = getCharOfCard(s, i);
            sb.append(c);
        }
        return sb.toString();
    }

    public String buildSecurityCode(int cardLength, CharSequence s) {
        if (s == null) {
            return BASE_FRONT_SECURITY_CODE;
        } else {
            return buildSecurityCode(cardLength, s.toString());
        }
    }

    private char getCharOfCard(String s, int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return "•".charAt(0);
        }
    }

    public void setDecorationPreference(DecorationPreference decorationPreference) {
        mDecorationPreference = decorationPreference;
    }
}
