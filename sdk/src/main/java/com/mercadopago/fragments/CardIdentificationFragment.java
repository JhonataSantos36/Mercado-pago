package com.mercadopago.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mercadopago.CardInterface;
import com.mercadopago.NewFormActivity;
import com.mercadopago.R;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class CardIdentificationFragment extends android.support.v4.app.Fragment {

    private MPTextView mCardIdentificationNumberView;
    private MPEditText mCardIdentificationNumberEditText;
    private FrameLayout mCardContainer;
    private FrameLayout mCardNumberClickableZone;
    private LinearLayout mBaseIdNumberView;

    private CardInterface mActivity;

    public CardIdentificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identification_card_front, container, false);
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
        ((NewFormActivity) getActivity()).saveCardIdentificationNumber(s.toString());
        if (s.length() == 0) {
            mCardIdentificationNumberView.setText("");
            ((NewFormActivity) getActivity()).saveCardIdentificationNumber(null);
        }
    }

    public void setCardInputViews() {
        mCardIdentificationNumberEditText = (MPEditText) getActivity().findViewById(R.id.cardIdentificationNumber);
        mCardIdentificationNumberEditText.requestFocus();
        if (getView() != null) {
            mBaseIdNumberView = (LinearLayout) getView().findViewById(R.id.identificationCardholderContainer);
            mCardNumberClickableZone = (FrameLayout) getView().findViewById(R.id.idNumberClickableZone);
            mCardIdentificationNumberView = (MPTextView) getView().findViewById(R.id.idNumberView);
            mCardContainer = (FrameLayout) getView().findViewById(R.id.identification_card_container);
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
//            String state = mActivity.getCardIdentificationNumberState();
            int color = CardInterface.FULL_TEXT_VIEW_COLOR;
            String number = mActivity.buildIdentificationNumberWithMask(identificationNumber);
            setText(mCardIdentificationNumberView, number, color);
        }
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(getResources().getColor(color));
        textView.setText(text);
    }

    public int getColorByState(String state) {
        if (state.equals(CardInterface.NORMAL_STATE)) {
            return CardInterface.FULL_TEXT_VIEW_COLOR;
        } else if (state.equals(CardInterface.ERROR_STATE)) {
            return CardInterface.ERROR_TEXT_VIEW_COLOR;
        }
        return 0;
    }

    public void setCardIdentificationNumberErrorView() {
        mCardIdentificationNumberView.setTextColor(getResources().getColor(CardInterface.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardIdentificationNumberErrorView() {
        mCardIdentificationNumberView.setTextColor(getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR));
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
}
