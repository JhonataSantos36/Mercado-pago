package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodSearchLargeRow implements PaymentMethodSearchViewController {

    private Context mContext;
    private View mView;
    private View mSeparator;
    private MPTextView mDescription;
    private MPTextView mComment;
    private ImageView mIcon;

    public PaymentMethodSearchLargeRow(Context context) {
        mContext = context;
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_pm_search_item_large, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    public void initializeControls() {
        mDescription = (MPTextView) mView.findViewById(R.id.description);
        mComment = (MPTextView) mView.findViewById(R.id.comment);
        mIcon = (ImageView) mView.findViewById(R.id.image);
        mSeparator = mView.findViewById(R.id.separator);
    }

    public void drawPaymentMethod(PaymentMethodSearchItem item) {
        if(item.hasDescription()) {
            mDescription.setText(item.getDescription());
        }
        if(item.hasComment()) {
            mComment.setText(item.getComment());
        }
        int resourceId = 0;

        if(item.isIconRecommended()) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, item.getId());
        }

        if(resourceId != 0) {
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
