package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.model.Reviewable;
import com.mercadopago.uicontrollers.CustomViewController;

import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public class ReviewablesAdapter extends RecyclerView.Adapter<ReviewablesAdapter.ViewHolder>{
    private List<Reviewable> mReviewables;

    public ReviewablesAdapter(List<Reviewable> reviewables) {
        mReviewables = reviewables;
    }

    @Override
    public ReviewablesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        Reviewable reviewable = mReviewables.get(position);
        reviewable.inflateInParent(parent, false);
        return new ReviewablesAdapter.ViewHolder(reviewable);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ReviewablesAdapter.ViewHolder holder, int position) {
        Reviewable reviewable = mReviewables.get(position);
        reviewable.draw();
    }

    @Override
    public int getItemCount() {
        return mReviewables.size();
    }

    public void clear() {
        int size = this.mReviewables.size();
        this.mReviewables.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomViewController mViewController;

        public ViewHolder(Reviewable reviewable) {
            super(reviewable.getView());
            mViewController = reviewable;
            mViewController.initializeControls();
        }
    }
}
