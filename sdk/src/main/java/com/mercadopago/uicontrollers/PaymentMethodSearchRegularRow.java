package com.mercadopago.uicontrollers;

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
public class PaymentMethodSearchRegularRow implements PaymentMethodViewController {

    public Context mContext;
    private MPTextView mDescription;
    private MPTextView mComment;
    private ImageView mIcon;
    private View mView;

    public PaymentMethodSearchRegularRow(Context context) {
        mContext = context;
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
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mDescription = (MPTextView) mView.findViewById(R.id.description);
        mComment = (MPTextView) mView.findViewById(R.id.comment);
        mIcon = (ImageView) mView.findViewById(R.id.image);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_pm_search_item, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
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
