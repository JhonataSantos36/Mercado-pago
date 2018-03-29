package com.mercadopago.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder> {

    private final Activity mActivity;
    private final List<PaymentMethod> mData;
    private View.OnClickListener mListener = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPaymentMethodImage;
        public TextView mPaymentMethodName;

        public ViewHolder(View v, View.OnClickListener listener) {

            super(v);
            mPaymentMethodImage = v.findViewById(R.id.mpsdkPmImage);
            mPaymentMethodName = v.findViewById(R.id.mpsdkPmName);
            if (listener != null) {
                v.setOnClickListener(listener);
            }
        }
    }

    public PaymentMethodsAdapter(Activity activity, List<PaymentMethod> data, View.OnClickListener listener) {

        mActivity = activity;
        mData = data;
        mListener = listener;
    }

    @Override
    public PaymentMethodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mpsdk_row_simple_list, parent, false);

        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PaymentMethod paymentMethod = mData.get(position);

        holder.mPaymentMethodImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(mActivity, paymentMethod.getId()));
        holder.mPaymentMethodName.setText(paymentMethod.getName());
        // Set view tag item
        holder.itemView.setTag(paymentMethod);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public PaymentMethod getItem(int position) {
        return mData.get(position);
    }

    public View.OnClickListener getListener() {
        return mListener;
    }
}