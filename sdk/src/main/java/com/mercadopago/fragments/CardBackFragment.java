package com.mercadopago.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.CardInterface;
import com.mercadopago.GuessingNewCardActivity;
import com.mercadopago.R;
import com.mercadopago.views.MPTextView;

public class CardBackFragment extends android.support.v4.app.Fragment {

    private MPTextView mCardSecurityView;

    private CardInterface mActivity;

    public CardBackFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (CardInterface)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_card_back, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCardInputViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViews();
    }

    public void setCardInputViews() {
        if (getView() != null) {
            mCardSecurityView = (MPTextView) getView().findViewById(R.id.cardSecurityCodeView);
        }
    }

    public void populateViews() {
        populateCardSecurityCode();
    }

    public void onSecurityTextChanged(CharSequence s, int start, int before, int count) {
        mCardSecurityView.setText(s);
    }

    public void afterSecurityTextChanged(Editable s) {
        mActivity.saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityView.setText("");
            mActivity.saveCardSecurityCode("");
        }
    }


    private void populateCardSecurityCode() {
        String securityCode = mActivity.getSecurityCode();
        if (securityCode != null) {
            mCardSecurityView.setText(securityCode);
            mCardSecurityView.setTextColor(getResources()
                    .getColor(GuessingNewCardActivity.FULL_TEXT_VIEW_COLOR));
        }
    }

}