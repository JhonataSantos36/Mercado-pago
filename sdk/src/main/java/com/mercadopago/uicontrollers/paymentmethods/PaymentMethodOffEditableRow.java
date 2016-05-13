package com.mercadopago.uicontrollers.paymentmethods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodOffEditableRow implements PaymentMethodViewController {

    private PaymentMethod mPaymentMethod;
    private PaymentMethodSearchItem mItem;
    private Context mContext;
    private View mSeparator;
    private View mView;
    private MPTextView mDescription;
    private MPTextView mComment;
    private ImageView mIcon;
    private View mEditHint;

    public PaymentMethodOffEditableRow(Context context, PaymentMethodSearchItem item) {
        mContext = context;
        mItem = item;
    }

    public PaymentMethodOffEditableRow(Context context, PaymentMethod paymentMethod) {
        mContext = context;
        mPaymentMethod = paymentMethod;
    }

    @Override
    public void drawPaymentMethod() {
        if(mItem != null) {
            drawWithSearchItem();
        } else if(mPaymentMethod != null) {
            drawWithPaymentMethod();
        }
    }

    private void drawWithPaymentMethod() {

        mComment.setText(MercadoPagoUtil.getAccreditationTimeMessage(mContext, mPaymentMethod.getAccreditationTime()));
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(mContext, mPaymentMethod.getId());

        if(resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    private void drawWithSearchItem() {
        if(mItem.hasDescription()) {
            mDescription.setText(mItem.getDescription());
        }
        if(mItem.hasComment()) {
            mComment.setText(mItem.getComment());
        }
        int resourceId = 0;

        if(mItem.isIconRecommended()) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, mItem.getId());
        }

        if(resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mEditHint.setVisibility(View.VISIBLE);
        mView.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mComment = (MPTextView) mView.findViewById(R.id.comment);
        mDescription = (MPTextView) mView.findViewById(R.id.description);
        mIcon = (ImageView) mView.findViewById(R.id.image);
        mEditHint = mView.findViewById(R.id.editHint);
        mSeparator = mView.findViewById(R.id.separator);

        mEditHint.setVisibility(View.GONE);
        mSeparator.setVisibility(View.GONE);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_payment_method_edit_large, parent, attachToRoot);
        return mView;
    }

    @Override
    public void showSeparator() {
        mSeparator.setVisibility(View.VISIBLE);
    }

    @Override
    public View getView() {
        return mView;
    }
}
