package com.mercadopago.adapters;

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
import java.util.Locale;

import static com.mercadopago.util.TextUtil.isEmpty;

public class CustomerCardItemAdapter extends RecyclerView.Adapter<CustomerCardItemAdapter.ViewHolder> {

    private static final int ITEM_TYPE_CARD = 0;
    private static final int ITEM_TYPE_MESSAGE = 1;

    private final List<Card> mCards;
    private final String mActionMessage;
    private final OnSelectedCallback<Card> mOnSelectedCallback;

    public CustomerCardItemAdapter(List<Card> cards, String actionMessage, OnSelectedCallback<Card> onSelectedCallback) {
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

        private final MPTextView mDescription;
        private final ImageView mIcon;
        private final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescription = view.findViewById(R.id.mpsdkDescription);
            mIcon = view.findViewById(R.id.mpsdkImage);
        }

        private void bind(Card card) {
            setCardDescription(card);
            setIcon(card);
            setListener(card);
        }

        private void bind(String actionMessage) {
            //TODO refactor - The same viewholder its used for trigger 2 kind
            //TODO of actions, it should be separated
            //TODO if not, logic should be added inside mOnSelectedCallback.onSelected implementation
            setListener(null);
            mDescription.setText(actionMessage);
        }

        private void setListener(final Card card) {
            if (mOnSelectedCallback != null) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnSelectedCallback.onSelected(card);
                    }
                });
            }
        }

        private void setCardDescription(Card card) {

            if (!isEmpty(card.getLastFourDigits())) {
                String digitsLabel = itemView.getContext().getString(R.string.mpsdk_last_digits_label);
                String formattedDigitsLabel = String.format(Locale.getDefault(), "%s%s%s",
                        digitsLabel, "\n", card.getLastFourDigits());
                mDescription.setText(formattedDigitsLabel);
            } else {
                mDescription.setVisibility(View.GONE);
            }
        }

        private void setIcon(Card card) {
            String imageId;
            int resourceId = 0;

            imageId = card.getPaymentMethod().getId();
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(itemView.getContext(), imageId);

            if (resourceId != 0) {
                mIcon.setImageResource(resourceId);
            } else {
                mIcon.setVisibility(View.GONE);
            }
        }
    }
}