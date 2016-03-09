package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CardInstallmentsAdapter extends  RecyclerView.Adapter<CardInstallmentsAdapter.ViewHolder> {


    private Context mContext;
    private List<PayerCost> mInstallmentsList;

    public CardInstallmentsAdapter(Context context) {
        this.mContext = context;
        this.mInstallmentsList = new ArrayList<>();
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
        View adapterView = inflater.inflate(R.layout.adapter_installments, parent, false);
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
        //TODO no vamos a tener currency
        sb.append(CurrenciesUtil.formatNumber(payerCost.getInstallmentAmount(), "MXN"));
        return CurrenciesUtil.formatCurrencyInText(payerCost.getInstallmentAmount(),
                "MXN", sb.toString(), true, true);
    }

    private Spanned getInstallmentsRateText(PayerCost payerCost) {
        StringBuffer sb = new StringBuffer();
        sb.append("( ");
        sb.append(CurrenciesUtil.formatNumber(payerCost.getTotalAmount(), "MXN"));
        sb.append(" )");
        return CurrenciesUtil.formatCurrencyInText(payerCost.getTotalAmount(),
                "MXN", sb.toString(), true, true);
    }

    public PayerCost getItem(int position) {
        return mInstallmentsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mInstallmentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public MPTextView mInstallmentsTextView;
        public MPTextView mZeroRateTextView;
        public MPTextView mRateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mInstallmentsTextView = (MPTextView) itemView.findViewById(R.id.adapter_installments_text);
            mZeroRateTextView = (MPTextView) itemView.findViewById(R.id.adapter_installments_zero_rate);
            mRateTextView = (MPTextView) itemView.findViewById(R.id.adapter_installments_with_rate);
        }
    }


}
