package com.mercadopago.examples.reviewables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercadopago.examples.R;
import com.mercadopago.model.Reviewable;

/**
 * Created by vaserber on 2/16/17.
 */

public class CongratsReview extends Reviewable {

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
                notifyChangeRequired();
            }
        });
    }

    @Override
    public void draw() {
        mTextView.setText(mText);
    }
}
