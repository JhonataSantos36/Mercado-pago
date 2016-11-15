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
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewSummaryView implements ReviewSummaryViewController {

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
    protected OnConfirmPaymentCallback mCallback;

    private Context mContext;
    private String mCurrencyId;
    private BigDecimal mAmount;
    private BigDecimal mDiscountPercentage;
    private BigDecimal mDiscountAmount;
    private PayerCost mPayerCost;
    private PaymentMethod mPaymentMethod;
    private DecorationPreference mDecorationPreference;

    public ReviewSummaryView(Context context, String currencyId, BigDecimal amount, PayerCost payerCost,
                             PaymentMethod paymentMethod, BigDecimal discountPercentage, BigDecimal discountAmount,
                             OnConfirmPaymentCallback callback, DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mCurrencyId = currencyId;
        this.mAmount = amount;
        this.mPayerCost = payerCost;
        this.mPaymentMethod = paymentMethod;
        this.mDiscountPercentage = discountPercentage;
        this.mDiscountAmount = discountAmount;
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
    public void drawSummary() {
        decorateButton();
        //Productos
        mProductsText.setText(getFormattedAmount(mAmount));
        //Descuentos
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
        if (MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {
            //Pagas
            showPayerCostRow();
            showTotal(mPayerCost.getTotalAmount());
        } else {
            mPayerCostRow.setVisibility(View.GONE);
            mFirstSeparator.setVisibility(View.GONE);
            showTotal(mAmount);
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
        String discountText = mContext.getResources().getString(R.string.mpsdk_review_summary_discounts,
                String.valueOf(mDiscountPercentage));
        mDiscountPercentageText.setText(discountText);
        mDiscountsText.setText(getFormattedAmount(mDiscountAmount));
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
        return mDiscountPercentage != null && mDiscountAmount != null && mCurrencyId != null;
    }

    private Spanned getFormattedAmount(BigDecimal amount) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, mCurrencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, mCurrencyId, originalNumber, false, true);
        return amountText;
    }

    private boolean hasSubtotal() {
        //TODO agregar envios cuando esté
        return hasDiscount();
    }

    private BigDecimal getSubtotal() {
        //TODO agregar envios cuando esté
        BigDecimal ans = mAmount;
        if (hasDiscount()) {
            ans = mAmount.subtract(mDiscountAmount);
        }
        return ans;
    }
}
