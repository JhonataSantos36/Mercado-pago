package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.CardInterface;
import com.mercadopago.GuessingCardActivity;
import com.mercadopago.R;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class CardIdentificationFragment extends android.support.v4.app.Fragment {

    private MPTextView mCardIdentificationNumberView;
    private MPEditText mCardIdentificationNumberEditText;
    private MPTextView mBaseIdNumberView;

    private CardInterface mActivity;

    public CardIdentificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_identification_card_front, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCardInputViews();
        setEditTextListeners();
        mActivity = (CardInterface)getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViews();
    }

    public void onNumberTextChanged(CharSequence s, int start, int before, int count) {
        mCardIdentificationNumberView.setText(s);
    }

    public void afterNumberTextChanged(Editable s) {
        ((GuessingCardActivity) getActivity()).saveCardIdentificationNumber(s.toString());
        if (s.length() == 0) {
            mCardIdentificationNumberView.setText("");
            ((GuessingCardActivity) getActivity()).saveCardIdentificationNumber(null);
        }
    }

    public void setCardInputViews() {
        mCardIdentificationNumberEditText = (MPEditText) getActivity().findViewById(R.id.mpsdkCardIdentificationNumber);
        mCardIdentificationNumberEditText.requestFocus();
        if (getView() != null) {
            mBaseIdNumberView = (MPTextView) getView().findViewById(R.id.mpsdkIdentificationCardholderContainer);
            mCardIdentificationNumberView = (MPTextView) getView().findViewById(R.id.mpsdkIdNumberView);
        }
    }

    public void populateViews() {
        populateIdentificationNumber();
    }

    protected void setEditTextListeners() {
        setCardIdentificationListener();
    }

    public void populateIdentificationNumber() {
        String identificationNumber = mActivity.getCardIdentificationNumber();
        if (identificationNumber == null) {
            mCardIdentificationNumberView.setVisibility(View.INVISIBLE);
            mBaseIdNumberView.setVisibility(View.VISIBLE);
        } else {
            mBaseIdNumberView.setVisibility(View.INVISIBLE);
            mCardIdentificationNumberView.setVisibility(View.VISIBLE);
            int color = CardInterface.NORMAL_TEXT_VIEW_COLOR;
            String number = mActivity.buildIdentificationNumberWithMask(identificationNumber);
            setText(mCardIdentificationNumberView, number, color);
        }
    }

    private void setCardIdentificationListener() {
        if (mCardIdentificationNumberEditText != null) {
            mCardIdentificationNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mBaseIdNumberView.setVisibility(View.INVISIBLE);
                    mCardIdentificationNumberView.setVisibility(View.VISIBLE);
                    String number = mActivity.buildIdentificationNumberWithMask(s);
                    mCardIdentificationNumberView.setText(number);
                }


                @Override
                public void afterTextChanged(Editable s) {
                    mActivity.saveCardIdentificationNumber(s.toString());
                    if (s.length() == 0) {
                        mCardIdentificationNumberView.setVisibility(View.INVISIBLE);
                        mBaseIdNumberView.setVisibility(View.VISIBLE);
                        mActivity.saveCardIdentificationNumber(null);
                    }
                }
            });
        }
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
        textView.setText(text);
    }
}
