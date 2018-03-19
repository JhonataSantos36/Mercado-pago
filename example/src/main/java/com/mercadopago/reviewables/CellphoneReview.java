package com.mercadopago.reviewables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.example.R;
import com.mercadopago.model.Reviewable;

@Deprecated
public class CellphoneReview extends Reviewable {

    public static final Integer CELLPHONE_CHANGE = 321321;

    protected View mView;
    protected TextView mNumberTextView;
    protected View mNumberEdition;

    private Context mContext;
    private String mNumber;

    public CellphoneReview(Context context, String cellphoneNumber) {
        this.mContext = context;
        this.mNumber = cellphoneNumber;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.cellphone_review, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mNumberTextView = mView.findViewById(R.id.phoneNumber);
        mNumberEdition = mView.findViewById(R.id.phoneNumberEdition);
        mNumberEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyChangeRequired(CELLPHONE_CHANGE);
                Toast.makeText(mContext, "Cambiar número!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void draw() {
        mNumberTextView.setText(mNumber);
    }
}
