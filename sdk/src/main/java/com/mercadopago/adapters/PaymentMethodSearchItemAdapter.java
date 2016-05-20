package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;
import com.mercadopago.uicontrollers.ViewControllerFactory;

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

        PaymentMethodSearchViewController paymentMethodSearchRow = ViewControllerFactory.getPaymentMethodSelectionViewController(item, mContext);

        paymentMethodSearchRow.inflateInParent(parent, false);

        return new ViewHolder(paymentMethodSearchRow);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethodSearchItem paymentMethodSearchItem = mItems.get(position);

        holder.mPaymentMethodSearchViewController.drawPaymentMethod(paymentMethodSearchItem);

        holder.mItem = mItems.get(position);
        if(position != mItems.size()-1) {
            holder.mPaymentMethodSearchViewController.showSeparator();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PaymentMethodSearchItem mItem;
        private PaymentMethodSearchViewController mPaymentMethodSearchViewController;

        public ViewHolder(PaymentMethodSearchViewController paymentMethodViewController) {
            super(paymentMethodViewController.getView());
            mPaymentMethodSearchViewController = paymentMethodViewController;

            mPaymentMethodSearchViewController.initializeControls();
            mPaymentMethodSearchViewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSelected(mItem);
                }
            });
        }
    }
}
