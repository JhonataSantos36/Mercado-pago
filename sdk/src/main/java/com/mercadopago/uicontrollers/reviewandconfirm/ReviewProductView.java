package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Item;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.CurrenciesUtil;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/10/16.
 */

public class ReviewProductView implements ReviewProductViewController {

    protected View mView;
    protected ImageView mProductImage;
    protected MPTextView mProductName;
    protected MPTextView mProductDescription;
    protected MPTextView mProductQuantity;
    protected MPTextView mProductPrice;
    protected View mFirstSeparator;

    private Context mContext;
    private Item mItem;
    private String mCurrencyId;

    public ReviewProductView(Context context, Item item, String currencyId) {
        this.mContext = context;
        this.mItem = item;
        this.mCurrencyId = currencyId;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_adapter_review_product, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mProductImage = (ImageView) mView.findViewById(R.id.mpsdkAdapterReviewProductImage);
        mProductName = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductText);
        mProductDescription = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductDescription);
        mProductQuantity = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductQuantity);
        mProductPrice = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductPrice);
        mFirstSeparator = mView.findViewById(R.id.mpsdkFirstSeparator);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void drawProduct(int position) {
        if (position != 0) {
            mFirstSeparator.setVisibility(View.GONE);
        }
        String pictureUrl = mItem.getPictureUrl();
        if (pictureUrl == null || pictureUrl.isEmpty()) {
            mProductImage.setImageResource(R.drawable.review_product_placeholder);
        } else {
            Picasso.with(mContext)
                    .load(pictureUrl)
                    .transform(new CircleTransform())
                    .fit()
                    .placeholder(R.drawable.review_product_placeholder)
                    .into(mProductImage);
        }
        if (mItem.getTitle() == null) {
            mProductName.setVisibility(View.GONE);
        } else {
            mProductName.setText(mItem.getTitle());
        }
        if (mItem.getDescription() == null || mItem.getDescription().isEmpty()) {
            mProductDescription.setVisibility(View.GONE);
        } else {
            mProductDescription.setText(mItem.getDescription());
        }
        Integer quantity = mItem.getQuantity();
        if (quantity == null) {
            quantity = 1;
        }
        mProductQuantity.setText(mContext.getResources().getString(R.string.mpsdk_review_product_quantity, String.valueOf(quantity)));
        BigDecimal price = mItem.getUnitPrice();
        String originalNumber = CurrenciesUtil.formatNumber(price, mCurrencyId);
        String string = mContext.getString(R.string.mpsdk_review_product_price, originalNumber);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        Spanned priceText = CurrenciesUtil.formatCurrencyInText(price, mCurrencyId, stringBuilder.toString(), false, true);
        mProductPrice.setText(priceText);
    }
}
