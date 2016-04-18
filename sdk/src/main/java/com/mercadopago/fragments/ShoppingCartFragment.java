package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
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

    private ImageView mImageViewTogglerShoppingCart;
    private ImageView mItemImageView;
    private MPTextView itemDescriptionTextView;
    private MPTextView itemAmountTextView;
    private View mViewBelow;

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
        itemDescriptionTextView.setText(mPurchaseTitle);
        itemAmountTextView.setText(getAmountLabel(mAmount, mCurrencyId));
        setItemImage();
    }

    protected String getFormattedPurchaseTitle() {
        if(mPurchaseTitle != null) {
            if (mPurchaseTitle.length() > PURCHASE_TITLE_MAX_LENGTH) {
                mPurchaseTitle = mPurchaseTitle.substring(0, PURCHASE_TITLE_MAX_LENGTH-1);
                mPurchaseTitle = mPurchaseTitle + "…";
            }
            return mPurchaseTitle;
        }
        else {
            return mPurchaseTitle;
        }
    }

    private void setItemImage() {
        if(mPictureUrl != null && !mPictureUrl.isEmpty()) {
            Picasso.with(getContext()).load(mPictureUrl).into(mItemImageView);
        }
        else
        {
            int dpAsPixels = ScaleUtil.getPxFromDp(24, getContext());
            mItemImageView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
    }

    public Spanned getAmountLabel(BigDecimal amount, String currencyId) {
        return CurrenciesUtil.formatNumber(amount, currencyId, true, true);
    }

    public void setToggler(ImageView toggler) {
        mImageViewTogglerShoppingCart = toggler;
        mImageViewTogglerShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
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
        return getView().isShown();
    }

    public void showItemInfo() {
        if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.close);

            int dpAsPixels = ScaleUtil.getPxFromDp(12, getContext());
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_to_down_in, R.anim.slide_down_to_top_out)
                    .show(this)
                    .commit();

            animateViewBelowIn();
        }
    }

    private void animateViewBelowIn() {

        int shoppingCartHeight = getContext().getResources().getDimensionPixelSize(R.dimen.mpsdk_shopping_cart_height);
        TranslateAnimation leavePlaceForShoppingCartAnimation =
                new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                        Animation.ABSOLUTE, -shoppingCartHeight, Animation.ABSOLUTE, 0);
        leavePlaceForShoppingCartAnimation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        mViewBelow.startAnimation(leavePlaceForShoppingCartAnimation);
    }

    public void hideItemInfo() {
        if(mImageViewTogglerShoppingCart != null) {
            mImageViewTogglerShoppingCart.setImageResource(R.drawable.icon_cart);
            int dpAsPixels = ScaleUtil.getPxFromDp(8, getContext());
            mImageViewTogglerShoppingCart.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

            Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_to_top_out);

            final Fragment fragmentToHide = this;
            hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_down_to_top_out, R.anim.slide_down_to_top_out)
                            .replace(R.id.shoppingCartFragment, fragmentToHide)
                            .hide(fragmentToHide)
                            .commit();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            getView().startAnimation(hideAnimation);
        }
    }

    private void animateViewBelowOut() {
        int shoppingCartHeight = getContext().getResources().getDimensionPixelSize(R.dimen.mpsdk_shopping_cart_height);
        TranslateAnimation collapseShoppingCartAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, 0, -shoppingCartHeight);

        collapseShoppingCartAnimation.setDuration(750);
        mViewBelow.startAnimation(collapseShoppingCartAnimation);
    }

    public void setViewBelow(View viewBelow) {
        mViewBelow = viewBelow;
    }
}
