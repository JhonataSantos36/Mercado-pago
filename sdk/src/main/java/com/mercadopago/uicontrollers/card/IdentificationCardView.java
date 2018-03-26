package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.identification.IdentificationView;
import com.mercadopago.util.MPCardMaskUtil;

/**
 * Created by vaserber on 10/20/16.
 */

public class IdentificationCardView extends IdentificationView {

    public IdentificationCardView(Context context) {
        super(context);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_card_identification, parent, attachToRoot);
        return mView;
    }

    @Override
    public void draw() {
        //TODO se puede subir
        if (mIdentificationNumber == null || mIdentificationNumber.length() == 0) {
            mIdentificationNumberTextView.setVisibility(View.INVISIBLE);
            mBaseIdNumberView.setVisibility(View.VISIBLE);
        } else {
            mBaseIdNumberView.setVisibility(View.INVISIBLE);
            mIdentificationNumberTextView.setVisibility(View.VISIBLE);

            String number = MPCardMaskUtil.buildIdentificationNumberWithMask(mIdentificationNumber, mIdentificationType);
            mIdentificationNumberTextView.setTextColor(ContextCompat.getColor(mContext, NORMAL_TEXT_VIEW_COLOR));
            mIdentificationNumberTextView.setText(number);
        }
    }
}
