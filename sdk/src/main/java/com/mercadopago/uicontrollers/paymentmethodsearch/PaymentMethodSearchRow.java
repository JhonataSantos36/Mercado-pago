package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodSearchRow implements PaymentMethodSearchViewController {

    protected Context mContext;
    protected View mView;
    protected View mSeparator;
    protected MPTextView mDescription;
    protected MPTextView mComment;
    protected ImageView mIcon;

    public PaymentMethodSearchRow(Context context) {
        mContext = context;
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_pm_search_item_large, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    public void initializeControls() {
        mDescription = (MPTextView) mView.findViewById(R.id.mpsdkDescription);
        mComment = (MPTextView) mView.findViewById(R.id.mpsdkComment);
        mIcon = (ImageView) mView.findViewById(R.id.mpsdkImage);
        mSeparator = mView.findViewById(R.id.mpsdkSeparator);

        mSeparator.setVisibility(View.GONE);
    }

    public void drawPaymentMethod(PaymentMethodSearchItem item) {
        if (item.hasDescription()) {
            mDescription.setText(item.getDescription());
        }
        if (item.hasComment()) {
            mComment.setText(item.getComment());
        }
        int resourceId = 0;

        if (item.isIconRecommended()) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, item.getId());
        }

        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSeparator() {
        mSeparator.setVisibility(View.VISIBLE);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }
}
