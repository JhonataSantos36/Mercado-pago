package com.mercadopago.uicontrollers.identification;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.MPCardMaskUtil;

import static com.mercadopago.util.TextUtil.isEmpty;

/**
 * Created by mromar on 9/27/17.
 */

public class IdentificationTicketView extends IdentificationView {

    private String mName;
    private String mLastName;

    private MPTextView mNameTextView;
    private MPTextView mLastNameTextView;
    private MPTextView mIdentificationTypeIdTextView;
    private FrameLayout mLastNameContainer;

    public IdentificationTicketView(Context context) {
        super(context);
    }

    public void initializeControls() {
        super.initializeControls();

        mNameTextView =  mView.findViewById(R.id.mpsdkNameView);
        mLastNameTextView =  mView.findViewById(R.id.mpsdkLastnameView);
        mIdentificationTypeIdTextView =  mView.findViewById(R.id.mpsdkIdentificationTypeId);
        mLastNameContainer =  mView.findViewById(R.id.mpsdkLastnameContainer);
        drawIdentificationTypeName();
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_ticket_identification, parent, attachToRoot);
        return mView;
    }

    @Override
    public void draw() {
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

        drawIdentificationName();
        drawIdentificationLastName();
        drawIdentificationTypeName();
    }

    private void drawIdentificationName() {
        if (isEmpty(mName)) {
            if (isEmpty(mLastName)) {
                mNameTextView.setText(mContext.getText(R.string.mpsdk_name_and_lastname_identification_label));
            } else {
                mNameTextView.setText("");
            }
        } else {
            mNameTextView.setText(mName);
            setNormalColorNameText();
            mLastNameContainer.setVisibility(View.VISIBLE);
        }
    }

    private void drawIdentificationLastName() {
        if (isEmpty(mLastName)) {
            mLastNameTextView.setText("");
        } else {
            mLastNameTextView.setText(mLastName);
        }
    }

    public void drawIdentificationTypeName() {
        if (mIdentificationType != null && !isEmpty(mIdentificationType.getId())) {
            mIdentificationTypeIdTextView.setText(mIdentificationType.getId());
        }
    }

    public void setIdentificationName(String name) {
        this.mName = name;
    }

    public void setIdentificationLastName(String lastName) {
        this.mLastName = lastName;
    }

    public void setNormalColorNameText() {
        mNameTextView.setTextColor(ContextCompat.getColor(mContext, NORMAL_TEXT_VIEW_COLOR));
    }

    public void setNormalColorLastNameText() {
        mLastNameTextView.setTextColor(ContextCompat.getColor(mContext, NORMAL_TEXT_VIEW_COLOR));
    }

    public void setAlphaColorNameText() {
        setAlphaColorText(mNameTextView);
    }

    public void setAlphaColorLastNameText() {
        setAlphaColorText(mLastNameTextView);
    }

    private void setAlphaColorText(MPTextView mpTextView) {
        mpTextView.setTextColor(ContextCompat.getColor(mContext, ALPHA_TEXT_VIEW_COLOR));
    }
}
