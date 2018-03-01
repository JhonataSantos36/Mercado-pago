package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.uicontrollers.CustomViewController;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 18/1/16.
 */
public class PaymentMethodSearchItemAdapter extends RecyclerView.Adapter<PaymentMethodSearchItemAdapter.ViewHolder> {

    private List<PaymentMethodSearchViewController> mItems;

    public PaymentMethodSearchItemAdapter() {
        mItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        CustomViewController item = mItems.get(position);
        item.inflateInParent(parent, false);
        return new ViewHolder(item);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethodSearchViewController viewController = mItems.get(position);
        viewController.draw();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(List<PaymentMethodSearchViewController> items) {
        mItems.addAll(items);
    }

    public void clear() {
        int size = this.mItems.size();
        this.mItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void notifyItemInserted() {
        notifyItemInserted(mItems.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomViewController mViewController;

        public ViewHolder(CustomViewController viewController) {
            super(viewController.getView());
            mViewController = viewController;
            mViewController.initializeControls();
        }
    }
}
