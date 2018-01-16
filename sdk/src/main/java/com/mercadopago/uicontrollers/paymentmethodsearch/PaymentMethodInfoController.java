package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.util.TextUtils;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodInfoController implements PaymentMethodSearchViewController {

    private static final int COMMENT_MAX_LENGTH = 75;

    protected PaymentMethodInfo mItem;
    protected Context mContext;
    protected View mView;
    protected MPTextView mName;
    protected MPTextView mDescription;
    protected ImageView mIcon;
    protected View.OnClickListener mListener;

    public PaymentMethodInfoController(@NonNull final Context context,
                                       @NonNull final PaymentMethodInfo item) {
        mContext = context;
        mItem = item;
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_pm_info_item, parent, attachToRoot);
        if (mListener != null) {
            mView.setOnClickListener(mListener);
        }
        mView.setTag(mItem.id);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    public void initializeControls() {
        mName = mView.findViewById(R.id.mpsdk_name);
        mDescription = mView.findViewById(R.id.mpsdk_description);
        mIcon = mView.findViewById(R.id.mpsdk_image);
    }

    public void draw() {

        if (TextUtils.isEmpty(mItem.name)) {
            mName.setVisibility(View.GONE);
        } else {
            mName.setVisibility(View.VISIBLE);
            mName.setText(mItem.name);
        }

        if (TextUtils.isNotEmpty(mItem.description)
                && mItem.description.length() < COMMENT_MAX_LENGTH) {
            mDescription.setText(mItem.description);
        }

        if (mItem.icon == R.drawable.mpsdk_none) {
            mIcon.setVisibility(View.GONE);
        } else {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setImageResource(mItem.icon);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
        if (mView != null) {
            mView.setOnClickListener(listener);
        }
    }
}
