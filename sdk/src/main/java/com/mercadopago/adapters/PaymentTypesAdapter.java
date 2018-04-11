package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.model.PaymentType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */
public class PaymentTypesAdapter extends RecyclerView.Adapter<PaymentTypesAdapter.ViewHolder> {

    private final List<PaymentType> mPaymentTypes;
    private final OnSelectedCallback<Integer> mCallback;

    public PaymentTypesAdapter(OnSelectedCallback<Integer> callback) {
        mPaymentTypes = new ArrayList<>();
        mCallback = callback;
    }

    public void addResults(List<PaymentType> list) {
        mPaymentTypes.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mPaymentTypes.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adapterView = inflater.inflate(R.layout.mpsdk_adapter_payment_types, parent, false);
        ViewHolder viewHolder = new ViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentType paymentType = mPaymentTypes.get(position);
        holder.mPaymentTypeIdTextView.setText(paymentTypeName(paymentType, holder.mPaymentTypeIdTextView.getContext()));
    }

    public String paymentTypeName(final PaymentType paymentType,
                                  final Context context) {
        String ans = "";
        if (paymentType.getId().equals(PaymentTypes.CREDIT_CARD)) {
            ans = context.getString(R.string.mpsdk_credit_payment_type);
        } else if (paymentType.getId().equals(PaymentTypes.DEBIT_CARD)) {
            ans = context.getString(R.string.mpsdk_debit_payment_type);
        } else if (paymentType.getId().equals(PaymentTypes.PREPAID_CARD)) {
            ans = context.getString(R.string.mpsdk_form_card_title_payment_type_prepaid);
        }
        return ans;
    }


    public PaymentType getItem(int position) {
        return mPaymentTypes.get(position);
    }

    @Override
    public int getItemCount() {
        return mPaymentTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MPTextView mPaymentTypeIdTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPaymentTypeIdTextView = itemView.findViewById(R.id.mpsdkPaymentTypeTextView);
            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

}
