package com.mercadopago.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mercadopago.NewFormActivity;
import com.mercadopago.R;
import com.mercadopago.views.MPTextView;

public class CardBackFragment extends android.support.v4.app.Fragment {

    private EditText mCardSecurityCodeEditText;
    private MPTextView mCardSecurityView;
    private FrameLayout mBaseCard;
    private GradientDrawable mBaseDrawableCard;

    public CardBackFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mCardSecurityCodeEditText = (EditText) getActivity().findViewById(R.id.cardSecurityCode);
        if (getView() != null) {
            mCardSecurityView = (MPTextView) getView().findViewById(R.id.cardSecurityCodeView);
            mBaseCard = (FrameLayout) getView().findViewById(R.id.activity_new_card_form_color_back);
            mBaseDrawableCard = (GradientDrawable) mBaseCard.getBackground();
        }
    }

    public void populateViews() {
        populateCardSecurityCode();
    }

    public void onSecurityTextChanged(CharSequence s, int start, int before, int count) {
        mCardSecurityView.setText(s);
    }

    public void afterSecurityTextChanged(Editable s) {
        ((NewFormActivity) getActivity()).saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityView.setText("");
            ((NewFormActivity) getActivity()).saveCardSecurityCode("");
        }
    }


    private void populateCardSecurityCode() {
        String securityCode = ((NewFormActivity)getActivity()).getSecurityCode();
//        String state = ((NewFormActivity)getActivity()).getSecurityCodeState();
        if (securityCode != null) {
            mCardSecurityView.setText(securityCode);
            mCardSecurityView.setTextColor(getResources()
                    .getColor(NewFormActivity.FULL_TEXT_VIEW_COLOR));
        }
    }

    public void setCardSecurityCodeErrorView() {
        mCardSecurityView.setTextColor(getResources().getColor(NewFormActivity.ERROR_TEXT_VIEW_COLOR));
    }

    public void clearCardSecurityCodeErrorView() {
        mCardSecurityView.setTextColor(getResources().getColor(NewFormActivity.FULL_TEXT_VIEW_COLOR));
    }

}