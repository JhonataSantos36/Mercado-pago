package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewSummaryView extends Reviewable {

    public static final String TEA = "TEA ";
    public static final String CFT = "CFT ";
    protected View mView;
    protected LinearLayout mProductsRow;
    protected LinearLayout mDiscountsRow;
    protected LinearLayout mSubtotalRow;
    protected LinearLayout mPayerCostRow;
    protected LinearLayout mTotalRow;
    protected MPTextView mProductsText;
    protected MPTextView mDiscountPercentageText;
    protected MPTextView mDiscountsText;
    protected MPTextView mSubtotalText;
    protected MPTextView mTotalText;
    protected View mFirstSeparator;
    protected View mSecondSeparator;
    protected FrameLayout mPayerCostContainer;
    protected FrameLayout mConfirmButton;
    protected MPTextView mConfirmTextView;
    protected MPTextView mTEATextView;
    protected MPTextView mCFTTextView;
    protected OnConfirmPaymentCallback mCallback;

    protected Context mContext;
    protected String mCurrencyId;
    protected BigDecimal mAmount;

    protected PayerCost mPayerCost;
    protected PaymentMethod mPaymentMethod;
    protected DecorationPreference mDecorationPreference;
    protected Discount mDiscount;

    public ReviewSummaryView(Context context, PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, String currency, DecorationPreference decorationPreference, OnConfirmPaymentCallback callback) {
        this.mContext = context;
        this.mCurrencyId = currency;
        this.mAmount = amount;
        this.mPayerCost = payerCost;
        this.mPaymentMethod = paymentMethod;
        this.mDiscount = discount;
        this.mCallback = callback;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public void initializeControls() {
        mProductsRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryProducts);
        mDiscountsRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryDiscounts);
        mSubtotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummarySubtotal);
        mPayerCostRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryPay);
        mTotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryTotal);
        mProductsText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryProductsText);
        mDiscountPercentageText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryDiscountPercentage);
        mDiscountsText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryDiscountsText);
        mSubtotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummarySubtotalText);
        mTotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        mFirstSeparator = mView.findViewById(R.id.mpsdkFirstSeparator);
        mSecondSeparator = mView.findViewById(R.id.mpsdkSecondSeparator);
        mPayerCostContainer = (FrameLayout) mView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        mConfirmButton = (FrameLayout) mView.findViewById(R.id.mpsdkReviewSummaryConfirmButton);
        mConfirmTextView = (MPTextView) mView.findViewById(R.id.mpsdkReviewButtonText);
        mTEATextView = (MPTextView) mView.findViewById(R.id.mpsdkTEA);
        mCFTTextView = (MPTextView) mView.findViewById(R.id.mpsdkCFT);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.confirmPayment();

            }
        });
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_review_summary_view, parent, attachToRoot);
        return mView;
    }

    @Override
    public void draw() {
        decorateButton();
        //Products
        mProductsText.setText(CurrenciesUtil.getFormattedAmount(mAmount,mCurrencyId));
        //Discounts
        if (hasDiscount()) {
            showDiscountRow();
        } else {
            mDiscountsRow.setVisibility(View.GONE);
        }
        //Subtotal
        if (hasSubtotal()) {
            mSubtotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(),mCurrencyId));
        } else {
            mSubtotalRow.setVisibility(View.GONE);
        }
        if (mPaymentMethod != null && MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {

            if(mPayerCost.getInstallments() == 1){
                hidePayerCostInfo();
            }else{
                showPayerCostRow();
                showFinance();
            }

            showTotal(mPayerCost.getTotalAmount());

        } else {

            hidePayerCostInfo();
            mTotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(),mCurrencyId));
        }
    }


    private void hidePayerCostInfo() {
        mPayerCostRow.setVisibility(View.GONE);
        mFirstSeparator.setVisibility(View.GONE);
        mSubtotalRow.setVisibility(View.GONE);
        mTEATextView.setVisibility(View.GONE);
        mCFTTextView.setVisibility(View.GONE);
    }
    private void showFinance() {
        if (mPayerCost.hasTEA()) {
            mTEATextView.setVisibility(View.VISIBLE);
            mTEATextView.setText(TEA + mPayerCost.getTEAPercent());
        }
        if (mPayerCost.hasCFT()) {
            mCFTTextView.setVisibility(View.VISIBLE);
            mCFTTextView.setText(CFT + mPayerCost.getCFTPercent());
        }
    }

    private void decorateButton() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mConfirmButton.setBackgroundColor(mDecorationPreference.getBaseColor());
            if (mDecorationPreference.isDarkFontEnabled()) {
                mConfirmTextView.setTextColor(mDecorationPreference.getDarkFontColor(mContext));
            }
        }
    }

    private void showDiscountRow() {
        StringBuilder formattedAmountBuilder = new StringBuilder();
        Spanned amountText;
        String discountText;

        if (mDiscount.hasPercentOff()) {
            discountText = mContext.getResources().getString(R.string.mpsdk_review_summary_discount_with_percent_off,
                    String.valueOf(mDiscount.getPercentOff()));
        } else {
            discountText = mContext.getResources().getString(R.string.mpsdk_review_summary_discount_with_amount_off);
        }

        mDiscountPercentageText.setText(discountText);

        formattedAmountBuilder.append("-");
        formattedAmountBuilder.append(CurrenciesUtil.formatNumber(mDiscount.getCouponAmount(), mCurrencyId));

        amountText = CurrenciesUtil.formatCurrencyInText(mDiscount.getCouponAmount(), mCurrencyId, formattedAmountBuilder.toString(), false, true);

        mDiscountsText.setText(amountText);
    }

    private void showPayerCostRow() {
        PayerCostColumn payerCostColumn = new PayerCostColumn(mContext, mCurrencyId);
        payerCostColumn.inflateInParent(mPayerCostContainer, true);
        payerCostColumn.initializeControls();
        payerCostColumn.drawPayerCostWithoutTotal(mPayerCost);
    }

    private void showTotal(BigDecimal amount) {
        mTotalText.setText(CurrenciesUtil.getFormattedAmount(amount,mCurrencyId));
    }

    private boolean hasDiscount() {
        return (mDiscount != null && mCurrencyId != null
                && (mDiscount.hasPercentOff() != null || mDiscount.getCouponAmount() != null));
    }



    private boolean hasSubtotal() {
        return hasDiscount();
    }

    private BigDecimal getSubtotal() {
        BigDecimal ans = mAmount;
        if (hasDiscount()) {
            ans = mAmount.subtract(mDiscount.getCouponAmount());
        }
        return ans;
    }
}
