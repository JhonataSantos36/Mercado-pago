package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;

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
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.util.UnlockCardUtil;

import java.math.BigDecimal;

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewSummaryView extends Reviewable {

    public static final String CFT = "CFT ";
    protected View mView;

    protected LinearLayout mProductsRow;
    protected LinearLayout mDiscountsRow;
    protected LinearLayout mSubtotalRow;
    protected LinearLayout mPayerCostRow;
    protected LinearLayout mTotalRow;
    protected LinearLayout mCustomRow;
    protected MPTextView mProductsText;
    protected MPTextView mDiscountPercentageText;
    protected MPTextView mDiscountsText;
    protected MPTextView mSubtotalText;
    protected MPTextView mTotalText;
    protected View mFirstSeparator;
    protected View mSecondSeparator;
    protected FrameLayout mPayerCostContainer;
    protected MPTextView mCFTTextView;
    protected MPTextView mCustomDescriptionTextView;
    protected MPTextView mCustomAmountTextView;

    protected OnConfirmPaymentCallback mCallback;

    protected Context mContext;
    protected String mProductDetailText;
    protected String mDiscountDetailText;
    protected String mConfirmationMessage;
    protected String mCurrencyId;
    protected String mCustomDescription;
    protected Integer mCustomTextColor;
    protected BigDecimal mAmount;
    protected BigDecimal mCustomAmount;
    protected PayerCost mPayerCost;
    protected PaymentMethod mPaymentMethod;
    protected DecorationPreference mDecorationPreference;
    protected Discount mDiscount;
    protected MPTextView mProductsLabelText;
    private MPTextView mUnlockCardTextView;
    private LinearLayout mUnlockCard;
    private Issuer mIssuer;
    private Site mSite;
    private String mUnlockLink;

    public ReviewSummaryView(Context context, String confirmationMessage, String productDetailText, String discountDetailText, PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, String currencyId, String customDescription, BigDecimal customAmount, Site site, Issuer issuer, @ColorInt Integer customTextColor, DecorationPreference decorationPreference, OnConfirmPaymentCallback callback) {
        this.mContext = context;
        this.mConfirmationMessage = confirmationMessage;
        this.mProductDetailText = productDetailText;
        this.mDiscountDetailText = discountDetailText;
        this.mCurrencyId = currencyId;
        this.mAmount = amount;
        this.mPayerCost = payerCost;
        this.mPaymentMethod = paymentMethod;
        this.mDiscount = discount;
        this.mCustomDescription = customDescription;
        this.mCustomAmount = customAmount;
        this.mCustomTextColor = customTextColor;
        this.mCallback = callback;
        this.mDecorationPreference = decorationPreference;
        this.mIssuer = issuer;
        this.mSite = site;
    }

    @Override
    public void initializeControls() {
        mProductsRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryProducts);
        mProductsLabelText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryProductsLabelText);
        mDiscountsRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryDiscounts);
        mSubtotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummarySubtotal);
        mPayerCostRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryPay);
        mTotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryTotal);
        mCustomRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryCustom);
        mProductsText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryProductsText);
        mDiscountPercentageText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryDiscountPercentage);
        mDiscountsText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryDiscountsText);
        mSubtotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummarySubtotalText);
        mTotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        mFirstSeparator = mView.findViewById(R.id.mpsdkFirstSeparator);
        mSecondSeparator = mView.findViewById(R.id.mpsdkSecondSeparator);
        mPayerCostContainer = (FrameLayout) mView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        mCFTTextView = (MPTextView) mView.findViewById(R.id.mpsdkCFT);
        mUnlockCard = (LinearLayout) mView.findViewById(R.id.mpsdkCheckoutUnlockCard);
        mUnlockCardTextView = (MPTextView) mView.findViewById(R.id.mpsdkUnlockCard);
        mCustomDescriptionTextView = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryCustomText);
        mCustomAmountTextView = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryCustomAmount);
    }

    private void startUnlockCardActivity() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUnlockLink));
        mContext.startActivity(browserIntent);
    }

    public void showUnlockCard() {

        mUnlockCard.setVisibility(View.VISIBLE);

        mUnlockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUnlockCardActivity();
            }
        });
    }

    private boolean isCardUnlockingNeeded() {
        String link = getCardUnlockingLink();
        if (!TextUtil.isEmpty(link)) {
            mUnlockLink = link;
            return true;
        }
        return false;
    }

    private String getCardUnlockingLink() {
        if (mSite == null || mIssuer == null) {
            return null;
        }
        return UnlockCardUtil.getCardUnlockingLink(mSite.getId(), mIssuer.getId());
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
        mProductsLabelText.setText(mProductDetailText);
        mProductsText.setText(CurrenciesUtil.getFormattedAmount(mAmount, mCurrencyId));

        //Custom info
        if (hasCustomInfo()) {
            showCustomInfo();
        }

        //Discounts
        if (hasDiscount()) {
            showDiscountRow();
        } else {
            mDiscountsRow.setVisibility(View.GONE);
        }
        //Subtotal
        if (hasSubtotal()) {
            mSubtotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(), mCurrencyId));
        } else {
            mSubtotalRow.setVisibility(View.GONE);
        }

        if (isCardPaymentMethod()) {
            if (mPayerCost.getInstallments() == 1) {
                hidePayerCostInfo();
                if (mDiscount == null) {
                    hideTotalRow();
                } else {
                    showTotal(mPayerCost.getTotalAmount());
                }
            } else {
                showPayerCostRow();
                showFinance();
                showTotal(mPayerCost.getTotalAmount());

            }

        } else if (!hasDiscount()) {
            hideTotalRow();
            hidePayerCostInfo();
        } else {
            hidePayerCostInfo();
            mTotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(), mCurrencyId));

        }

        if (isCardUnlockingNeeded()) {
            showUnlockCard();
        }

    }

    private void hideTotalRow() {
        mTotalRow.setVisibility(View.GONE);
        mSecondSeparator.setVisibility(View.GONE);
    }

    private void hidePayerCostInfo() {
        mPayerCostRow.setVisibility(View.GONE);
        mFirstSeparator.setVisibility(View.GONE);
        mSubtotalRow.setVisibility(View.GONE);
        mCFTTextView.setVisibility(View.GONE);
    }

    private void showFinance() {
        if (mPayerCost.hasCFT()) {
            mCFTTextView.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(CFT);
            stringBuilder.append(mPayerCost.getCFTPercent());
            mCFTTextView.setText(stringBuilder);
        }
    }

    private void decorateButton() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mUnlockCardTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }

    private void showCustomInfo() {
        mCustomRow.setVisibility(View.VISIBLE);
        mCustomDescriptionTextView.setText(mCustomDescription);

        setCustomAmount();
        setCustomTextColor();
    }

    private void setCustomAmount() {
        if (mCurrencyId != null && mCustomAmount != null && mCustomAmount.compareTo(BigDecimal.ZERO) >= 0) {
            Spanned customAmount = CurrenciesUtil.getFormattedAmount(mCustomAmount, mCurrencyId);
            mCustomAmountTextView.setText(customAmount);
        } else {
            mCustomAmountTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setCustomTextColor() {
        if (mCustomTextColor != null) {
            mCustomDescriptionTextView.setTextColor(mCustomTextColor);

            if (mCustomAmount != null) {
                mCustomAmountTextView.setTextColor(mCustomTextColor);
            }
        }
    }

    private void showDiscountRow() {
        StringBuilder formattedAmountBuilder = new StringBuilder();
        Spanned amountText;
        String discountText;

        discountText = getDiscountText();
        mDiscountPercentageText.setText(discountText);

        formattedAmountBuilder.append("-");
        formattedAmountBuilder.append(CurrenciesUtil.formatNumber(mDiscount.getCouponAmount(), mCurrencyId));

        amountText = CurrenciesUtil.formatCurrencyInText(mDiscount.getCouponAmount(), mCurrencyId, formattedAmountBuilder.toString(), false, true);

        mDiscountsText.setText(amountText);
    }

    private String getDiscountText() {
        String discountText;

        if (mDiscount.hasPercentOff()) {
            discountText = getDiscountTextWithPercentOff();
        } else {
            discountText = getDiscountTextWithoutPercentOff();
        }

        return discountText;
    }

    private String getDiscountTextWithPercentOff() {
        String discountText;

        if (hasDiscountConcept()) {
            discountText = mDiscount.getConcept() + " " + mDiscount.getPercentOff() + mContext.getResources().getString(R.string.mpsdk_percent);
        } else {
            discountText = mContext.getResources().getString(R.string.mpsdk_review_summary_discount_with_percent_off,
                    String.valueOf(mDiscount.getPercentOff()));
        }

        return discountText;
    }

    private String getDiscountTextWithoutPercentOff() {
        String discountText;

        if (hasDiscountConcept()) {
            discountText = mDiscount.getConcept();
        } else {
            discountText = mContext.getResources().getString(R.string.mpsdk_review_summary_discount_with_amount_off);
        }

        return discountText;
    }

    private Boolean hasDiscountConcept() {
        return mDiscount != null && !isEmpty(mDiscount.getConcept());
    }

    private void showPayerCostRow() {
        PayerCostColumn payerCostColumn = new PayerCostColumn(mContext, mSite);
        payerCostColumn.inflateInParent(mPayerCostContainer, true);
        payerCostColumn.initializeControls();
        payerCostColumn.drawPayerCostWithoutTotal(mPayerCost);
    }

    private void showTotal(BigDecimal amount) {
        mTotalText.setText(CurrenciesUtil.getFormattedAmount(amount, mCurrencyId));
    }

    private boolean hasCustomInfo() {
        return !isEmpty(mCustomDescription);
    }

    private boolean hasDiscount() {
        return (mDiscount != null && mCurrencyId != null
                && (mDiscount.hasPercentOff() != null || mDiscount.getCouponAmount() != null));
    }


    private boolean hasSubtotal() {
        return hasDiscount();
    }

    private boolean isCardPaymentMethod() {
        return mPaymentMethod != null && MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId());
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
