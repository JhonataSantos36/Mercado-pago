package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.PaymentMethodSearchCallback;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.util.List;

/**
 * Created by mreverter on 18/1/16.
 */
public class PaymentMethodSearchItemAdapter extends RecyclerView.Adapter<PaymentMethodSearchItemAdapter.ViewHolder>{

    private Context mContext;
    private List<PaymentMethodSearchItem> mItems;
    private PaymentMethodSearchCallback mCallback;

    public PaymentMethodSearchItemAdapter(Context context, List<PaymentMethodSearchItem> items, PaymentMethodSearchCallback callback)
    {
        this.mContext = context;
        this.mItems = items;
        this.mCallback = callback;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        PaymentMethodSearchItem item = mItems.get(position);
        View view = getViewForItem(parent, item);
        return new ViewHolder(view);
    }

    private View getViewForItem(ViewGroup parent, PaymentMethodSearchItem item) {
        View view;
        if(itemNeedsDescription(item)) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_pm_search_item, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_pm_search_item_large, parent, false);
        }
        return view;
    }

    public Integer getHeightForItems() {
        Float mCurrentHeight = (float) 0;
        for(PaymentMethodSearchItem item : mItems) {
            if (itemNeedsDescription(item)) {
                mCurrentHeight += mContext.getResources().getDimension(R.dimen.list_item_height);
            } else {
                mCurrentHeight += mContext.getResources().getDimension(R.dimen.list_item_height_large);
            }
        }
        return Math.round(mCurrentHeight);
    }

    private boolean itemNeedsDescription(PaymentMethodSearchItem item) {
        return !item.getType().equals("payment_method") || item.getId().equals("bitcoin");
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethodSearchItem paymentMethodSearchItem = mItems.get(position);

        if(holder.mDescription != null) {
            holder.mDescription.setText(paymentMethodSearchItem.getDescription());
        }
        if(paymentMethodSearchItem.getComment() != null && !paymentMethodSearchItem.getId().equals("bitcoin")) {
            holder.mComment.setText(paymentMethodSearchItem.getComment());
        }
        else {
            holder.mComment.setVisibility(View.GONE);
        }

        Integer resourceId;

        if(paymentMethodSearchItem.getId() != null) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, paymentMethodSearchItem.getId());
        }
        else {
            resourceId = 0;
        }

        if(resourceId != 0) {
            holder.mIcon.setImageResource(resourceId);
            if(itemNeedsTint(paymentMethodSearchItem)) {
                setTintColor(mContext, holder.mIcon);
            }
        } else {
            holder.mIcon.setVisibility(View.GONE);
        }

        holder.mItem = paymentMethodSearchItem;

        if(position == mItems.size()-1) {
            holder.mSeparator.setVisibility(View.GONE);
        }
    }

    private boolean itemNeedsTint(PaymentMethodSearchItem paymentMethodSearchItem) {

        return paymentMethodSearchItem.getType().equals("group") || paymentMethodSearchItem.getType().equals("payment_type") || paymentMethodSearchItem.getId().equals("bitcoin");
    }

    private void setTintColor(Context mContext, ImageView mIcon) {
        mIcon.setColorFilter(mContext.getResources().getColor(R.color.mpsdk_icon_image_color));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MPTextView mDescription;
        private MPTextView mComment;
        private ImageView mIcon;
        private View mSeparator;
        private PaymentMethodSearchItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextStep(mItem);
                }
            });

            mDescription = (MPTextView) itemView.findViewById(R.id.title);
            mComment = (MPTextView) itemView.findViewById(R.id.comment);
            mIcon = (ImageView) itemView.findViewById(R.id.image);
            mSeparator = itemView.findViewById(R.id.separator);
        }

        private void nextStep(PaymentMethodSearchItem mItem) {
            switch (mItem.getType()) {
                case "group":
                    mCallback.onGroupItemClicked(mItem);
                    break;
                case "payment_type":
                    mCallback.onPaymentTypeItemClicked(mItem);
                    break;
                case "payment_method":
                    mCallback.onPaymentMethodItemClicked(mItem);
                    break;
            }
        }
    }
}
