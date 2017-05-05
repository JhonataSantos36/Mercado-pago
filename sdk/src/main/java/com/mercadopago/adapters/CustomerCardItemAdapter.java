package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Card;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtil;

import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

public class CustomerCardItemAdapter extends RecyclerView.Adapter<CustomerCardItemAdapter.ViewHolder> {

    private static final int ITEM_TYPE_CARD = 0;
    private static final int ITEM_TYPE_MESSAGE = 1;

    private List<Card> mCards;
    private String mActionMessage;
    private Context mContext;
    private OnSelectedCallback<Card> mOnSelectedCallback;

    public CustomerCardItemAdapter(Context context, List<Card> cards, String actionMessage, OnSelectedCallback<Card> onSelectedCallback) {
        mContext = context;
        mCards = cards;
        mActionMessage = actionMessage;
        mOnSelectedCallback = onSelectedCallback;
    }

    @Override
    public CustomerCardItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == ITEM_TYPE_CARD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mpsdk_row_pm_search_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mpsdk_custom_text_row, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (isActionMessageItem(position)) {
            viewType = ITEM_TYPE_MESSAGE;
        } else {
            viewType = ITEM_TYPE_CARD;
        }

        return viewType;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (isActionMessageItem(position)) {
            viewHolder.bind(mActionMessage);
        } else {
            viewHolder.bind(mCards.get(position));
        }
    }

    private boolean isActionMessageItem(int position) {
        return position == mCards.size();
    }

    @Override
    public int getItemCount() {
        return TextUtil.isEmpty((mActionMessage)) ? mCards.size() : mCards.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MPTextView mDescription;
        private ImageView mIcon;
        private View mView;
        private Card mCard;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescription = (MPTextView) view.findViewById(R.id.mpsdkDescription);
            mIcon = (ImageView) view.findViewById(R.id.mpsdkImage);
        }

        private void bind(Card card) {
            mCard = card;

            setCardDescription(card);
            setIcon(card);
            setListener();
        }

        private void bind(String actionMessage) {
            setListener();

            mDescription.setText(actionMessage);
        }

        private void setListener() {
            if (mOnSelectedCallback != null) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnSelectedCallback.onSelected(mCard);
                    }
                });
            }
        }

        private void setCardDescription(Card card) {
            String description;

            if (!isEmpty(card.getLastFourDigits())) {
                description = mContext.getString(R.string.mpsdk_last_digits_label) + "\n" + card.getLastFourDigits();

                mDescription.setText(description);
            } else {
                mDescription.setVisibility(View.GONE);
            }
        }

        private void setIcon(Card card) {
            String imageId;
            int resourceId = 0;

            imageId = card.getPaymentMethod().getId();
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, imageId);

            if (resourceId != 0) {
                mIcon.setImageResource(resourceId);
            } else {
                mIcon.setVisibility(View.GONE);
            }
        }
    }
}