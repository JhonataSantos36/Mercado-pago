package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPAutoResizeTextView;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MPAnimationUtils;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.util.MPCardUIUtils;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.ScaleUtil;

/**
 * Created by vaserber on 9/29/16.
 */

public class FrontCardView {

    public static final String BASE_NUMBER_CARDHOLDER = "•••• •••• •••• ••••";
    public static final String BASE_FRONT_SECURITY_CODE = "••••";

    public static final int CARD_NUMBER_MAX_LENGTH = 16;
    public static final int CARD_SECURITY_CODE_DEFAULT_LENGTH = 4;

    public static final int CARD_DEFAULT_AMOUNT_SPACES = 3;
    public static final int CARD_AMEX_DINERS_AMOUNT_SPACES = 2;
    public static final int CARD_NUMBER_MAESTRO_SETTING_2_AMOUNT_SPACES = 1;

    public static final int CARD_NUMBER_AMEX_LENGTH = 15;
    public static final int CARD_NUMBER_DINERS_LENGTH = 14;
    public static final int CARD_NUMBER_MAESTRO_SETTING_1_LENGTH = 18;
    public static final int CARD_NUMBER_MAESTRO_SETTING_2_LENGTH = 19;

    public static final int EDITING_TEXT_VIEW_ALPHA = 255;


    private Context mContext;
    private View mView;
    private String mMode;
    private String mSize;

    //Card info
    private PaymentMethod mPaymentMethod;
    private int mCardNumberLength;
    private int mSecurityCodeLength;
    private boolean mShowSecurityCode;
    private String mLastFourDigits;

    //View controls
    private FrameLayout mCardContainer;
    private ImageView mCardBorder;
    private MPAutoResizeTextView mCardNumberTextView;
    private MPTextView mCardholderNameTextView;
    private MPTextView mCardExpiryMonthTextView;
    private MPTextView mCardExpiryYearTextView;
    private MPTextView mCardDateDividerTextView;
    private MPTextView mCardSecurityCodeTextView;
    private FrameLayout mBaseImageCard;
    private ImageView mImageCardContainer;
    private ImageView mCardLowApiImageView;
    private ImageView mCardLollipopImageView;
    private Animation mAnimFadeIn;

    public FrontCardView(Context context, String mode) {
        this.mContext = context;
        this.mMode = mode;
        this.mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        this.mSecurityCodeLength = CARD_SECURITY_CODE_DEFAULT_LENGTH;
        this.mShowSecurityCode = false;
    }


    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public void setCardNumberLength(int cardNumberLength) {
        this.mCardNumberLength = cardNumberLength;
    }

    public void setSecurityCodeLength(int securityCodeLength) {
        this.mSecurityCodeLength = securityCodeLength;
    }

    public void hasToShowSecurityCode(boolean show) {
        this.mShowSecurityCode = show;
        if (show) {
            showEmptySecurityCode();
        } else {
            hideSecurityCode();
        }
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.mLastFourDigits = lastFourDigits;
    }

