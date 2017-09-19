package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.model.Item;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewProductView;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewProductViewController;

import java.util.List;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewProductAdapter extends RecyclerView.Adapter<ReviewProductAdapter.ViewHolder> {

    private DecorationPreference mDecorationPreference;
    private List<Item> mItemList;
    private String mCurrency;
    private Context mContext;
    private ReviewScreenPreference mReviewScreenPreference;

    public ReviewProductAdapter(Context context, List<Item> items, String currency, ReviewScreenPreference reviewScreenPreference, DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mItemList = items;
        this.mCurrency = currency;
        this.mReviewScreenPreference = reviewScreenPreference;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReviewProductViewController productViewController = new ReviewProductView(mContext, mReviewScreenPreference);
        productViewController.inflateInParent(parent, false);
        return new ReviewProductAdapter.ViewHolder(productViewController);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(ReviewProductAdapter.ViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.mReviewProductViewController.drawProduct(position, item, mCurrency, mDecorationPreference);
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
