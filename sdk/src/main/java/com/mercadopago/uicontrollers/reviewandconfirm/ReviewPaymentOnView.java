package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOnView implements ReviewPaymentViewController {

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
    private CardInfo mCardInfo;
    private String mCurrencyId;
    private PayerCost mPayerCost;
    private PayerCostViewController payerCostViewController;

    private OnChangePaymentMethodCallback mCallback;
    private boolean isUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;

    public ReviewPaymentOnView(Context context, PaymentMethod paymentMethod, CardInfo cardInfo,
                               String currencyId, PayerCost payerCost, OnChangePaymentMethodCallback callback,
                               boolean uniquePaymentMethod, DecorationPreference decorationPreference) {

        this.mContext = context;
        this.mPaymentMethod = paymentMethod;
        this.mCardInfo = cardInfo;
        this.mCurrencyId = currencyId;
        this.mPayerCost = payerCost;
        this.mCallback = callback;
        this.isUniquePaymentMethod = uniquePaymentMethod;
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
        mChangePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangePaymentMethodSelected();
            }
        });
        if (isUniquePaymentMethod) {
            mChangePaymentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void drawPaymentMethod() {
        decorateText();
        mPaymentImage.setImageResource(R.drawable.mpsdk_review_payment_on);
        mPaymentText.setVisibility(View.GONE);

        payerCostViewController = ReviewPaymentViewFactory.getReviewPayerCostController(mContext, mCurrencyId);
        payerCostViewController.inflateInParent(mPayerCostContainer, true);
        payerCostViewController.initializeControls();
        payerCostViewController.drawPayerCost(mPayerCost);

        String description = mContext.getString(R.string.mpsdk_review_description_card, mPaymentMethod.getName(),
                mCardInfo.getLastFourDigits());
        mPaymentDescription.setText(description);
    }

    private void decorateText() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mChangePaymentTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }
}
