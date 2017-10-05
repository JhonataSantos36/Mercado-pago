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

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by mromar on 9/27/17.
 */

public class IdentificationTicketView extends IdentificationView {

    private String mName;
    private String mLastName;
    private String mIdentificationTypeId;

    private MPTextView mNameTextView;
    private MPTextView mLastNameTextView;
    private MPTextView mIdentificationTypeIdTextView;
    private FrameLayout mLastNameContainer;

    public IdentificationTicketView(Context context) {
        super(context);
    }

    public void initializeControls() {
        super.initializeControls();

        mNameTextView = (MPTextView) mView.findViewById(R.id.mpsdkNameView);
        mLastNameTextView = (MPTextView) mView.findViewById(R.id.mpsdkLastnameView);
        mIdentificationTypeIdTextView = (MPTextView) mView.findViewById(R.id.mpsdkIdentificationTypeId);
        mLastNameContainer = (FrameLayout) mView.findViewById(R.id.mpsdkLastnameContainer);
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
            mNameTextView.setText(mContext.getText(R.string.mpsdk_name_and_lastname_identification_label));
        } else {
            mNameTextView.setText(mName);
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
        if (!isEmpty(mIdentificationTypeId)) {
            mIdentificationTypeIdTextView.setText(mIdentificationTypeId);
        }
    }

    public void setIdentificationName(String name) {
        this.mName = name;
    }

    public void setIdentificationLastName(String lastName) {
        this.mLastName = lastName;
    }

    public void setIdentificationTypeId(String identificationTypeId) {
        this.mIdentificationTypeId = identificationTypeId;
    }
}
