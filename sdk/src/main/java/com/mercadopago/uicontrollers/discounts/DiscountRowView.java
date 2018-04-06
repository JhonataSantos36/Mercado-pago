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
import com.mercadopago.lite.model.Currency;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.util.CurrenciesUtil;

import java.math.BigDecimal;

import static com.mercadopago.util.TextUtil.isEmpty;

/**
 * Created by mromar on 1/19/17.
 */

public class DiscountRowView implements DiscountView {

    //Local vars
    private final String mCurrencyId;
    private final BigDecimal mTransactionAmount;
    private final Context mContext;
    private final Discount mDiscount;
    private final Boolean mShortRowEnabled;
    private final Boolean mDiscountEnabled;
    private final Boolean mShowArrow;
    private final Boolean mShowSeparator;

    //Views
    private View mView;
    private MPTextView mTotalAmountTextView;
    private MPTextView mDiscountAmountTextView;
    private MPTextView mDiscountOffTextView;
    private MPTextView mDiscountConcept;
    private ImageView mDiscountArrow;
    private LinearLayout mHighDiscountRow;
    private LinearLayout mHasDiscountLinearLayout;
    private LinearLayout mHasDirectDiscountLinearLayout;
    private LinearLayout mDiscountDetail;
    private View mDiscountSeparator;

    public DiscountRowView(Context context, Discount discount, BigDecimal transactionAmount, String currencyId, Boolean shortRowEnabled,
                           Boolean discountEnabled, Boolean showArrow, Boolean showSeparator) {
        mContext = context;
        mDiscount = discount;
        mTransactionAmount = transactionAmount;
        mCurrencyId = currencyId;
        mShortRowEnabled = shortRowEnabled;
        mDiscountEnabled = discountEnabled;
        mShowArrow = showArrow;
        mShowSeparator = showSeparator;
    }

    @Override
    public void draw() {
        if (isDiscountEnabled()) {
            if (mDiscount == null) {
                showHasDiscountRow();
            } else {
                showDiscountDetailRow();
            }
        } else {
            showDefaultRow();
        }
    }

    private void showDefaultRow() {
        if (!isShortRowEnabled()) {
            showHighDefaultRow();
        }
    }

    private void showHighDefaultRow() {
        if (isAmountValid(mTransactionAmount) && CurrenciesUtil.isValidCurrency(mCurrencyId)) {
            mTotalAmountTextView.setText(getFormattedAmount(mTransactionAmount, mCurrencyId));
        } else {
            mHighDiscountRow.setVisibility(View.GONE);
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
        if (isDiscountCurrencyIdValid() && isAmountValid(mDiscount.getCouponAmount()) && isCampaignIdValid()) {
            mDiscountDetail.setVisibility(View.VISIBLE);
            mHasDiscountLinearLayout.setVisibility(View.GONE);

            setDiscountOff();
        }
    }

    private void drawHighDiscountDetailRow() {
        if (areValidParameters()) {
            mHasDirectDiscountLinearLayout.setVisibility(View.VISIBLE);
            mDiscountAmountTextView.setVisibility(View.VISIBLE);
            mHasDiscountLinearLayout.setVisibility(View.GONE);

            setDiscountConcept();
            setArrowVisibility();
            setSeparatorVisibility();
            setDiscountOff();
            setTotalAmountWithDiscount();
            setTotalAmount();
        } else {
            mHasDirectDiscountLinearLayout.setVisibility(View.GONE);
            mHighDiscountRow.setVisibility(View.GONE);
        }
    }

    private Boolean areValidParameters() {
        return isDiscountCurrencyIdValid() && isAmountValid(mTransactionAmount) && isAmountValid(mDiscount.getCouponAmount()) && isCampaignIdValid();
    }

    private void drawShortHasDiscountRow() {
        mDiscountDetail.setVisibility(View.GONE);
        mHasDiscountLinearLayout.setVisibility(View.VISIBLE);
    }

    private void drawHighHasDiscountRow() {
        if (isAmountValid(mTransactionAmount) && CurrenciesUtil.isValidCurrency(mCurrencyId)) {
            mHasDiscountLinearLayout.setVisibility(View.VISIBLE);
            mTotalAmountTextView.setText(getFormattedAmount(mTransactionAmount, mCurrencyId));
        } else {
            mHighDiscountRow.setVisibility(View.GONE);
        }
    }

    private void setDiscountConcept() {
        if (hasDiscountConcept()) {
            mDiscountConcept.setText(mDiscount.getConcept());
        }
    }

    private Boolean hasDiscountConcept() {
        return mDiscount != null && !isEmpty(mDiscount.getConcept());
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
        if (isAmountOffValid() && mDiscount.getAmountOff().compareTo(BigDecimal.ZERO) > 0) {
            Currency currency = CurrenciesUtil.getCurrency(mDiscount.getCurrencyId());
            String amount = currency.getSymbol() + " " + mDiscount.getAmountOff();

            mDiscountOffTextView.setText(amount);
        } else if (isPercentOffValid() && mDiscount.getPercentOff().compareTo(BigDecimal.ZERO) > 0) {
            String discountOff = mContext.getResources().getString(R.string.mpsdk_discount_percent_off,
                    String.valueOf(mDiscount.getPercentOff()));

            mDiscountOffTextView.setText(discountOff);
        } else {
            Currency currency = CurrenciesUtil.getCurrency(mDiscount.getCurrencyId());
            String amount = currency.getSymbol() + " " + mDiscount.getCouponAmount();

            mDiscountOffTextView.setText(amount);
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
        mHighDiscountRow = mView.findViewById(R.id.mpsdkDiscountRow);
        mTotalAmountTextView = mView.findViewById(R.id.mpsdkTotalAmount);
        mDiscountAmountTextView = mView.findViewById(R.id.mpsdkDiscountAmount);
        mDiscountOffTextView = mView.findViewById(R.id.mpsdkDiscountOff);
        mDiscountConcept = mView.findViewById(R.id.mpsdkDiscountConcept);
        mHasDiscountLinearLayout = mView.findViewById(R.id.mpsdkHasDiscount);
        mHasDirectDiscountLinearLayout = mView.findViewById(R.id.mpsdkHasDirectDiscount);
        mDiscountDetail = mView.findViewById(R.id.mpsdkDiscountDetail);
        mDiscountArrow = mView.findViewById(R.id.mpsdkDiscountArrow);
        mDiscountSeparator = mView.findViewById(R.id.mpsdkDiscountSeparator);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
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

    private Boolean isDiscountEnabled() {
        return mDiscountEnabled == null || mDiscountEnabled;
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.getSpannedString(amount, currencyId, false, true);
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isAmountOffValid() {
        return mDiscount != null && mDiscount.getAmountOff() != null && isAmountValid(mDiscount.getAmountOff());
    }

    private Boolean isPercentOffValid() {
        return mDiscount != null && mDiscount.getPercentOff() != null && mDiscount.getPercentOff().compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isDiscountCurrencyIdValid() {
        return mDiscount != null && mDiscount.getCurrencyId() != null && CurrenciesUtil.isValidCurrency(mDiscount.getCurrencyId());
    }

    private Boolean isCampaignIdValid() {
        return mDiscount != null && mDiscount.getId() != null;
    }
}
