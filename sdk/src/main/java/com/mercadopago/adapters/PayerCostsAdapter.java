package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayerCostsAdapter extends  RecyclerView.Adapter<PayerCostsAdapter.ViewHolder> {

    private Context mContext;
    private List<PayerCost> mInstallmentsList;
    private String mCurrencyId;
    private OnSelectedCallback<Integer> mCallback;

    public PayerCostsAdapter(Context context, String currency, OnSelectedCallback<Integer> callback) {
        this.mContext = context;
        this.mCurrencyId = currency;
        this.mInstallmentsList = new ArrayList<>();
        this.mCallback = callback;
    }

    public void addResults(List<PayerCost> list) {
        mInstallmentsList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mInstallmentsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.mpsdk_row_payer_cost_edit, parent, false);
        ViewHolder viewHolder = new ViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PayerCost payerCost = mInstallmentsList.get(position);

        holder.mInstallmentsTextView.setText(getInstallmentsText(payerCost));

        if (payerCost.getInstallmentRate().equals(BigDecimal.ZERO)) {
            holder.mRateTextView.setVisibility(View.GONE);
            if (payerCost.getInstallments() != 1) {
                holder.mZeroRateTextView.setVisibility(View.VISIBLE);
            } else {
                holder.mZeroRateTextView.setVisibility(View.GONE);
            }
        } else {
            holder.mZeroRateTextView.setVisibility(View.GONE);
            holder.mRateTextView.setText(getInstallmentsRateText(payerCost));
            holder.mRateTextView.setVisibility(View.VISIBLE);
        }

    }

    private Spanned getInstallmentsText(PayerCost payerCost) {
        StringBuffer sb = new StringBuffer();
        sb.append(payerCost.getInstallments());
        sb.append(" ");
        sb.append(mContext.getString(R.string.mpsdk_installments_of));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(payerCost.getInstallmentAmount(), mCurrencyId));
        return CurrenciesUtil.formatCurrencyInText(payerCost.getInstallmentAmount(),
                mCurrencyId, sb.toString(), true, true);
    }

    private Spanned getInstallmentsRateText(PayerCost payerCost) {
        StringBuffer sb = new StringBuffer();
        sb.append("( ");
        sb.append(CurrenciesUtil.formatNumber(payerCost.getTotalAmount(), mCurrencyId));
        sb.append(" )");
        return CurrenciesUtil.formatCurrencyInText(payerCost.getTotalAmount(),
                mCurrencyId, sb.toString(), true, true);
    }

    public PayerCost getItem(int position) {
        return mInstallmentsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mInstallmentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MPTextView mInstallmentsTextView;
        public MPTextView mZeroRateTextView;
        public MPTextView mRateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mInstallmentsTextView = (MPTextView) itemView.findViewById(R.id.mpsdkInstallmentsText);
            mZeroRateTextView = (MPTextView) itemView.findViewById(R.id.mpsdkInstallmentsZeroRate);
            mRateTextView = (MPTextView) itemView.findViewById(R.id.mpsdkInstallmentsWithRate);

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }


}
