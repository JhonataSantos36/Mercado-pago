package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Card;
import com.mercadopago.uicontrollers.paymentmethods.card.PaymentMethodOnSelectionRow;

import java.util.List;

public class CustomerCardsAdapter extends RecyclerView.Adapter<CustomerCardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Card> mData;
    private OnSelectedCallback<Card> mSelectionCallback;

    public CustomerCardsAdapter(Context context, List<Card> data, OnSelectedCallback<Card> callback) {
        mContext = context;
        mData = data;
        mSelectionCallback = callback;
    }

    @Override
    public CustomerCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int position) {

        Card card = mData.get(position);

        PaymentMethodOnSelectionRow paymentMethodCardSelectionRow = new PaymentMethodOnSelectionRow(mContext, card);

        paymentMethodCardSelectionRow.inflateInParent(parent, false);

        return new ViewHolder(paymentMethodCardSelectionRow, card);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPaymentMethodCardRow.drawPaymentMethod();
        if (position != mData.size() - 1) {
            holder.mPaymentMethodCardRow.showSeparator();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Card mCard;
        public PaymentMethodOnSelectionRow mPaymentMethodCardRow;

        public ViewHolder(PaymentMethodOnSelectionRow paymentMethodCardRow, Card card) {
            super(paymentMethodCardRow.getView());
            mCard = card;
            mPaymentMethodCardRow = paymentMethodCardRow;

            mPaymentMethodCardRow.initializeControls();
            mPaymentMethodCardRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectionCallback.onSelected(mCard);
                }
            });
        }
    }
}