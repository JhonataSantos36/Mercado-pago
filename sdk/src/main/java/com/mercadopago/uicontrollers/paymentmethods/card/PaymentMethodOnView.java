package com.mercadopago.uicontrollers.paymentmethods.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 28/6/16.
 */
public abstract class PaymentMethodOnView implements PaymentMethodViewController {

    protected PaymentMethod mPaymentMethod;
    protected Context mContext;
    protected View mSeparator;
    protected View mView;
    protected MPTextView mDescription;
    protected ImageView mIcon;
    protected ImageView mEditHint;

    @Override
    public void draw() {
        if (getLastFourDigits() == null || getLastFourDigits().isEmpty()) {
            mDescription.setText(mPaymentMethod.getName());
        }
        else {
            mDescription.setText(new StringBuilder().append(mContext.getString(R.string.mpsdk_last_digits_label)).append(" ").append(getLastFourDigits()).toString());
        }
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(mContext, mPaymentMethod.getId());
        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    protected abstract String getLastFourDigits();

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mEditHint.setVisibility(View.VISIBLE);
        mView.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mDescription = mView.findViewById(R.id.mpsdkDescription);
        mIcon = mView.findViewById(R.id.mpsdkImage);
        mEditHint = mView.findViewById(R.id.mpsdkEditHint);
        mSeparator = mView.findViewById(R.id.mpsdkSeparator);
    }

    @Override
    public abstract View inflateInParent(ViewGroup parent, boolean attachToRoot);

    @Override
    public void showSeparator() {
        mSeparator.setVisibility(View.VISIBLE);
    }

    @Override
    public View getView() {
        return mView;
    }
}
