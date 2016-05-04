package com.mercadopago.uicontrollers;

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

    private Context mContext;
    private View mView;
    private MPTextView mDescription;
    private MPTextView mComment;
    private ImageView mIcon;
    private ImageView mEditImage;

    public PaymentMethodOffEditableRow(Context context) {
        mContext = context;
    }

    @Override
    public void drawPaymentMethod(PaymentMethodSearchItem item) {
        if(item.hasDescription()) {
            mDescription.setText(item.getDescription());
        }
        else {
            mDescription.setText("");
        }
        if(item.hasComment()) {
            mComment.setText(item.getComment());
        }
        else {
            mComment.setVisibility(View.GONE);
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
    public void drawPaymentMethod(PaymentMethod paymentMethod) {
        String accreditationTimeMessage = MercadoPagoUtil.getAccreditationTimeMessage(paymentMethod.getAccreditationTime(), mContext);
        mComment.setText(accreditationTimeMessage);

        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(mContext, paymentMethod.getId());

        if(resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mEditImage.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mDescription = (MPTextView) mView.findViewById(R.id.description);
        mComment = (MPTextView) mView.findViewById(R.id.comment);
        mIcon = (ImageView) mView.findViewById(R.id.image);
        mEditImage = (ImageView) mView.findViewById(R.id.imageEdit);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_payment_method_edit_large, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }
}
