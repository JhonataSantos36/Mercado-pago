package com.mercadopago.uicontrollers.paymentmethods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodOffEditableRow implements PaymentMethodViewController {

    private PaymentMethodSearchItem mItem;
    private Context mContext;
    private View mSeparator;
    private View mView;
    private MPTextView mDescription;
    private MPTextView mComment;
    private ImageView mIcon;
    private ImageView mEditImage;

    public PaymentMethodOffEditableRow(Context context, PaymentMethodSearchItem item) {
        mContext = context;
        mItem = item;
    }

    @Override
    public void drawPaymentMethod() {
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
        mEditImage.setVisibility(View.VISIBLE);
        mEditImage.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mComment = (MPTextView) mView.findViewById(R.id.comment);
        mDescription = (MPTextView) mView.findViewById(R.id.description);
        mIcon = (ImageView) mView.findViewById(R.id.image);
        mEditImage = (ImageView) mView.findViewById(R.id.imageEdit);
        mSeparator = mView.findViewById(R.id.separator);
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
