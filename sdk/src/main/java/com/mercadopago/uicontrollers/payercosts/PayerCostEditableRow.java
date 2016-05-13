package com.mercadopago.uicontrollers.payercosts;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;

/**
 * Created by mreverter on 12/5/16.
 */
public class PayerCostEditableRow implements PayerCostViewController {

    private PayerCost mPayerCost;
    private String mCurrencyId;

    private Context mContext;
    private View mView;
    private TextView mInstallmentsTextView;
    private TextView mZeroRateText;
    private TextView mRateText;
    private ImageView mEditImage;

    public PayerCostEditableRow(Context context, String currencyId) {
        this.mContext = context;
        mCurrencyId = currencyId;
    } 
    
    @Override
    public void drawPayerCost(PayerCost payerCost) {
        mPayerCost = payerCost;
        setInstallmentsText();

        if(payerCost.getInstallmentRate().compareTo(BigDecimal.ZERO) == 0) {
            mRateText.setVisibility(View.GONE);
            mZeroRateText.setVisibility(View.VISIBLE);
        }
        else {
            setAmountWithRateText();
        }
    }

    private void setAmountWithRateText() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getTotalAmount(), mCurrencyId));
        sb.append(" )");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getTotalAmount(),
                mCurrencyId, sb.toString(), true, true);
        mRateText.setText(spannedFullAmountText);
    }

    private void setInstallmentsText() {
        StringBuilder sb = new StringBuilder();
        sb.append(mPayerCost.getInstallments());
        sb.append(" ");
        sb.append(mContext.getString(R.string.mpsdk_installments_of));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getInstallmentAmount(), mCurrencyId));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getInstallmentAmount(),
                mCurrencyId, sb.toString(), true, true);
        mInstallmentsTextView.setText(spannedInstallmentsText);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mEditImage.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mInstallmentsTextView = (TextView) mView.findViewById(R.id.installments_text);
        mZeroRateText = (TextView) mView.findViewById(R.id.installments_zero_rate);
        mRateText = (TextView) mView.findViewById(R.id.installments_with_rate);
        mEditImage = (ImageView) mView.findViewById(R.id.imageEdit);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_payer_cost_edit, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }
}
