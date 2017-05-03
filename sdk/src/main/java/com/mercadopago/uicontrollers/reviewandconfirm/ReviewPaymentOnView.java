package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ReviewUtil;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOnView extends Reviewable {

    public static final String CFT = "CFT ";
    protected View mView;
    protected ImageView mPaymentImage;
    protected MPTextView mPaymentText;
    protected MPTextView mPaymentDescription;
    protected MPTextView mChangePaymentTextView;
    protected MPTextView mCFTTextView;
    protected FrameLayout mChangePaymentButton;
    protected FrameLayout mPayerCostContainer;
    protected ImageView mIconTimeImageView;

    protected Context mContext;
    protected PayerCostViewController mPayerCostViewController;

    protected String mCurrency;
    protected PayerCost mPayerCost;
    protected CardInfo mCardInfo;
    protected PaymentMethod mPaymentMethod;
    protected OnReviewChange mCallback;
    protected Boolean mIsUniquePaymentMethod;
    protected DecorationPreference mDecorationPreference;

    public ReviewPaymentOnView(Context context, PaymentMethod paymentMethod, CardInfo cardInfo, PayerCost payerCost,
                               String currencyId, OnReviewChange callback,
                               Boolean uniquePaymentMethod, DecorationPreference decorationPreference) {

        this.mContext = context;
        this.mCallback = callback;
        this.mPaymentMethod = paymentMethod;
        this.mCardInfo = cardInfo;
        this.mPayerCost = payerCost;
        this.mCurrency = currencyId;
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
        mIconTimeImageView.setVisibility(View.GONE);
        mCFTTextView = (MPTextView) mView.findViewById(R.id.mpsdkCFT);
        mChangePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeSelected();
            }
        });
        if (mIsUniquePaymentMethod) {
            mChangePaymentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void draw() {

        decorateText();
        setIcon();

        if (mPayerCost.getInstallments() != 1) {
            mPaymentText.setVisibility(View.GONE);

            mPayerCostViewController = new PayerCostColumn(mContext, mCurrency);
            mPayerCostViewController.inflateInParent(mPayerCostContainer, true);
            mPayerCostViewController.initializeControls();
            mPayerCostViewController.drawPayerCost(mPayerCost);
            showFinance();


        } else {
            mPayerCostContainer.setVisibility(View.GONE);
            mCFTTextView.setVisibility(View.GONE);

            Spanned amountText = CurrenciesUtil.getFormattedAmount(mPayerCost.getTotalAmount(), mCurrency);
            mPaymentText.setText(amountText);
        }

        String description = mContext.getString(R.string.mpsdk_review_description_card, mPaymentMethod.getName(),
                mCardInfo.getLastFourDigits());

        mPaymentDescription.setText(description);
    }

    private void setIcon() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mPaymentImage.setImageResource(R.drawable.mpsdk_grey_review_payment_on);
            mPaymentImage.setColorFilter(mDecorationPreference.getBaseColor(), PorterDuff.Mode.SRC_ATOP);
        } else {
            mPaymentImage.setImageResource(R.drawable.mpsdk_review_payment_on);
        }
    }

    private void showFinance() {
        if (mPayerCost.hasCFT()) {
            mCFTTextView.setVisibility(View.VISIBLE);
            mCFTTextView.setText(CFT + mPayerCost.getCFTPercent());
        }
    }

    private void decorateText() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mChangePaymentTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }


}
