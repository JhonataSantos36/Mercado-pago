package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.model.CardInfo;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewFactory;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewOnController;

import java.util.List;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOnAdapter extends RecyclerView.Adapter<ReviewPaymentOnAdapter.ViewHolder> {

    private Context mContext;
    private List<PaymentMethod> mPaymentMethodList;
    private List<CardInfo> mCardInfoList;
    private List<PayerCost> mPayerCostList;
    private OnChangePaymentMethodCallback mCallback;
    private boolean isUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;
    private String mCurrency;

    public ReviewPaymentOnAdapter(Context context, List<PaymentMethod> paymentMethodList, List<CardInfo> cardInfoList,
                                  List<PayerCost> payerCosts, String currency,
                                  OnChangePaymentMethodCallback callback, boolean uniquePaymentMethod,
                                  DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethodList = paymentMethodList;
        this.mCardInfoList = cardInfoList;
        this.mPayerCostList = payerCosts;
        this.mCurrency = currency;
        this.mCallback = callback;
        this.isUniquePaymentMethod = uniquePaymentMethod;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public ReviewPaymentOnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        ReviewPaymentViewOnController paymentViewController = ReviewPaymentViewFactory.getReviewPaymentMethodOnViewController(
                mContext, mCallback, isUniquePaymentMethod, mDecorationPreference);
        paymentViewController.inflateInParent(parent, false);

        return new ReviewPaymentOnAdapter.ViewHolder(paymentViewController);
    }


    @Override
    public int getItemCount() {
        return mPaymentMethodList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethod paymentMethod = mPaymentMethodList.get(position);
        CardInfo cardInfo = null;
        if (!mCardInfoList.isEmpty()) {
            cardInfo = mCardInfoList.get(position);
        }
        PayerCost payerCost = null;
        if (!mPayerCostList.isEmpty()) {
            payerCost = mPayerCostList.get(position);
        }
        holder.mReviewPaymentViewOnController.drawPaymentMethod(paymentMethod, cardInfo, payerCost, mCurrency);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ReviewPaymentViewOnController mReviewPaymentViewOnController;

        public ViewHolder(ReviewPaymentViewOnController reviewPaymentViewOnController) {
            super(reviewPaymentViewOnController.getView());
            mReviewPaymentViewOnController = reviewPaymentViewOnController;
            mReviewPaymentViewOnController.initializeControls();
        }
    }
}
