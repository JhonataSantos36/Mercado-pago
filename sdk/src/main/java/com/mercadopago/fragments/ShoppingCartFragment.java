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
import com.mercadopago.model.DecorationPreference;
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
    private MPTextView mItemDescriptionTextView;
    private MPTextView mItemAmountTextView;
    private View mShoppingCartLayout;
    private DecorationPreference mDecorationPreference;

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

    public static ShoppingCartFragment newInstance(String pictureUrl, String purchaseTitle, BigDecimal amount, String currrencyId, DecorationPreference decorationPreference) {

        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pictureUrl", pictureUrl);
        bundle.putString("amount", amount.toString());
        bundle.putString("currencyId", currrencyId);
        bundle.putString("purchaseTitle", purchaseTitle);
        bundle.putSerializable("decorationPreference", decorationPreference);

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
            mDecorationPreference = (DecorationPreference) getArguments().getSerializable("decorationPreference");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_fragment_shopping_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls();
        fillData();
    }

    private void initializeControls() {
        if(getView() != null) {
            mItemImageView = (ImageView) getView().findViewById(R.id.mpsdkItemImage);
            mItemDescriptionTextView = (MPTextView) getView().findViewById(R.id.mpsdkItemTitle);
            mItemAmountTextView = (MPTextView) getView().findViewById(R.id.mpsdkItemAmount);
            mShoppingCartLayout = getView().findViewById(R.id.mpsdkShoppingCartLayout);
        }
    }

    private void fillData() {
        String truncatedTitle = getFormattedPurchaseTitle();
        mItemDescriptionTextView.setText(truncatedTitle);
        mItemAmountTextView.setText(getAmountLabel(mAmount, mCurrencyId));
        setItemImage();

        if(mDecorationPreference != null) {
            mShoppingCartLayout.setBackgroundColor(mDecorationPreference.getLighterColor());
            if(mDecorationPreference.isDarkFontEnabled()) {
                mItemAmountTextView.setTextColor(mDecorationPreference.getDarkFontColor(getActivity()));
                mItemDescriptionTextView.setTextColor(mDecorationPreference.getDarkFontColor(getActivity()));
            }
        }
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
