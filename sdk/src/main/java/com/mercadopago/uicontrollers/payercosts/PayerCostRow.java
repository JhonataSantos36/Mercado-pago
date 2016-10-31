package com.mercadopago.uicontrollers.payercosts;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;

/**
 * Created by mreverter on 12/5/16.
 */
public class PayerCostRow implements PayerCostViewController {

    private PayerCost mPayerCost;
    private String mCurrencyId;

    private Context mContext;
    private View mView;
    private MPTextView mInstallmentsTextView;
    private MPTextView mZeroRateText;
    private MPTextView mRateText;

    public PayerCostRow(Context context, String currencyId) {
        this.mContext = context;
        this.mCurrencyId = currencyId;

    }

    @Override
    public void drawPayerCost(PayerCost payerCost) {
        mPayerCost = payerCost;
        setInstallmentsText();

        if (payerCost.getInstallmentRate().compareTo(BigDecimal.ZERO) == 0) {
            mRateText.setVisibility(View.GONE);
            if (payerCost.getInstallments() > 1) {
                mZeroRateText.setVisibility(View.VISIBLE);
            }
        } else {
            setAmountWithRateText();
        }
    }

    private void setAmountWithRateText() {
        mRateText.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getTotalAmount(), mCurrencyId));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getTotalAmount(),
                mCurrencyId, sb.toString(), false, true);
        mRateText.setText(spannedFullAmountText);
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
        mRateText = (MPTextView) mView.findViewById(R.id.mpsdkInstallmentsWithRate);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_payer_cost_list, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }
}
