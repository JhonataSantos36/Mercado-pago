package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewController;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewPaymentViewFactory;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentAdapter extends RecyclerView.Adapter<ReviewPaymentAdapter.ViewHolder> {

    private Context mContext;
    private List<PaymentMethod> mPaymentMethodList;
    private List<CardInfo> mCardInfoList;
    private List<PayerCost> mPayerCostList;
    private List<String> mCurrenciesList;
    private List<BigDecimal> mTotalAmountList;
    private List<PaymentMethodSearchItem> mPaymentMethodSearchItemList;
    private Site mSite;
    private OnChangePaymentMethodCallback mCallback;
    private boolean isUniquePaymentMethod;
    private DecorationPreference mDecorationPreference;

    public ReviewPaymentAdapter(Context context, List<PaymentMethod> paymentMethodList, List<CardInfo> cardInfoList,
                                List<PayerCost> payerCosts, List<String> currencies, List<BigDecimal> totalAmounts,
                                List<PaymentMethodSearchItem> paymentMethodSearchItems, Site site,
                                OnChangePaymentMethodCallback callback, boolean uniquePaymentMethod,
                                DecorationPreference decorationPreference) {
        this.mContext = context;
        this.mPaymentMethodList = paymentMethodList;
        this.mCardInfoList = cardInfoList;
        this.mPayerCostList = payerCosts;
        this.mCurrenciesList = currencies;
        this.mTotalAmountList = totalAmounts;
        this.mPaymentMethodSearchItemList = paymentMethodSearchItems;
        this.mSite = site;
        this.mCallback = callback;
        this.isUniquePaymentMethod = uniquePaymentMethod;
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public ReviewPaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        PaymentMethod paymentMethod = mPaymentMethodList.get(position);
        CardInfo cardInfo = mCardInfoList.get(position);
        BigDecimal amount = mTotalAmountList.get(position);
        PaymentMethodSearchItem item = mPaymentMethodSearchItemList.get(position);
        String currencyId = mCurrenciesList.get(position);
        PayerCost payerCost = mPayerCostList.get(position);
        ReviewPaymentViewController paymentViewController = null;
        if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId())) {
            paymentViewController = ReviewPaymentViewFactory.getReviewPaymentMethodOnViewController(mContext,
                    paymentMethod, cardInfo, currencyId, payerCost, mCallback, isUniquePaymentMethod,
                    mDecorationPreference);
            paymentViewController.inflateInParent(parent, false);
        } else {
            paymentViewController = ReviewPaymentViewFactory.getReviewPaymentMethodOffViewController(mContext,
                    paymentMethod, amount, item, currencyId, mSite, mCallback, isUniquePaymentMethod,
                    mDecorationPreference);
            paymentViewController.inflateInParent(parent, false);
        }

        return new ReviewPaymentAdapter.ViewHolder(paymentViewController);
    }


    @Override
    public int getItemCount() {
        return mPaymentMethodList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mReviewPaymentViewController.drawPaymentMethod();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ReviewPaymentViewController mReviewPaymentViewController;

        public ViewHolder(ReviewPaymentViewController reviewPaymentViewController) {
            super(reviewPaymentViewController.getView());
            mReviewPaymentViewController = reviewPaymentViewController;
            mReviewPaymentViewController.initializeControls();
        }
    }
}