    public void initializeControls() {
        mCardContainer = (FrameLayout) mView.findViewById(R.id.mpsdkCardFrontContainer);
        mCardBorder = (ImageView) mView.findViewById(R.id.mpsdkCardShadowBorder);
        mAnimFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.mpsdk_fade_in);
        mCardNumberTextView = (MPAutoResizeTextView) mView.findViewById(R.id.mpsdkCardNumberTextView);
        mCardholderNameTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardholderNameView);
        mCardExpiryMonthTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderExpiryMonth);
        mCardExpiryYearTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderExpiryYear);
        mCardDateDividerTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderDateDivider);
        mCardSecurityCodeTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardSecurityCodeViewFront);
        mBaseImageCard = (FrameLayout) mView.findViewById(R.id.mpsdkBaseImageCard);
        mImageCardContainer = (ImageView) mView.findViewById(R.id.mpsdkImageCardContainer);
        mCardLowApiImageView = (ImageView) mView.findViewById(R.id.mpsdkCardLowApiImageView);
        mCardLollipopImageView = (ImageView) mView.findViewById(R.id.mpsdkCardLollipopImageView);

        if (mSize != null) {
            resize();
        }

    }

    public void hide() {
        mCardContainer.setVisibility(View.GONE);
    }

    public void show() {
        mCardContainer.setVisibility(View.VISIBLE);
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_card_front, parent, attachToRoot);
        return mView;
    }

    public View getView() {
        return mView;
    }

    public void decorateCardBorder(int borderColor) {
        GradientDrawable cardShadowRounded = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.mpsdk_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, mContext), borderColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
    }

    public void draw() {
        if (mMode == null) {
            mMode = CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY;
        }
        if (mMode.equals(CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY)) {
            drawEmptyCard();
        } else if (mMode.equals(CardRepresentationModes.SHOW_FULL_FRONT_ONLY)) {
            drawFullCard();
        } else if (mMode.equals(CardRepresentationModes.EDIT_FRONT)) {
            drawEmptyCard();
        }
    }

    public void drawEditingCard(String cardNumber, String cardholderName, String expiryMonth,
                                String expiryYear, String securityCode) {
        onPaymentMethodSet();
        drawEditingCardNumber(cardNumber);
        drawEditingCardHolderName(cardholderName);
        if (securityCode != null) {
            drawEditingSecurityCode(securityCode);
        }
        drawEditingExpiryMonth(expiryMonth);
        drawEditingExpiryYear(expiryYear);
    }

    public void drawEditingCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() == 0) {
            mCardNumberTextView.setText(BASE_NUMBER_CARDHOLDER);
        } else if (cardNumber.length() < MercadoPagoUtil.BIN_LENGTH || mPaymentMethod == null) {
            mCardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(CARD_NUMBER_MAX_LENGTH, cardNumber));
        } else {
            mCardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(mCardNumberLength, cardNumber));
        }
        enableEditingFontColor(mCardNumberTextView);
        disableEditingFontColor(mCardholderNameTextView);
        disableEditingFontColor(mCardExpiryMonthTextView);
        disableEditingFontColor(mCardExpiryYearTextView);
        disableEditingFontColor(mCardDateDividerTextView);
        disableEditingFontColor(mCardSecurityCodeTextView);
    }

    public void updateCardNumberMask(String cardNumber) {
        mCardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(mCardNumberLength, cardNumber));
    }

    public void drawEditingCardHolderName(String cardholderName) {
        if (cardholderName == null || cardholderName.length() == 0) {
            mCardholderNameTextView.setText(mContext.getResources().getString(R.string.mpsdk_cardholder_name_short));
        } else {
            mCardholderNameTextView.setText(cardholderName.toUpperCase());
        }
        enableEditingFontColor(mCardholderNameTextView);
        disableEditingFontColor(mCardNumberTextView);
        disableEditingFontColor(mCardExpiryMonthTextView);
        disableEditingFontColor(mCardExpiryYearTextView);
        disableEditingFontColor(mCardDateDividerTextView);
        disableEditingFontColor(mCardSecurityCodeTextView);
    }

    public void drawEditingExpiryMonth(String cardMonth) {
        if (cardMonth == null || cardMonth.length() == 0) {
            mCardExpiryMonthTextView.setText(mContext.getResources()
                    .getString(R.string.mpsdk_card_expiry_month_hint));
        } else {
            mCardExpiryMonthTextView.setText(cardMonth);
        }
        enableEditingFontColor(mCardExpiryMonthTextView);
        enableEditingFontColor(mCardExpiryYearTextView);
        enableEditingFontColor(mCardDateDividerTextView);
        disableEditingFontColor(mCardholderNameTextView);
        disableEditingFontColor(mCardNumberTextView);
        disableEditingFontColor(mCardSecurityCodeTextView);
    }

    public void drawEditingExpiryYear(String cardYear) {
        if (cardYear == null || cardYear.length() == 0) {
            mCardExpiryYearTextView.setText(mContext.getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        } else {
            mCardExpiryYearTextView.setText(cardYear);
        }
        enableEditingFontColor(mCardExpiryMonthTextView);
        enableEditingFontColor(mCardExpiryYearTextView);
        enableEditingFontColor(mCardDateDividerTextView);
        disableEditingFontColor(mCardholderNameTextView);
        disableEditingFontColor(mCardNumberTextView);
        disableEditingFontColor(mCardSecurityCodeTextView);
    }

    public void drawEditingSecurityCode(String securityCode) {
        if (securityCode == null || securityCode.length() == 0) {
            mCardSecurityCodeTextView.setText(BASE_FRONT_SECURITY_CODE);
        } else {
            mCardSecurityCodeTextView.setText(MPCardMaskUtil.buildSecurityCode(mSecurityCodeLength, securityCode));
        }
        enableEditingFontColor(mCardSecurityCodeTextView);
        disableEditingFontColor(mCardNumberTextView);
        disableEditingFontColor(mCardholderNameTextView);
        disableEditingFontColor(mCardExpiryMonthTextView);
        disableEditingFontColor(mCardExpiryYearTextView);
        disableEditingFontColor(mCardDateDividerTextView);
    }

    public void transitionPaymentMethodSet() {
        fadeInColor(MPCardUIUtils.getCardColor(mPaymentMethod, mContext));
        int fontColor = MPCardUIUtils.getCardFontColor(mPaymentMethod, mContext);
        setFontColor(fontColor, mCardNumberTextView);
        setFontColor(fontColor, mCardholderNameTextView);
        setFontColor(fontColor, mCardExpiryMonthTextView);
        setFontColor(fontColor, mCardDateDividerTextView);
        setFontColor(fontColor, mCardExpiryYearTextView);
        setFontColor(fontColor, mCardSecurityCodeTextView);
        enableEditingFontColor(mCardNumberTextView);
        transitionImage(getCardImage(mPaymentMethod), true);
    }

    public void fillCardHolderName(String cardholderName) {
        if (cardholderName == null || cardholderName.length() == 0) {
            mCardholderNameTextView.setText(mContext.getResources().getString(R.string.mpsdk_cardholder_name_short));
        } else {
            mCardholderNameTextView.setText(cardholderName.toUpperCase());
        }
    }

    public void transitionClearPaymentMethod() {
        mPaymentMethod = null;
        fadeOutColor(MPCardUIUtils.NEUTRAL_CARD_COLOR);
        clearCardImage();
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardNumberTextView);
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardholderNameTextView);
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardExpiryMonthTextView);
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardDateDividerTextView);
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardExpiryYearTextView);
        setFontColor(MPCardUIUtils.FULL_TEXT_VIEW_COLOR, mCardSecurityCodeTextView);
        enableEditingFontColor(mCardNumberTextView);
        mCardSecurityCodeTextView.setText("");
    }

    private void drawEmptyCard() {
        String number = BASE_NUMBER_CARDHOLDER;
        mCardNumberTextView.setText(number);
        mCardholderNameTextView.setText(mContext.getResources().getString(R.string.mpsdk_cardholder_name_short));
        mCardExpiryMonthTextView.setText(mContext.getResources().getString(R.string.mpsdk_card_expiry_month_hint));
        mCardExpiryYearTextView.setText(mContext.getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        mCardSecurityCodeTextView.setText("");
        clearImage();
    }

    private void clearImage() {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mImageCardContainer.setVisibility(View.INVISIBLE);
        if (mBaseImageCard.getVisibility() == View.INVISIBLE) {
            mBaseImageCard.setVisibility(View.VISIBLE);
            mBaseImageCard.startAnimation(mAnimFadeIn);
        }
    }

    public void drawFullCard() {
        if (mLastFourDigits == null || mPaymentMethod == null) return;
        mCardNumberTextView.setText(MPCardMaskUtil.getCardNumberHidden(mCardNumberLength, mLastFourDigits));
        mCardholderNameTextView.setVisibility(View.GONE);
        mCardExpiryMonthTextView.setVisibility(View.GONE);
        mCardDateDividerTextView.setVisibility(View.GONE);
        mCardExpiryYearTextView.setVisibility(View.GONE);
        onPaymentMethodSet();
    }

    private void setCardColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColorLollipop(mCardLollipopImageView, mContext, color);
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColor(mCardLowApiImageView, mContext, color);
        }
    }

    private void setCardImage(int image) {
        transitionImage(image, false);
    }

    private void setFontColor(int color, TextView textView) {
        textView.setTextColor(ContextCompat.getColor(mContext, color));
    }

    private void transitionImage(int image, boolean animate) {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mBaseImageCard.setVisibility(View.INVISIBLE);
        mImageCardContainer.setImageResource(image);
        mImageCardContainer.setVisibility(View.VISIBLE);
        if (animate) {
            mImageCardContainer.startAnimation(mAnimFadeIn);
        }
    }

    private int getCardImage(PaymentMethod paymentMethod) {
        String imageName = "mpsdk_ico_card_" + paymentMethod.getId().toLowerCase();
        return mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
    }



    private void resize() {
        if (mSize == null) return;
        if (mSize.equals(CardRepresentationModes.MEDIUM_SIZE)) {
            resizeCard(mCardContainer, R.dimen.mpsdk_card_size_medium_height, R.dimen.mpsdk_card_size_medium_width,
                    CardRepresentationModes.CARD_NUMBER_SIZE_MEDIUM, CardRepresentationModes.CARD_HOLDER_NAME_SIZE_MEDIUM,
                    CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_MEDIUM, CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_MEDIUM);
        } else if (mSize.equals(CardRepresentationModes.BIG_SIZE)) {
            resizeCard(mCardContainer, R.dimen.mpsdk_card_size_big_height, R.dimen.mpsdk_card_size_big_width,
                    CardRepresentationModes.CARD_NUMBER_SIZE_BIG, CardRepresentationModes.CARD_HOLDER_NAME_SIZE_BIG,
                    CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_BIG, CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_BIG);
        } else if (mSize.equals(CardRepresentationModes.EXTRA_BIG_SIZE)) {
            resizeCard(mCardContainer, R.dimen.mpsdk_card_size_extra_big_height, R.dimen.mpsdk_card_size_extra_big_width,
                    CardRepresentationModes.CARD_NUMBER_SIZE_EXTRA_BIG, CardRepresentationModes.CARD_HOLDER_NAME_SIZE_EXTRA_BIG,
                    CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_EXTRA_BIG, CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_EXTRA_BIG);
        }
    }

    private void resizeCard(ViewGroup cardViewContainer, int cardHeight, int cardWidth, int cardNumberFontSize,
                            int cardHolderNameFontSize, int cardExpiryDateSize, int cardSecurityCodeSize) {
        LayoutUtil.resizeViewGroupLayoutParams(cardViewContainer, cardHeight, cardWidth, mContext);

        if(cardNumberFontSize == CardRepresentationModes.CARD_NUMBER_SIZE_MEDIUM){
            int minTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.mpsdk_smallest_text);
            mCardNumberTextView.setMinTextSize(minTextSize);
        }

        mCardNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardNumberFontSize);
        mCardholderNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardHolderNameFontSize);
        mCardExpiryMonthTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardExpiryDateSize);
        mCardDateDividerTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardExpiryDateSize);
        mCardExpiryYearTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardExpiryDateSize);
        mCardSecurityCodeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardSecurityCodeSize);
    }

    private void showEmptySecurityCode() {
        mCardSecurityCodeTextView.setText(BASE_FRONT_SECURITY_CODE);
    }

    private void hideSecurityCode() {
        mCardSecurityCodeTextView.setText("");
    }

    public void enableEditingCardNumber() {
        enableEditingFontColor(mCardNumberTextView);
    }

    private void enableEditingFontColor(TextView textView) {
        int alpha = EDITING_TEXT_VIEW_ALPHA;
        int fontColor = MPCardUIUtils.getCardFontColor(mPaymentMethod, mContext);
        int color = ContextCompat.getColor(mContext, fontColor);
        int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        textView.setTextColor(newColor);
    }

    private void disableEditingFontColor(TextView textView) {
        int fontColor = MPCardUIUtils.getCardFontColor(mPaymentMethod, mContext);
        setFontColor(fontColor, textView);
    }

    private void onPaymentMethodSet() {
        if (mPaymentMethod == null) return;
        setCardColor(MPCardUIUtils.getCardColor(mPaymentMethod, mContext));
        setCardImage(getCardImage(mPaymentMethod));
        int fontColor = MPCardUIUtils.getCardFontColor(mPaymentMethod, mContext);
        setFontColor(fontColor, mCardNumberTextView);
    }

    private void fadeInColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeInLollipop(color, mCardLollipopImageView, mContext);
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeIn(color, mCardLowApiImageView, mContext);
        }
    }

    private void fadeOutColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeOutLollipop(color, mCardLollipopImageView, mContext);
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.fadeOut(color, mCardLowApiImageView, mContext);
        }
    }

    private void clearCardImage() {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mImageCardContainer.setVisibility(View.INVISIBLE);
        if (mBaseImageCard.getVisibility() == View.INVISIBLE) {
            mBaseImageCard.setVisibility(View.VISIBLE);
            mBaseImageCard.startAnimation(mAnimFadeIn);
        }
    }

}
