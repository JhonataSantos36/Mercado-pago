package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.views.PaymentMethodRow;
import com.mercadopago.views.ViewFactory;

import java.util.List;

/**
 * Created by mreverter on 18/1/16.
 */
public class PaymentMethodSearchItemAdapter extends RecyclerView.Adapter<PaymentMethodSearchItemAdapter.ViewHolder>{

    private Context mContext;
    private List<PaymentMethodSearchItem> mItems;
    private OnSelectedCallback<PaymentMethodSearchItem> mCallback;

    public PaymentMethodSearchItemAdapter(Context context, List<PaymentMethodSearchItem> items, OnSelectedCallback<PaymentMethodSearchItem> callback)
    {
        this.mContext = context;
        this.mItems = items;
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        PaymentMethodSearchItem item = mItems.get(position);

        PaymentMethodRow paymentMethodSearchRow = ViewFactory.getPaymentMethodSearchItemRow(item, mContext);

        paymentMethodSearchRow.inflateInParent(parent);

        return new ViewHolder(paymentMethodSearchRow);
    }

    public Integer getHeightForItems() {
        Float mCurrentHeight = (float) 0;
        for(PaymentMethodSearchItem item : mItems) {
            if (withLargeRow(item)) {
                mCurrentHeight += mContext.getResources().getDimension(R.dimen.list_item_height_large);
            } else {
                mCurrentHeight += mContext.getResources().getDimension(R.dimen.list_item_height);
            }
        }
        return Math.round(mCurrentHeight);
    }

    private boolean withLargeRow(PaymentMethodSearchItem item) {
        return item.hasComment();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethodSearchItem paymentMethodSearchItem = mItems.get(position);

        holder.mPaymentMethodSearchRow.setFields(paymentMethodSearchItem);

        holder.mItem = mItems.get(position);
        if(position == mItems.size()-1) {
            holder.mSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PaymentMethodSearchItem mItem;
        private View mSeparator;
        private PaymentMethodRow mPaymentMethodSearchRow;

        public ViewHolder(PaymentMethodRow row) {
            super(row.getView());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSelected(mItem);
                }
            });
            mPaymentMethodSearchRow = row;
            mSeparator = itemView.findViewById(R.id.separator);
            row.initializeControls();
        }
    }
}
