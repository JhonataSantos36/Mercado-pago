package com.mercadopago.uicontrollers.summaryrows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.SummaryItemType;
import com.mercadopago.model.SummaryRow;
import com.mercadopago.util.CurrenciesUtil;

/**
 * Created by mromar on 9/11/17.
 */

public class SummaryRowView implements SummaryRowViewController {

    private final Context mContext;
    private View mView;

    private MPTextView mSummaryLableText;
    private MPTextView mSummaryAmountText;

    public SummaryRowView(Context context) {
        mContext = context;
    }

    @Override
    public void initializeControls() {
        mSummaryLableText = mView.findViewById(R.id.mpsdkReviewSummaryLabelText);
        mSummaryAmountText = mView.findViewById(R.id.mpsdkReviewSummaryAmountText);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_view_summary_row, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void drawSummaryRow(SummaryRow summaryRow) {
        mSummaryLableText.setText(summaryRow.getTitle());
        setAmount(summaryRow);

        mSummaryLableText.setTextColor(summaryRow.getRowTextColor());
        mSummaryAmountText.setTextColor(summaryRow.getRowTextColor());
    }

    private void setAmount(SummaryRow summaryRow) {
        StringBuilder formattedAmountBuilder = new StringBuilder();

        if (summaryRow.getSummaryItemType().equals(SummaryItemType.DISCOUNT)) {
            formattedAmountBuilder.append("-");
            formattedAmountBuilder.append(CurrenciesUtil.formatNumber(summaryRow.getAmount(), summaryRow.getCurrencyId()));

            mSummaryAmountText.setText(CurrenciesUtil.formatCurrencyInText(summaryRow.getAmount(), summaryRow.getCurrencyId(), formattedAmountBuilder.toString(), false, true));
        } else {
            mSummaryAmountText.setText(CurrenciesUtil.getFormattedAmount(summaryRow.getAmount(), summaryRow.getCurrencyId()));
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }
}
