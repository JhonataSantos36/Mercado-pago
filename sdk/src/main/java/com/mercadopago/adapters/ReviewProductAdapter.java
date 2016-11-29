package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.model.Item;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewProductView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewProductViewController;

import java.util.List;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewProductAdapter extends RecyclerView.Adapter<ReviewProductAdapter.ViewHolder> {

    private List<Item> mItemList;
    private List<String> mCurrenciesList;
    private Context mContext;

    public ReviewProductAdapter(Context context, List<Item> items, List<String> currencies) {
        this.mContext = context;
        this.mItemList = items;
        this.mCurrenciesList = currencies;
    }

    @Override
    public ReviewProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        Item item = mItemList.get(position);
        String currencyId = mCurrenciesList.get(position);
        ReviewProductViewController productViewController = new ReviewProductView(mContext, item, currencyId);
        productViewController.inflateInParent(parent, false);
        return new ReviewProductAdapter.ViewHolder(productViewController);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(ReviewProductAdapter.ViewHolder holder, int position) {
        holder.mReviewProductViewController.drawProduct(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ReviewProductViewController mReviewProductViewController;

        public ViewHolder(ReviewProductViewController reviewProductViewController) {
            super(reviewProductViewController.getView());
            mReviewProductViewController = reviewProductViewController;
            mReviewProductViewController.initializeControls();
        }
    }
}
