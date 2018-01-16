package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.adapters.SummaryRowAdapter;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.model.Summary;
import com.mercadopago.model.SummaryDetail;
import com.mercadopago.model.SummaryRow;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.util.UnlockCardUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

/**
 * Created by vaserber on 11/10/16.
 */

public class SummaryView extends Reviewable {

    public static final String CFT = "CFT ";
    protected View mView;

    protected LinearLayout mSubtotalRow;
    protected LinearLayout mPayerCostRow;
    protected LinearLayout mTotalRow;
    protected MPTextView mSubtotalText;
    protected MPTextView mTotalText;
    protected View mFirstSeparator;
    protected View mSecondSeparator;
    protected FrameLayout mPayerCostContainer;
    protected MPTextView mDisclaimerText;
    protected MPTextView mCFTTextView;
    protected RecyclerView mSummaryRowsRecyclerView;

    protected OnConfirmPaymentCallback mCallback;

    protected Context mContext;
    protected String mConfirmationMessage;
    protected String mCurrencyId;
    protected BigDecimal mAmount;
    protected PayerCost mPayerCost;
    protected PaymentMethod mPaymentMethod;
    protected SummaryRowAdapter mSummaryRowAdapter;
    protected Discount mDiscount;
    private MPTextView mUnlockCardTextView;
    private LinearLayout mUnlockCard;
    private Issuer mIssuer;
    private Site mSite;
    private String mUnlockLink;
    private Summary mSummary;

    public SummaryView(Context context, String confirmationMessage, PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, String currencyId, Site site, Issuer issuer, Summary summary, OnConfirmPaymentCallback callback) {
        this.mContext = context;
        this.mConfirmationMessage = confirmationMessage;
        this.mCurrencyId = currencyId;
        this.mAmount = amount;
        this.mPayerCost = payerCost;
        this.mPaymentMethod = paymentMethod;
        this.mDiscount = discount;
        this.mCallback = callback;
        this.mIssuer = issuer;
        this.mSite = site;
        this.mSummary = summary;
    }

    @Override
    public void initializeControls() {
        mSummaryRowsRecyclerView = (RecyclerView) mView.findViewById(R.id.mpsdkActivitySummaryView);
        mSubtotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummarySubtotal);
        mPayerCostRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryPay);
        mTotalRow = (LinearLayout) mView.findViewById(R.id.mpsdkReviewSummaryTotal);
        mSubtotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummarySubtotalText);
        mTotalText = (MPTextView) mView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        mFirstSeparator = mView.findViewById(R.id.mpsdkFirstSeparator);
        mSecondSeparator = mView.findViewById(R.id.mpsdkSecondSeparator);
        mPayerCostContainer = (FrameLayout) mView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        mCFTTextView = (MPTextView) mView.findViewById(R.id.mpsdkCFT);
        mDisclaimerText = (MPTextView) mView.findViewById(R.id.mpsdkDisclaimer);
        mUnlockCard = (LinearLayout) mView.findViewById(R.id.mpsdkCheckoutUnlockCard);
        mUnlockCardTextView = (MPTextView) mView.findViewById(R.id.mpsdkUnlockCard);
    }

    private void initializeAdapter(OnSelectedCallback<Integer> onSelectedCallback) {
        mSummaryRowAdapter = new SummaryRowAdapter(mContext, onSelectedCallback);
        initializeAdapterListener(mSummaryRowAdapter, mSummaryRowsRecyclerView);
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(mContext));
        view.addOnItemTouchListener(new RecyclerItemClickListener(mContext,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }
                }));
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
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_review_summary_view, parent, attachToRoot);
        return mView;
    }

    @Override
    public void draw() {
        List<SummaryRow> summaryRowList = getSummaryRows();
        drawSummaryRows(summaryRowList, getDpadSelectionCallback());

        if (mSummary.showSubtotal() && hasSubtotal()) {
            mSubtotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(), mCurrencyId));
        } else {
            mSubtotalRow.setVisibility(View.GONE);
        }

        if (isCardPaymentMethod()) {
            if (mPayerCost.getInstallments() == 1) {
                hidePayerCostInfo();
                if (mDiscount == null && isEmptySummaryDetails()) {
                    hideTotalRow();
                } else {
                    showTotal(mPayerCost.getTotalAmount());
                }
            } else {
                showPayerCostRow();
                showFinance();
                showTotal(mPayerCost.getTotalAmount());
            }
        } else if (!hasDiscount() && isEmptySummaryDetails()) {
            hideTotalRow();
            hidePayerCostInfo();
        } else {
            hidePayerCostInfo();
            mTotalText.setText(CurrenciesUtil.getFormattedAmount(getSubtotal(), mCurrencyId));
        }

        if (!isEmpty(mSummary.getDisclaimerText())) {
            mDisclaimerText.setText(mSummary.getDisclaimerText());
            mDisclaimerText.setVisibility(View.VISIBLE);
            mDisclaimerText.setTextColor(mSummary.getDisclaimerColor());
        }

        if (isCardUnlockingNeeded()) {
            showUnlockCard();
        }
    }

    private boolean isEmptySummaryDetails() {
        return mSummary != null && mSummary.getSummaryDetails() != null && mSummary.getSummaryDetails().size() < 2;
    }

    private void drawSummaryRows(List<SummaryRow> summaryRowList, OnSelectedCallback<Integer> onSelectedCallback) {
        initializeAdapter(onSelectedCallback);
        mSummaryRowAdapter.addResults(summaryRowList);
    }

    private OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
            }
        };
    }

    private List<SummaryRow> getSummaryRows() {
        List<SummaryRow> summaryRows = new ArrayList<>();
        List<SummaryDetail> summaryDetails = mSummary.getSummaryDetails();

        for (SummaryDetail summaryDetail : summaryDetails) {
            SummaryRow summaryRow = new SummaryRow(summaryDetail.getTitle(), summaryDetail.getTotalAmount(), mCurrencyId, summaryDetail.getSummaryItemType(), summaryDetail.getTextColor());
            summaryRows.add(summaryRow);
        }

        return summaryRows;
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

    private boolean hasDiscount() {
        return (mDiscount != null && mCurrencyId != null && (mDiscount.hasPercentOff() != null || mDiscount.getCouponAmount() != null));
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
