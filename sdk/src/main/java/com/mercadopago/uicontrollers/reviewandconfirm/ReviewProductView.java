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
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.util.CircleTransform;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ScaleUtil;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import static com.mercadopago.util.TextUtil.isEmpty;

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

    private Context mContext;
    private ReviewScreenPreference mReviewScreenPreference;

    public ReviewProductView(Context context, ReviewScreenPreference reviewScreenPreference) {
        this.mContext = context;
        this.mReviewScreenPreference = reviewScreenPreference;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_adapter_review_product, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mProductImage = (ImageView) mView.findViewById(R.id.mpsdkAdapterReviewProductImage);
        mProductName = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductText);
        mProductDescription = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductDescription);
        mProductQuantity = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductQuantity);
        mProductPrice = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewProductPrice);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void drawProduct(int position, Item item, String currencyId) {
        String pictureUrl = item.getPictureUrl();

        setProductIcon(pictureUrl);
        setProductName(item);
        setProductDescription(item);
        setProductQuantity(item);
        setProductAmount(item, currencyId);
    }

    private void setProductName(Item item) {
        if (item.getTitle() == null) {
            mProductName.setVisibility(View.GONE);
        } else {
            mProductName.setText(item.getTitle());
        }
    }

    private void setProductDescription(Item item) {
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            mProductDescription.setVisibility(View.GONE);
        } else {
            mProductDescription.setText(item.getDescription());
        }
    }

    private void setProductQuantity(Item item) {
        Integer quantity = item.getQuantity();
        if (quantity == null) {
            quantity = 1;
        }

        if (mReviewScreenPreference != null && !mReviewScreenPreference.showQuantityRow()) {
            mProductQuantity.setVisibility(View.GONE);
        } else if (mReviewScreenPreference != null && mReviewScreenPreference.showQuantityRow() && !isEmpty(mReviewScreenPreference.getQuantityTitle())) {
            String productQuantityText = mReviewScreenPreference.getQuantityTitle() + quantity;
            mProductQuantity.setText(productQuantityText);
        } else {
            mProductQuantity.setText(mContext.getResources().getString(R.string.mpsdk_review_product_quantity, String.valueOf(quantity)));
        }
    }

    private void setProductAmount(Item item, String currencyId) {
        if (item.getUnitPrice() != null) {
            String priceText;
            BigDecimal price = item.getUnitPrice();

            if (mReviewScreenPreference != null && !mReviewScreenPreference.showAmountTitle()) {
                priceText = CurrenciesUtil.formatNumber(price, currencyId);
            } else if (mReviewScreenPreference != null && mReviewScreenPreference.showAmountTitle() && !isEmpty(mReviewScreenPreference.getAmountTitle())) {
                String originalNumber = CurrenciesUtil.formatNumber(price, currencyId);
                priceText = mReviewScreenPreference.getAmountTitle() + originalNumber;
            } else {
                String originalNumber = CurrenciesUtil.formatNumber(price, currencyId);
                priceText = mContext.getString(R.string.mpsdk_review_product_price, originalNumber);
            }

            Spanned priceSpanned = CurrenciesUtil.formatCurrencyInText(price, currencyId, priceText, false, true);
            mProductPrice.setText(priceSpanned);
        }
    }

    private void setProductIcon(String pictureUrl) {
        int resId = R.drawable.mpsdk_review_product_placeholder;

        if (pictureUrl == null || pictureUrl.isEmpty()) {
            setDefaultProductIcon(resId);
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

    private void setDefaultProductIcon(int resourceId) {
        mProductImage.setImageResource(resourceId);
    }
}
