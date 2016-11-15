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
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;

/**
 * Created by vaserber on 10/25/16.
 */

public class PayerCostColumn implements PayerCostViewController {

    private PayerCost mPayerCost;
    private String mCurrencyId;

    private Context mContext;
    private View mView;
    private MPTextView mInstallmentsTextView;
    private MPTextView mZeroRateText;
    private MPTextView mTotalText;

    public PayerCostColumn(Context context, String currencyId) {
        this.mContext = context;
        this.mCurrencyId = currencyId;
    }

    @Override
    public void drawPayerCost(PayerCost payerCost) {
        drawBasicPayerCost(payerCost);
        setAmountWithRateText();
        alignCenter();
    }

    @Override
    public void drawPayerCostWithoutTotal(PayerCost payerCost) {
        drawBasicPayerCost(payerCost);
        hideTotalAmount();
        alignRight();
    }

    private void drawBasicPayerCost(PayerCost payerCost) {
        mPayerCost = payerCost;
        setInstallmentsText();

        if (payerCost.getInstallmentRate().compareTo(BigDecimal.ZERO) == 0) {
            if (payerCost.getInstallments() > 1) {
                mZeroRateText.setVisibility(View.VISIBLE);
            } else {
                mZeroRateText.setVisibility(View.GONE);
            }
        }
    }

    private void setAmountWithRateText() {
        mTotalText.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getTotalAmount(), mCurrencyId));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getTotalAmount(),
                mCurrencyId, sb.toString(), false, true);
        mTotalText.setText(spannedFullAmountText);
    }

    private void setInstallmentsText() {
        StringBuilder sb = new StringBuilder();
        sb.append(mPayerCost.getInstallments());
        sb.append(" ");
        sb.append(mContext.getString(R.string.mpsdk_installments_by));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getInstallmentAmount(), mCurrencyId));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getInstallmentAmount(),
                mCurrencyId, sb.toString(), false, true);
        mInstallmentsTextView.setText(spannedInstallmentsText);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mInstallmentsTextView = (MPTextView) mView.findViewById(R.id.mpsdkInstallmentsText);
        mZeroRateText = (MPTextView) mView.findViewById(R.id.mpsdkInstallmentsZeroRate);
        mTotalText = (MPTextView) mView.findViewById(R.id.mpsdkInstallmentsTotalAmount);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_column_payer_cost, parent, attachToRoot);
        return mView;
    }

    @Override
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
