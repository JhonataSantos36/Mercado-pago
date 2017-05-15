package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.constants.Sites;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ReviewUtil;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOffView extends Reviewable {

    protected View mView;
    protected ImageView mPaymentImage;
    protected MPTextView mPaymentText;
    protected MPTextView mPaymentDescription;
    protected FrameLayout mChangePaymentButton;
    protected FrameLayout mPayerCostContainer;
    protected ImageView mIconTimeImageView;
    protected MPTextView mChangePaymentTextView;

    private Context mContext;
    private PaymentMethod mPaymentMethod;
    private String mExtraInfo;
    private BigDecimal mAmount;
    private Site mSite;
    private OnReviewChange mOnReviewChange;
    private Boolean mIsUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;

    public ReviewPaymentOffView(Context context, PaymentMethod paymentMethod, String extraInfo, BigDecimal amount, Site site, OnReviewChange onReviewChange,
                                Boolean uniquePaymentMethod,
                                DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethod = paymentMethod;
        this.mExtraInfo = extraInfo;
        this.mAmount = amount;
        this.mSite = site;
        this.mOnReviewChange = onReviewChange;
        this.mIsUniquePaymentMethod = uniquePaymentMethod == null ? false : mIsUniquePaymentMethod;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_adapter_review_payment, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mPaymentImage = (ImageView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentImage);
        mPaymentText = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentText);
        mPaymentDescription = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentDescription);
        mChangePaymentButton = (FrameLayout) mView.findViewById(R.id.mpsdkAdapterReviewPaymentChangeButton);
        mPayerCostContainer = (FrameLayout) mView.findViewById(R.id.mpsdkAdapterReviewPayerCostContainer);
        mIconTimeImageView = (ImageView) mView.findViewById(R.id.mpsdkIconTime);
        mChangePaymentTextView = (MPTextView) mView.findViewById(R.id.mpsdkReviewChangePaymentText);
        mChangePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnReviewChange.onChangeSelected();
            }
        });
        if (mIsUniquePaymentMethod) {
            mChangePaymentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void draw() {
        setSmallTextSize();
        decorateText();
        setIcon();

        mPayerCostContainer.setVisibility(View.GONE);

        int paymentInstructionsTemplate = ReviewUtil.getPaymentInstructionTemplate(mPaymentMethod);

        String originalNumber = CurrenciesUtil.formatNumber(mAmount, mSite.getCurrencyId());
        String itemName;
        itemName = ReviewUtil.getPaymentMethodDescription(mPaymentMethod, mContext);
        String completeDescription = mContext.getString(paymentInstructionsTemplate, originalNumber, itemName);

        Spanned amountText = CurrenciesUtil.formatCurrencyInText(mAmount, mSite.getCurrencyId(), completeDescription, false, true);

        mPaymentText.setText(amountText);
        mPaymentDescription.setText(mExtraInfo);
    }

    private void setIcon() {
        int resId = getResource();
        mPaymentImage.setImageResource(resId);

        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mPaymentImage.setColorFilter(mDecorationPreference.getBaseColor(), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private int getResource() {
        int resId;
        boolean isMLB = mSite != null && Sites.BRASIL.getId().equals(mSite.getId());
        boolean isTintNeeded = mDecorationPreference != null && mDecorationPreference.hasColors();

        if (isTintNeeded) {
            if (isMLB) {
                resId = R.drawable.mpsdk_grey_boleto_off;
            } else {
                resId = R.drawable.mpsdk_grey_review_payment_off;
            }
        } else {
            if (isMLB) {
                resId = R.drawable.mpsdk_boleto_off;
            } else {
                resId = R.drawable.mpsdk_review_payment_off;
            }
        }
        return resId;
    }

    private void setSmallTextSize() {
        mPaymentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.mpsdk_payment_text_small_text));
        mPaymentDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.mpsdk_payment_description_small_text));
    }

    private void decorateText() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mChangePaymentTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }

}
