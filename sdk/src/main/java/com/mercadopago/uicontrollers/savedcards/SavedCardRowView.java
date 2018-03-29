package com.mercadopago.uicontrollers.savedcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.lite.model.Card;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 5/10/16.
 */
public class SavedCardRowView implements SavedCardView {

    private final Integer mSelectionImageResId;
    private final Card mCard;
    private final PaymentMethod mPaymentMethod;
    private final Context mContext;
    private View mSeparator;
    private View mView;
    private MPTextView mDescription;
    private ImageView mIcon;
    private ImageView mEditHint;

    public SavedCardRowView(Context context, Card card, int selectionImageResId) {
        mContext = context;
        mPaymentMethod = card.getPaymentMethod();
        mSelectionImageResId = selectionImageResId;
        mCard = card;
    }

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
    public void showSeparator() {
        mSeparator.setVisibility(View.VISIBLE);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_payment_method_card, parent, attachToRoot);
        return mView;
    }

    protected String getLastFourDigits() {
        String lastFourDigits = "";
        if (mCard != null) {
            lastFourDigits = mCard.getLastFourDigits();
        }
        return lastFourDigits;
    }

    @Override
    public void draw() {
        if (getLastFourDigits() == null || getLastFourDigits().isEmpty()) {
            mDescription.setText(mPaymentMethod.getName());
        } else {
            mDescription.setText(new StringBuilder().append(mContext.getString(R.string.mpsdk_last_digits_label)).append(" ").append(getLastFourDigits()).toString());
        }
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(mContext, mPaymentMethod.getId());
        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
        } else {
            mIcon.setVisibility(View.GONE);
        }
        if (mSelectionImageResId != null) {
            mEditHint.setImageResource(mSelectionImageResId);
        }
    }
}
