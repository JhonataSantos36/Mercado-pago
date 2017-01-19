package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewFactory;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewOffController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 12/6/16.
 */

public class ReviewPaymentOffAdapter extends RecyclerView.Adapter<ReviewPaymentOffAdapter.ViewHolder> {

    private Context mContext;
    private List<PaymentMethod> mPaymentMethodList;
    private List<BigDecimal> mTotalAmountList;
    private List<PaymentMethodSearchItem> mPaymentMethodSearchItemList;
    private Site mSite;
    private OnChangePaymentMethodCallback mCallback;
    private boolean isUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;
    private String mCurrency;

    public ReviewPaymentOffAdapter(Context context, List<PaymentMethod> paymentMethodList, String currency,
                                   List<BigDecimal> totalAmounts, List<PaymentMethodSearchItem> paymentMethodSearchItems,
                                   Site site, OnChangePaymentMethodCallback callback, boolean uniquePaymentMethod,
                                   DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethodList = paymentMethodList;
        this.mCurrency = currency;
        this.mTotalAmountList = totalAmounts;
        this.mPaymentMethodSearchItemList = paymentMethodSearchItems;
        this.mSite = site;
        this.mCallback = callback;
        this.isUniquePaymentMethod = uniquePaymentMethod;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public ReviewPaymentOffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        ReviewPaymentViewOffController paymentViewController = ReviewPaymentViewFactory.getReviewPaymentMethodOffViewController(
                mContext, mSite, mCallback, isUniquePaymentMethod, mDecorationPreference);
        paymentViewController.inflateInParent(parent, false);

        return new ReviewPaymentOffAdapter.ViewHolder(paymentViewController);
    }


    @Override
    public int getItemCount() {
        return mPaymentMethodList.size();
    }

    @Override
    public void onBindViewHolder(ReviewPaymentOffAdapter.ViewHolder holder, int position) {
        PaymentMethod paymentMethod = mPaymentMethodList.get(position);
        BigDecimal amount = mTotalAmountList.get(position);
        PaymentMethodSearchItem item = mPaymentMethodSearchItemList.get(position);
        holder.mReviewPaymentViewOffController.drawPaymentMethod(paymentMethod, amount, item, mCurrency);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ReviewPaymentViewOffController mReviewPaymentViewOffController;

        public ViewHolder(ReviewPaymentViewOffController reviewPaymentViewOffController) {
            super(reviewPaymentViewOffController.getView());
            mReviewPaymentViewOffController = reviewPaymentViewOffController;
            mReviewPaymentViewOffController.initializeControls();
        }
    }
}
