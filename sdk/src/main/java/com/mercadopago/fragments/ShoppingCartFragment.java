package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.MPTextView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

public class ShoppingCartFragment extends Fragment {

    protected static final Integer PURCHASE_TITLE_MAX_LENGTH = 50;

    private String mPurchaseTitle;
    private String mPictureUrl;
    private BigDecimal mAmount;
    private String mCurrencyId;

    private ImageView mItemImageView;
    private MPTextView itemDescriptionTextView;
    private MPTextView itemAmountTextView;

    public ShoppingCartFragment() {}

    public static ShoppingCartFragment newInstance(String pictureUrl, String purchaseTitle, BigDecimal amount, String currrencyId) {

        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pictureUrl", pictureUrl);
        bundle.putString("amount", amount.toString());
        bundle.putString("currencyId", currrencyId);
        bundle.putString("purchaseTitle", purchaseTitle);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPictureUrl = getArguments().getString("pictureUrl");
            mPurchaseTitle = getArguments().getString("purchaseTitle");
            mCurrencyId = getArguments().getString("currencyId");
            String amountParameter = getArguments().getString("amount");
            if(amountParameter != null) {
                mAmount = new BigDecimal(amountParameter);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls();
        fillData();
    }

    private void initializeControls() {
        if(getView() != null) {
            mItemImageView = (ImageView) getView().findViewById(R.id.itemImage);
            itemDescriptionTextView = (MPTextView) getView().findViewById(R.id.itemTitle);
            itemAmountTextView = (MPTextView) getView().findViewById(R.id.itemAmount);
        }
    }

    private void fillData() {
        String truncatedTitle = getFormattedPurchaseTitle();
        itemDescriptionTextView.setText(truncatedTitle);
        itemAmountTextView.setText(getAmountLabel(mAmount, mCurrencyId));
        setItemImage();
    }

    protected String getFormattedPurchaseTitle() {
        if(mPurchaseTitle != null) {
            if (mPurchaseTitle.length() > PURCHASE_TITLE_MAX_LENGTH) {
                mPurchaseTitle = mPurchaseTitle.substring(0, PURCHASE_TITLE_MAX_LENGTH - 1);
                mPurchaseTitle = mPurchaseTitle + "…";
            }
        }
        return mPurchaseTitle;
    }

    private void setItemImage() {
        if(mPictureUrl != null && !mPictureUrl.isEmpty()) {
            mItemImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mpsdk_white));
            Picasso.with(getContext()).load(mPictureUrl).placeholder(R.drawable.progress).into(mItemImageView);
        }
        else {
            int dpAsPixels = ScaleUtil.getPxFromDp(24, getContext());
            mItemImageView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
    }

    public Spanned getAmountLabel(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.formatNumber(amount, currencyId, true, true);
    }
}
