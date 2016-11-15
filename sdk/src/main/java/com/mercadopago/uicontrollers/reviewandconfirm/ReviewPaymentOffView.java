package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.constants.Sites;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ReviewUtil;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOffView implements ReviewPaymentViewController {

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
    private BigDecimal mAmount;
    private PaymentMethodSearchItem mItem;
    private String mCurrencyId;
    private Site mSite;
    private OnChangePaymentMethodCallback mCallback;
    private boolean isUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;

    public ReviewPaymentOffView(Context context, PaymentMethod paymentMethod,
                                BigDecimal amount, PaymentMethodSearchItem item, String currencyId,
                                Site site, OnChangePaymentMethodCallback callback, boolean uniquePaymentMethod,
                                DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethod = paymentMethod;
        this.mAmount = amount;
        this.mItem = item;
        this.mCurrencyId = currencyId;
        this.mSite = site;
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

        mPaymentImage.setImageResource(R.drawable.review_payment_off);
        if (mSite != null && mSite.getId().equals(Sites.BRASIL.getId())) {
            Picasso.with(mContext)
                    .load(R.drawable.mpsdk_boleto_off)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.review_payment_off)
                    .into(mPaymentImage);
        }

        mPayerCostContainer.setVisibility(View.GONE);

        int paymentText = ReviewUtil.getPaymentInfoStringForItem(mItem);
        String originalNumber = CurrenciesUtil.formatNumber(mAmount, mCurrencyId);
        String itemName = ReviewUtil.getPaymentNameForItem(mItem, mPaymentMethod, mContext);

        String string = mContext.getString(paymentText, originalNumber, itemName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);

        Spanned amountText = CurrenciesUtil.formatCurrencyInText(mAmount, mCurrencyId, stringBuilder.toString(), false, true);

        mPaymentText.setText(amountText);
        mPaymentDescription.setText(mItem.getComment());
    }

    private void decorateText() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mChangePaymentTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }

}
