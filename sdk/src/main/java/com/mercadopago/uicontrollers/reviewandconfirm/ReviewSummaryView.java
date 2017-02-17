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
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.preferences.DecorationPreference;
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
    protected String mProductDetailText;
    protected String mDiscountDetailText;
    protected String mConfirmationMessage;
    protected String mCurrencyId;
    protected BigDecimal mAmount;
    protected PayerCost mPayerCost;
    protected PaymentMethod mPaymentMethod;
    protected DecorationPreference mDecorationPreference;
    protected Discount mDiscount;
    protected MPTextView mProductsLabelText;

    public ReviewSummaryView(Context context, String confirmationMessage, String productDetailText, String discountDetailText, PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, String currencyId, DecorationPreference decorationPreference, OnConfirmPaymentCallback callback) {
        this.mContext = context;
        this.mConfirmationMessage = confirmationMessage;
        this.mProductDetailText = productDetailText;
        this.mDiscountDetailText = discountDetailText;
        this.mCurrencyId = currencyId;
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
        mProductsLabelText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryProductsLabelText);
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
        mConfirmTextView.setText(mConfirmationMessage);
        mProductsText.setText(getFormattedAmount(mAmount));
        mProductsLabelText.setText(mProductDetailText);
        //Discounts
        if (hasDiscount()) {
            showDiscountRow();
        } else {
            mDiscountsRow.setVisibility(View.GONE);
        }
        //Subtotal
        if (hasSubtotal()) {
            mSubtotalText.setText(getFormattedAmount(getSubtotal()));
        } else {
            mSubtotalRow.setVisibility(View.GONE);
        }
        if (mPaymentMethod != null && MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {
            //Pagas
            showPayerCostRow();
            showTotal(mPayerCost.getTotalAmount());
            showFinance();
        } else {
            mPayerCostRow.setVisibility(View.GONE);
            mFirstSeparator.setVisibility(View.GONE);
            mSubtotalRow.setVisibility(View.GONE);
            mTotalText.setText(getFormattedAmount(getSubtotal()));
        }
    }

    private void showFinance() {
        if(mPayerCost.hasTEA()) {
            mTEATextView.setVisibility(View.VISIBLE);
            mTEATextView.setText(TEA + mPayerCost.getTEAPercent());
        }
        if(mPayerCost.hasCFT()) {
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

        mDiscountPercentageText.setText(mDiscountDetailText);

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
        mTotalText.setText(getFormattedAmount(amount));
    }

    private boolean hasDiscount() {
        return (mDiscount != null && mCurrencyId != null
                && (mDiscount.hasPercentOff() != null || mDiscount.getCouponAmount() != null));
    }

    private Spanned getFormattedAmount(BigDecimal amount) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, mCurrencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, mCurrencyId, originalNumber, false, true);
        return amountText;
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

    @Override
    public String getKey() {
        return ReviewKeys.SUMMARY;
    }
}
