package com.mercadopago.uicontrollers.installments;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

/**
 * Created by mromar on 2/3/17.
 */

public class InstallmentsReviewView implements InstallmentsView {

    //Local vars
    private final Context mContext;
    private final PayerCost mPayerCost;
    private final String mCurrencyId;

    //Views
    private View mView;
    private MPTextView mInstallmentsAmount;
    private MPTextView mTotalAmount;
    private MPTextView mCftpercent;
    private MPTextView mContinueTextButton;
    private FrameLayout mInstallmentsContinueButton;

    public InstallmentsReviewView(Context context, PayerCost payerCost, String currencyId) {
        mContext = context;
        mPayerCost = payerCost;
        mCurrencyId = currencyId;
    }

    @Override
    public void draw() {
        setInstallmentAmountText();
        setTotalAmountWithRateText();
        setCFTPercentText();
    }

    private void setInstallmentAmountText() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(mPayerCost.getInstallments());
        stringBuilder.append(" ");
        stringBuilder.append(mContext.getString(R.string.mpsdk_installments_by));
        stringBuilder.append(" ");

        stringBuilder.append(CurrenciesUtil.formatNumber(mPayerCost.getInstallmentAmount(), mCurrencyId));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getInstallmentAmount(),
                mCurrencyId, stringBuilder.toString(), false, true);

        mInstallmentsAmount.setText(spannedInstallmentsText);
    }

    private void setTotalAmountWithRateText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(CurrenciesUtil.formatNumber(mPayerCost.getTotalAmount(), mCurrencyId));
        stringBuilder.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getTotalAmount(),
                mCurrencyId, stringBuilder.toString(), false, true);

        mTotalAmount.setText(spannedFullAmountText);
    }

    private void setCFTPercentText() {
        String cftPercent = mContext.getString(R.string.mpsdk_installments_cft) + " " + mPayerCost.getCFTPercent();
        mCftpercent.setText(cftPercent);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mInstallmentsContinueButton.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mInstallmentsContinueButton = mView.findViewById(R.id.mpsdkInstallmentsContinueButton);
        mContinueTextButton = mView.findViewById(R.id.mpsdkInstallmentsButtonText);
        mInstallmentsAmount = mView.findViewById(R.id.mpsdkInstallmentsAmount);
        mTotalAmount = mView.findViewById(R.id.mpsdkReviewTotalAmount);
        mCftpercent = mView.findViewById(R.id.mpsdkCftpercent);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        parent.removeAllViews();

        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_installments_review_view, parent, attachToRoot);

        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }
}


