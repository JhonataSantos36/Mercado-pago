package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ReviewUtil;
import com.mercadopago.util.TextUtil;

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
    private String mPaymentMethodCommentInfo;
    private String mPaymentMethodDescriptionInfo;
    private BigDecimal mAmount;
    private Site mSite;
    private OnReviewChange mOnReviewChange;
    private Boolean mEditionEnabled;
    private DecorationPreference mDecorationPreference;
    private ViewGroup mPaymentMethodExtraInfo;

    public ReviewPaymentOffView(Context context, PaymentMethod paymentMethod, String paymentMethodCommentInfo, String paymentMethodDescriptionInfo, BigDecimal amount, Site site, OnReviewChange onReviewChange, Boolean editionEnabled, DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethod = paymentMethod;
        this.mPaymentMethodCommentInfo = paymentMethodCommentInfo;
        this.mPaymentMethodDescriptionInfo = paymentMethodDescriptionInfo;
        this.mAmount = amount;
        this.mSite = site;
        this.mOnReviewChange = onReviewChange;
        this.mEditionEnabled = editionEnabled == null ? true : editionEnabled;
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
        mPaymentMethodExtraInfo = (ViewGroup) mView.findViewById(R.id.mpsdkExtraInfoContainer);
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

        if (mEditionEnabled) {
            mChangePaymentButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void draw() {
        setSmallTextSize();
        decorateText();
        setIcon();

        mPayerCostContainer.setVisibility(View.GONE);

        mPaymentText.setText(getPaymentMethodDescription());

        if (TextUtil.isEmpty(mPaymentMethodCommentInfo)) {
            mPaymentMethodExtraInfo.setVisibility(View.GONE);
        } else {
            mPaymentDescription.setText(mPaymentMethodCommentInfo);
        }
    }

    private CharSequence getPaymentMethodDescription() {
        CharSequence description;
        if (PaymentMethods.ACCOUNT_MONEY.equals(mPaymentMethod.getId())) {
            description = mContext.getString(R.string.mpsdk_ryc_account_money_description);
        } else {
            int paymentInstructionsTemplate = ReviewUtil.getPaymentInstructionTemplate(mPaymentMethod);
            String originalNumber = CurrenciesUtil.formatNumber(mAmount, mSite.getCurrencyId());
            String itemName;
            itemName = ReviewUtil.getPaymentMethodDescription(mPaymentMethod, mPaymentMethodDescriptionInfo, mContext);
            String completeDescription = mContext.getString(paymentInstructionsTemplate, originalNumber, itemName);
            description = CurrenciesUtil.formatCurrencyInText(mAmount, mSite.getCurrencyId(), completeDescription, false, true);
        }
        return description;
    }

    private void setIcon() {
        int resId = getResource();
        mPaymentImage.setImageResource(resId);

        if (mDecorationPreference != null
                && mDecorationPreference.hasColors()
                && !PaymentTypes.PLUGIN.equals(mPaymentMethod.getPaymentTypeId())) {
            mPaymentImage.setColorFilter(mDecorationPreference.getBaseColor(), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private int getResource() {
        int resId;
        boolean isTintNeeded = mDecorationPreference != null && mDecorationPreference.hasColors();

        if (mPaymentMethod != null && PaymentTypes.PLUGIN.equals(mPaymentMethod.getPaymentTypeId())) {
            resId = CheckoutStore.getInstance().getSelectedPaymentMethod().icon;
        } else if (mPaymentMethod != null && mPaymentMethod.getId().equals(PaymentMethods.BRASIL.BOLBRADESCO)) {
            if (isTintNeeded) {
                resId = R.drawable.mpsdk_grey_boleto_off;
            } else {
                resId = R.drawable.mpsdk_boleto_off;
            }
        } else {
            if (isTintNeeded) {
                resId = R.drawable.mpsdk_grey_review_payment_off;
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

    @Override
    public String getKey() {
        return ReviewKeys.PAYMENT_METHODS;
    }
}
