package com.mercadopago.controllers;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
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
    private Activity mActivity;

    private Boolean mItemDescriptionShown;
    private ImageView mImageViewTogglerShoppingCart;
    private MenuItem mMenuItemTogglerShoppingCart;
    private RelativeLayout mItemInfoLayout;
    private View mViewBelowShoppingCart;
    private String mPictureUrl;
    private ImageView mItemImageView;

    public ShoppingCartViewController(Activity activity, MenuItem toggler, String pictureUri, String purchaseTitle, BigDecimal amount, String currencyId, Boolean startShowingItemInfo, View viewBelowShoppingCart) {
        mMenuItemTogglerShoppingCart = toggler;
        initialize(activity, pictureUri, purchaseTitle, amount, currencyId, startShowingItemInfo, viewBelowShoppingCart);
        start();
    }
    public ShoppingCartViewController(Activity activity, ImageView toggler, String pictureUri, String purchaseTitle, BigDecimal amount, String currencyId, Boolean startShowingItemInfo, View viewBelowShoppingCart) {
        mImageViewTogglerShoppingCart = toggler;
        initialize(activity, pictureUri, purchaseTitle, amount, currencyId, startShowingItemInfo, viewBelowShoppingCart);
        start();

    }
    private void initialize(Activity activity, String pictureUri, String purchaseTitle, BigDecimal amount, String currencyId, Boolean startShowingItemInfo, View viewBelowShoppingCart) {
        mActivity = activity;
        mStartShowingItemInfo = startShowingItemInfo;
        mItemInfoLayout = (RelativeLayout) mActivity.findViewById(R.id.itemInfoLayout);
        mItemImageView = (ImageView) mActivity.findViewById(R.id.itemImage);
        mViewBelowShoppingCart = viewBelowShoppingCart;
        mPictureUrl = pictureUri;

        MPTextView itemDescriptionTextView = (MPTextView) mActivity.findViewById(R.id.itemTitle);
        MPTextView itemAmountTextView = (MPTextView) mActivity.findViewById(R.id.itemAmount);
        itemDescriptionTextView.setText(purchaseTitle);
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
            showItemInfo(false);
        }
        else {
            hideItemInfo();
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    public Spanned getAmountLabel(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.formatNumber(amount, currencyId, true, true);
    }

    public void toggle(boolean withAnimation) {
        if(!isItemShown()) {
            showItemInfo(withAnimation);
        }
        else {
            hideItemInfo();
        }
    }
    public boolean isItemShown() {
        return this.mItemDescriptionShown;
    }

    public void hideItemInfo() {
        mItemInfoLayout.setVisibility(View.GONE);
        mItemDescriptionShown = false;
        if(mMenuItemTogglerShoppingCart != null) {
            mMenuItemTogglerShoppingCart.setIcon(R.drawable.icon_cart);
        } else if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.icon_cart);
            int dpAsPixels = ScaleUtil.getPxFromDp(8, mActivity);
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    public void showItemInfo(boolean enableAnimation) {
        mItemInfoLayout.setVisibility(View.VISIBLE);
        if(enableAnimation) {
            startAnimations();
        }
        mItemDescriptionShown = true;
        if(mMenuItemTogglerShoppingCart != null) {
            mMenuItemTogglerShoppingCart.setIcon(R.drawable.close);
        } else if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.close);

            int dpAsPixels = ScaleUtil.getPxFromDp(12, mActivity);
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        tintTogglerDrawableWithColor(mActivity.getResources().getColor(R.color.mpsdk_white));
    }

    private void startAnimations() {

        int shoppingCartHeight = mActivity.getResources().getDimensionPixelSize(R.dimen.mpsdk_shopping_cart_height);
        TranslateAnimation leavePlaceForShoppingCartAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF - shoppingCartHeight, 0);
        leavePlaceForShoppingCartAnimation.setDuration(mActivity.getResources().getInteger(android.R.integer.config_shortAnimTime));

        mItemInfoLayout.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.slide_up_to_down_in));
        mViewBelowShoppingCart.startAnimation(leavePlaceForShoppingCartAnimation);
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
        if(mMenuItemTogglerShoppingCart != null) {
            togglerDrawable = mMenuItemTogglerShoppingCart.getIcon();
        }
        else if(mImageViewTogglerShoppingCart != null) {
            togglerDrawable = mImageViewTogglerShoppingCart.getDrawable();
        }
        return togglerDrawable;
    }
}
