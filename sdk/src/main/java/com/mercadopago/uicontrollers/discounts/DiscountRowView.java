package com.mercadopago.uicontrollers.discounts;

import android.content.Context;
import android.graphics.Paint;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Currency;
import com.mercadopago.model.Discount;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

/**
 * Created by mromar on 1/19/17.
 */

public class DiscountRowView implements DiscountView {

    //Local vars
    private String mCurrencyId;
    private BigDecimal mTransactionAmount;
    private Context mContext;
    private Discount mDiscount;
    private Boolean mShortRowEnabled;
    private Boolean mShowArrow;
    private Boolean mShowSeparator;


    //Views
    private View mView;
    private MPTextView mTotalAmountTextView;
    private MPTextView mDiscountAmountTextView;
    private MPTextView mDiscountOffTextView;
    private ImageView mDiscountArrow;
    private LinearLayout mHighDiscountRow;
    private LinearLayout mHasDiscountLinearLayout;
    private LinearLayout mHasDirectDiscountLinearLayout;
    private LinearLayout mDiscountDetail;
    private View mDiscountSeparator;

    public DiscountRowView(Context context, Discount discount, BigDecimal transactionAmount, String currencyId, Boolean shortRowEnabled,
                           Boolean showArrow, Boolean showSeparator) {
        mContext = context;
        mDiscount = discount;
        mTransactionAmount = transactionAmount;
        mCurrencyId = currencyId;
        mShortRowEnabled = shortRowEnabled;
        mShowArrow = showArrow;
        mShowSeparator = showSeparator;
    }

    @Override
    public void draw() {
        if (mDiscount == null) {
            showHasDiscountRow();
        } else {
            showDiscountDetailRow();
        }
    }

    private void showHasDiscountRow() {
        if (isShortRowEnabled()) {
            drawShortHasDiscountRow();
        } else {
            drawHighHasDiscountRow();
        }
    }

    private void showDiscountDetailRow() {
        if (isShortRowEnabled()) {
            drawShortDiscountDetailRow();
        } else {
            drawHighDiscountDetailRow();
        }
    }

    private void drawShortDiscountDetailRow() {
        mDiscountDetail.setVisibility(View.VISIBLE);
        mHasDiscountLinearLayout.setVisibility(View.GONE);

        setDiscountOff();
    }

    private void drawHighDiscountDetailRow() {
        mHasDirectDiscountLinearLayout.setVisibility(View.VISIBLE);
        mDiscountAmountTextView.setVisibility(View.VISIBLE);
        mHasDiscountLinearLayout.setVisibility(View.GONE);

        setArrowVisibility();
        setSeparatorVisibility();
        showHighDiscountRow();
        setDiscountOff();
        setTotalAmountWithDiscount();
        setTotalAmount();
    }

    private void drawShortHasDiscountRow() {
        mDiscountDetail.setVisibility(View.GONE);
        mHasDiscountLinearLayout.setVisibility(View.VISIBLE);
    }

    private void drawHighHasDiscountRow() {
        if (mTransactionAmount != null && !isEmpty(mCurrencyId)) {
            mHasDiscountLinearLayout.setVisibility(View.VISIBLE);
            mTotalAmountTextView.setText(getFormattedAmount(mTransactionAmount, mCurrencyId));

            showHighDiscountRow();
        }
    }

    private void setArrowVisibility() {
        if (mShowArrow != null && !mShowArrow) {
            mDiscountArrow.setVisibility(View.GONE);
        }
    }

    private void setSeparatorVisibility() {
        if (mShowSeparator != null && !mShowSeparator) {
            mDiscountSeparator.setVisibility(View.GONE);
        }
    }

    private void setDiscountOff() {
        if (mDiscount.getAmountOff() != null && mDiscount.getAmountOff().compareTo(BigDecimal.ZERO) > 0) {
            Currency currency = CurrenciesUtil.getCurrency(mDiscount.getCurrencyId());
            String amount = currency.getSymbol() + " " + mDiscount.getAmountOff();
            mDiscountOffTextView.setText(amount);
        } else {
            String discountOff = mContext.getResources().getString(R.string.mpsdk_discount_percent_off,
                    String.valueOf(mDiscount.getPercentOff()));
            mDiscountOffTextView.setText(discountOff);
        }
    }

    private void setTotalAmountWithDiscount() {
        mDiscountAmountTextView.setText(getFormattedAmount(mDiscount.getAmountWithDiscount(mTransactionAmount), mDiscount.getCurrencyId()));
    }

    private void setTotalAmount() {
        mTotalAmountTextView.setText(getFormattedAmount(mTransactionAmount, mDiscount.getCurrencyId()));
        mTotalAmountTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mHighDiscountRow = (LinearLayout) mView.findViewById(R.id.mpsdkDiscountRow);
        mTotalAmountTextView = (MPTextView) mView.findViewById(R.id.mpsdkTotalAmount);
        mDiscountAmountTextView = (MPTextView) mView.findViewById(R.id.mpsdkDiscountAmount);
        mDiscountOffTextView = (MPTextView) mView.findViewById(R.id.mpsdkDiscountOff);
        mHasDiscountLinearLayout = (LinearLayout) mView.findViewById(R.id.mpsdkHasDiscount);
        mHasDirectDiscountLinearLayout = (LinearLayout) mView.findViewById(R.id.mpsdkHasDirectDiscount);
        mDiscountDetail = (LinearLayout) mView.findViewById(R.id.mpsdkDiscountDetail);
        mDiscountArrow = (ImageView) mView.findViewById(R.id.mpsdkDiscountArrow);
        mDiscountSeparator = mView.findViewById(R.id.mpsdkDiscountSeparator);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        //TODO Review
        parent.removeAllViews();
        if (isShortRowEnabled()) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_row_guessing_discount, parent, attachToRoot);
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_row_discount, parent, attachToRoot);
        }
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    private Boolean isShortRowEnabled() {
        return mShortRowEnabled != null && mShortRowEnabled;
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, currencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, currencyId, originalNumber, false, true);
        return amountText;
    }

    private void showHighDiscountRow() {
        mHighDiscountRow.setVisibility(View.VISIBLE);
    }
}
