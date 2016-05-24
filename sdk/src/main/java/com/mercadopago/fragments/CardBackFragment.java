package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.views.MPTextView;

public class CardBackFragment extends android.support.v4.app.Fragment {

    private MPTextView mCardSecurityCodeTextView;
    private FrameLayout mBaseCardSecurityCode;

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
            mCardSecurityCodeTextView = (MPTextView) getView().findViewById(R.id.cardSecurityCodeView);
            mBaseCardSecurityCode = (FrameLayout) getView().findViewById(R.id.base_card_security_container);
        }
    }

    public void populateViews() {
        populateCardSecurityCode();
    }
    
    public void onSecurityTextChanged(CharSequence s, int start, int before, int count) {
        mBaseCardSecurityCode.setVisibility(View.INVISIBLE);
        mCardSecurityCodeTextView.setVisibility(View.VISIBLE);
        mCardSecurityCodeTextView.setText(s);
    }

    public void afterSecurityTextChanged(Editable s) {
        mActivity.saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityCodeTextView.setVisibility(View.INVISIBLE);
            mBaseCardSecurityCode.setVisibility(View.VISIBLE);
            mActivity.saveCardSecurityCode(null);
        }
    }

    private void populateCardSecurityCode() {
        String securityCode = mActivity.getSecurityCode();
        if (securityCode == null || securityCode.equals("")) {
            mBaseCardSecurityCode.setVisibility(View.VISIBLE);
            mCardSecurityCodeTextView.setVisibility(View.INVISIBLE);
        } else {
            mBaseCardSecurityCode.setVisibility(View.INVISIBLE);
            mCardSecurityCodeTextView.setVisibility(View.VISIBLE);
            setText(mCardSecurityCodeTextView, securityCode, CardInterface.FULL_TEXT_VIEW_COLOR);
        }
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
        textView.setText(text);
    }

}