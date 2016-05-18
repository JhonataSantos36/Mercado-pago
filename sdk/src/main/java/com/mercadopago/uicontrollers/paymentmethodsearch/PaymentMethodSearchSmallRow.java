package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodSearchSmallRow extends PaymentMethodSearchRow {

    public PaymentMethodSearchSmallRow(Context context) {
        super(context);
    }

    @Override
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
            if(itemNeedsTint(item)) {
                setTintColor(mContext, mIcon);
            }
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_pm_search_item, parent, attachToRoot);
        return mView;
    }

    private void setTintColor(Context mContext, ImageView mIcon) {
        mIcon.setColorFilter(mContext.getResources().getColor(R.color.mpsdk_icon_image_color));
    }

    private boolean itemNeedsTint(PaymentMethodSearchItem paymentMethodSearchItem) {

        return paymentMethodSearchItem.isGroup()
                || paymentMethodSearchItem.isPaymentType()
                || paymentMethodSearchItem.getId().equals("bitcoin");
    }
}
