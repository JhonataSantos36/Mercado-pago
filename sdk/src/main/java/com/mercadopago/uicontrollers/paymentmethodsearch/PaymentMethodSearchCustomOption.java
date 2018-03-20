package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 10/25/16.
 */

public class PaymentMethodSearchCustomOption implements PaymentMethodSearchViewController {
    protected CustomSearchItem mItem;
    protected Context mContext;
    protected View mView;
    protected MPTextView mDescription;
    protected MPTextView mComment;
    protected ImageView mIcon;
    protected View.OnClickListener mListener;

    public PaymentMethodSearchCustomOption(Context context, CustomSearchItem item) {
        mContext = context;
        mItem = item;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_pm_search_item, parent, attachToRoot);
        if (mListener != null) {
            mView.setOnClickListener(mListener);
        }
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void initializeControls() {
        mDescription = mView.findViewById(R.id.mpsdkDescription);
        mComment = mView.findViewById(R.id.mpsdkComment);
        mIcon = mView.findViewById(R.id.mpsdkImage);
    }

    @Override
    public void draw() {

        mDescription.setText(mItem.getDescription());

        int resourceId = 0;

        if (!TextUtils.isEmpty(mItem.getPaymentMethodId())) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, mItem.getPaymentMethodId());
        }

        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }

        mComment.setVisibility(View.GONE);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
        if (mView != null) {
            mView.setOnClickListener(listener);
        }
    }
}
