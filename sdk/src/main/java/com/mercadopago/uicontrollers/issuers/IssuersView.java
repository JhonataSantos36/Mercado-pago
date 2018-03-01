package com.mercadopago.uicontrollers.issuers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Issuer;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersView implements IssuersViewController {

    public static final String CARD_IMAGE_PREFIX = "mpsdk_issuer_";

    private Context mContext;
    private View mView;
    private ImageView mIssuerImageView;
    private MPTextView mIssuerTextView;

    public IssuersView(Context context) {
        this.mContext = context;
    }

    @Override
    public void initializeControls() {
        mIssuerImageView = (ImageView) mView.findViewById(R.id.mpsdkIssuerImageView);
        mIssuerTextView = (MPTextView) mView.findViewById(R.id.mpsdkIssuerTextView);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_view_issuer, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void drawIssuer(Issuer issuer) {
        int image = getCardImage(issuer);
        if (image == 0) {
            mIssuerImageView.setVisibility(View.GONE);
            mIssuerTextView.setVisibility(View.VISIBLE);
            mIssuerTextView.setText(issuer.getName());
        } else {
            mIssuerImageView.setVisibility(View.VISIBLE);
            mIssuerTextView.setVisibility(View.GONE);
            mIssuerImageView.setImageResource(getCardImage(issuer));
        }
    }

    private int getCardImage(Issuer issuer) {
        String imageName = CARD_IMAGE_PREFIX + String.valueOf(issuer.getId());
        return mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
    }
}
