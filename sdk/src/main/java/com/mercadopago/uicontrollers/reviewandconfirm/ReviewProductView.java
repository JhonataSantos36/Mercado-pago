package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Item;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ScaleUtil;
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

    public ReviewProductView(Context context) {
        this.mContext = context;
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
    public void drawProduct(int position, Item item, String currencyId, DecorationPreference decorationPreference) {
        if (position != 0) {
            mFirstSeparator.setVisibility(View.GONE);
        }
        String pictureUrl = item.getPictureUrl();
        setProductIcon(pictureUrl, decorationPreference);

        if (item.getTitle() == null) {
            mProductName.setVisibility(View.GONE);
        } else {
            mProductName.setText(item.getTitle());
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            mProductDescription.setVisibility(View.GONE);
        } else {
            mProductDescription.setText(item.getDescription());
        }
        Integer quantity = item.getQuantity();
        if (quantity == null) {
            quantity = 1;
        }
        mProductQuantity.setText(mContext.getResources().getString(R.string.mpsdk_review_product_quantity, String.valueOf(quantity)));

        if (item.getUnitPrice() != null) {
            BigDecimal price = item.getUnitPrice();
            String originalNumber = CurrenciesUtil.formatNumber(price, currencyId);
            String string = mContext.getString(R.string.mpsdk_review_product_price, originalNumber);
            Spanned priceText = CurrenciesUtil.formatCurrencyInText(price, currencyId, string, false, true);
            mProductPrice.setText(priceText);
        }
    }

    private void setProductIcon(String pictureUrl, DecorationPreference decorationPreference) {
        int resId;
        if (decorationPreference != null && decorationPreference.hasColors()) {
            resId = R.drawable.mpsdk_grey_review_product_placeholder;
        } else {
            resId = R.drawable.mpsdk_review_product_placeholder;
        }

        if (pictureUrl == null || pictureUrl.isEmpty()) {
            setDefaultProductIcon(resId, decorationPreference);
        } else {
            int dimen = ScaleUtil.getPxFromDp(48, mContext);
            Picasso.with(mContext)
                    .load(pictureUrl)
                    .transform(new CircleTransform())
                    .resize(dimen, dimen)
                    .centerInside()
                    .placeholder(resId)
                    .into(mProductImage);
        }
    }

    private void setDefaultProductIcon(int resourceId, DecorationPreference decorationPreference) {
        mProductImage.setImageResource(resourceId);
        if (decorationPreference != null && decorationPreference.hasColors()) {
            mProductImage.setColorFilter(decorationPreference.getBaseColor(), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
