package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

/**
 * Created by vaserber on 8/17/17.
 */
public class ReviewPaymentMethodsAdapter extends RecyclerView.Adapter<ReviewPaymentMethodsAdapter.ViewHolder> {

    private final List<PaymentMethod> mPaymentMethods;

    public ReviewPaymentMethodsAdapter(List<PaymentMethod> paymentMethods) {
        mPaymentMethods = paymentMethods;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mpsdk_review_payment_method, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethod paymentMethod = mPaymentMethods.get(position);
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(holder.itemView.getContext(), paymentMethod.getId());
        if (resourceId != 0) {
            holder.mPaymentMethodImage.setImageResource(resourceId);
        }
        holder.mPaymentMethodName.setText(paymentMethod.getName());
        // Set view tag item
        holder.itemView.setTag(paymentMethod);
    }

    @Override
    public int getItemCount() {
        return mPaymentMethods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mPaymentMethodImage;
        private final MPTextView mPaymentMethodName;


        public ViewHolder(View v) {
            super(v);
            mPaymentMethodImage = v.findViewById(R.id.mpsdkPaymentMethodImage);
            mPaymentMethodName = v.findViewById(R.id.mpsdkPaymentMethodName);
        }
    }
}
