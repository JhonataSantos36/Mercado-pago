package com.mercadopago.uicontrollers.paymentmethods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

/**
 * Created by mreverter on 12/5/16.
 */
public class PaymentMethodCardEditableRow implements PaymentMethodViewController {

    private PaymentMethod mPaymentMethod;
    private Token mToken;
    private Context mContext;
    private View mSeparator;
    private View mView;
    private MPTextView mDescription;
    private ImageView mIcon;
    private View mEditHint;

    public PaymentMethodCardEditableRow(Context context, PaymentMethod paymentMethod, Token token) {
        mContext = context;
        mPaymentMethod = paymentMethod;
        mToken = token;
    }

    @Override
    public void drawPaymentMethod() {

        if(mToken != null) {
            mDescription.setText(mContext.getString(R.string.mpsdk_last_digits_label) + " " + mToken.getLastFourDigits());
        }
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(mContext, mPaymentMethod.getId());
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
        mDescription = (MPTextView) mView.findViewById(R.id.mpsdkDescription);
        mIcon = (ImageView) mView.findViewById(R.id.mpsdkImage);
        mEditHint = mView.findViewById(R.id.mpsdkEditHint);
        mSeparator = mView.findViewById(R.id.mpsdkSeparator);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_payment_method_edit_card, parent, attachToRoot);
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
