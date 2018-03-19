package com.mercadopago.reviewables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercadopago.example.R;
import com.mercadopago.model.Reviewable;

@Deprecated
public class CongratsReview extends Reviewable {

    public static final Integer CUSTOM_REVIEW = 98;
    protected View mView;
    protected TextView mTextView;
    protected FrameLayout mButton;

    private Context mContext;
    private String mText;

    public CongratsReview(Context context, String text) {
        this.mContext = context;
        this.mText = text;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.congrats_review, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mTextView = (TextView) mView.findViewById(R.id.textView);
        mButton = (FrameLayout) mView.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyChangeRequired(CUSTOM_REVIEW);
            }
        });
    }

    @Override
    public void draw() {
        mTextView.setText(mText);
    }
}
