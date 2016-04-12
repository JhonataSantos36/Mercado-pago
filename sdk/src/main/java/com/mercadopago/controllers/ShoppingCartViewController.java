package com.mercadopago.controllers;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mercadopago.R;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.views.MPTextView;
import com.mercadopago.util.ScaleUtil;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * Created by mreverter on 10/2/16.
 */
public class ShoppingCartViewController {

    private Boolean mStartShowingItemInfo;
    private String mPurchaseTitle;
    private Boolean mItemDescriptionShown;
    private String mPictureUrl;

    private Activity mActivity;
    private RelativeLayout mItemInfoLayout;
    private ImageView mImageViewTogglerShoppingCart;
    private ImageView mItemImageView;

    public ShoppingCartViewController(Activity activity, ImageView toggler, String pictureUri, String purchaseTitle, Integer titleMaxLength, BigDecimal amount, String currencyId, Boolean startShowingItemInfo) {
        mImageViewTogglerShoppingCart = toggler;
        initialize(activity, pictureUri, purchaseTitle, titleMaxLength, amount, currencyId, startShowingItemInfo);
        start();
    }
    private void initialize(Activity activity, String pictureUri, String purchaseTitle, Integer titleMaxLength, BigDecimal amount, String currencyId, Boolean startShowingItemInfo) {
        mActivity = activity;
        mStartShowingItemInfo = startShowingItemInfo;
        mItemInfoLayout = (RelativeLayout) mActivity.findViewById(R.id.itemInfoLayout);
        mItemImageView = (ImageView) mActivity.findViewById(R.id.itemImage);
        mPictureUrl = pictureUri;
        mPurchaseTitle = this.getFormattedPurchaseTitle(purchaseTitle, titleMaxLength);

        MPTextView itemDescriptionTextView = (MPTextView) mActivity.findViewById(R.id.itemTitle);
        MPTextView itemAmountTextView = (MPTextView) mActivity.findViewById(R.id.itemAmount);
        itemDescriptionTextView.setText(mPurchaseTitle);
        itemAmountTextView.setText(getAmountLabel(amount, currencyId));
        showItemImage();
    }

    private void showItemImage() {
        if(mPictureUrl != null && !mPictureUrl.isEmpty()) {
            Picasso.with(mActivity).load(mPictureUrl).into(mItemImageView);
        }
        else
        {
            int dpAsPixels = ScaleUtil.getPxFromDp(24, mActivity);
            mItemImageView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
    }

    private void start() {
        if(mStartShowingItemInfo) {
            showItemInfo();
        }
        else {
            hideItemInfo();
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    protected String getFormattedPurchaseTitle(String purchaseTitle, Integer purchaseTitleMaxLength) {
        if(purchaseTitle != null && purchaseTitleMaxLength != null) {
            if (purchaseTitle.length() > purchaseTitleMaxLength) {
                purchaseTitle = purchaseTitle.substring(0, purchaseTitleMaxLength-1);
                purchaseTitle = purchaseTitle + "…";
            }
            return purchaseTitle;
        }
        else {
            return purchaseTitle;
        }
    }

    public String getPurchaseTitle() {
        return mPurchaseTitle;
    }

    public Spanned getAmountLabel(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.formatNumber(amount, currencyId, true, true);
    }

    public void toggle() {
        if(!isItemShown()) {
            showItemInfo();
        }
        else {
            hideItemInfo();
        }
    }
    public boolean isItemShown() {
        return this.mItemDescriptionShown;
    }

    public void hideItemInfo() {
        mItemDescriptionShown = false;
        mItemInfoLayout.setVisibility(View.GONE);
        if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.icon_cart);
            int dpAsPixels = ScaleUtil.getPxFromDp(8, mActivity);
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    public void showItemInfo() {
        mItemInfoLayout.setVisibility(View.VISIBLE);
        mItemDescriptionShown = true;
        if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.close);
            int dpAsPixels = ScaleUtil.getPxFromDp(12, mActivity);
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    private void tintTogglerDrawableWithColor(int color) {
        Drawable togglerDrawable = getTogglerDrawable();
        if (togglerDrawable != null) {
            togglerDrawable.mutate();
            togglerDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private Drawable getTogglerDrawable() {
        Drawable togglerDrawable = null;
        if(mImageViewTogglerShoppingCart != null) {
            togglerDrawable = mImageViewTogglerShoppingCart.getDrawable();
        }
        return togglerDrawable;
    }
}
