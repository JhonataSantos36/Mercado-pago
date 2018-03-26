package com.mercadopago.uicontrollers.payercosts;

import android.content.Context;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.InstallmentsUtil;

import java.math.BigDecimal;

/**
 * Created by vaserber on 10/25/16.
 */

public class PayerCostColumn {

    private final String mSiteId;
    private final String mCurrencyId;

    private final Context mContext;
    private View mView;
    private MPTextView mInstallmentsTextView;
    private MPTextView mZeroRateText;
    private MPTextView mTotalText;

    private final BigDecimal installmentsRate;
    private final BigDecimal payerCostTotalAmount;
    private final BigDecimal installmentsAmount;
    private final Integer installments;

    public PayerCostColumn(Context context, String currencyId, String siteId,
                           BigDecimal installmentsRate, Integer installments,
                           BigDecimal payerCostTotalAmount, BigDecimal installmentsAmount) {
        mContext = context;
        mCurrencyId = currencyId;
        mSiteId = siteId;
        this.installmentsRate = installmentsRate;
        this.installments = installments;
        this.payerCostTotalAmount = payerCostTotalAmount;
        this.installmentsAmount = installmentsAmount;
    }

    public void drawPayerCostWithoutTotal() {
        drawBasicPayerCost();
        hideTotalAmount();
        alignRight();
    }

    private void drawBasicPayerCost() {
        setInstallmentsText();

        if (!InstallmentsUtil.shouldWarnAboutBankInterests(mSiteId)) {
            if (installmentsRate.compareTo(BigDecimal.ZERO) == 0) {
                if (installments > 1) {
                    mZeroRateText.setVisibility(View.VISIBLE);
                } else {
                    mZeroRateText.setVisibility(View.GONE);
                }
            }
        }
    }

    public void drawPayerCost() {
        drawBasicPayerCost();
        setAmountWithRateText();
        alignCenter();
    }

    private void setAmountWithRateText() {
        mTotalText.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(payerCostTotalAmount, mCurrencyId));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(payerCostTotalAmount,
                mCurrencyId, sb.toString(), false, true);
        mTotalText.setText(spannedFullAmountText);
    }

    private void setInstallmentsText() {
        StringBuilder sb = new StringBuilder();
        sb.append(installments);
        sb.append(" ");
        sb.append(mContext.getString(R.string.mpsdk_installments_by));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(installmentsAmount, mCurrencyId));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(installmentsAmount,
                mCurrencyId, sb.toString(), false, true);
        mInstallmentsTextView.setText(spannedInstallmentsText);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    public void initializeControls() {
        mInstallmentsTextView = mView.findViewById(R.id.mpsdkInstallmentsText);
        mZeroRateText = mView.findViewById(R.id.mpsdkInstallmentsZeroRate);
        mTotalText = mView.findViewById(R.id.mpsdkInstallmentsTotalAmount);
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_column_payer_cost, parent, attachToRoot);
        return mView;
    }

    public View getView() {
        return mView;
    }

    private void hideTotalAmount() {
        mTotalText.setVisibility(View.GONE);
    }

    private void alignRight() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        mZeroRateText.setLayoutParams(params);
    }

    private void alignCenter() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mZeroRateText.setLayoutParams(params);
    }
}
