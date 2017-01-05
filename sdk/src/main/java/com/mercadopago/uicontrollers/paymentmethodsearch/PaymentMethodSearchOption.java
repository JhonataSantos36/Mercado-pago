package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodSearchOption implements PaymentMethodSearchViewController {

    private static final int COMMENT_MAX_LENGTH = 75;
    private static final String TO_TINT_IMAGES_PREFIX = "grey_";

    protected PaymentMethodSearchItem mItem;
    protected Context mContext;
    protected View mView;
    protected MPTextView mDescription;
    protected MPTextView mComment;
    protected ImageView mIcon;
    protected DecorationPreference mDecorationPreference;
    protected View.OnClickListener mListener;

    public PaymentMethodSearchOption(Context context, PaymentMethodSearchItem item, DecorationPreference decorationPreference) {
        mContext = context;
        mItem = item;
        mDecorationPreference = decorationPreference;
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_pm_search_item, parent, attachToRoot);
        if (mListener != null) {
            mView.setOnClickListener(mListener);
        }
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

    }

    public void draw() {
        if (mItem.hasDescription()) {
            mDescription.setVisibility(View.VISIBLE);
            mDescription.setText(mItem.getDescription());
        } else {
            mDescription.setVisibility(View.GONE);
        }
        if (mItem.hasComment() && mItem.getComment().length() < COMMENT_MAX_LENGTH) {
            mComment.setText(mItem.getComment());
        }

        int resourceId = 0;

        Boolean needsTint = itemNeedsTint(mItem);
        String imageId;
        if(needsTint) {
            imageId = TO_TINT_IMAGES_PREFIX + mItem.getId();
        } else {
            imageId = mItem.getId();
        }

        if (mItem.isIconRecommended()) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, imageId);
        }

        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }

        if(needsTint) {
            mIcon.setColorFilter(mDecorationPreference.getBaseColor(), PorterDuff.Mode.MULTIPLY);
        }
    }

    private boolean itemNeedsTint(PaymentMethodSearchItem paymentMethodSearchItem) {

        return mDecorationPreference != null && mDecorationPreference.hasColors()
                && (paymentMethodSearchItem.isGroup()
                || paymentMethodSearchItem.isPaymentType());
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
        if (mView != null) {
            mView.setOnClickListener(listener);
        }
    }
}
