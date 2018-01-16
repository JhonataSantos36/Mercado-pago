package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.InstallmentsUtil;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentOnView extends Reviewable {

    public static final String CFT = "CFT ";
    protected View mView;
    protected ImageView mPaymentImage;
    protected MPTextView mPaymentText;
    protected MPTextView mPaymentDescription;
    protected MPTextView mChangePaymentTextView;
    protected MPTextView mCFTTextView;
    private MPTextView mNoInstallmentsRateTextView;
    protected FrameLayout mChangePaymentButton;
    protected FrameLayout mPayerCostContainer;
    protected ImageView mIconTimeImageView;

    protected Context mContext;
    protected PayerCostViewController mPayerCostViewController;

    protected String mCurrency;
    protected PayerCost mPayerCost;
    protected CardInfo mCardInfo;
    protected PaymentMethod mPaymentMethod;
    protected OnReviewChange mCallback;
    protected Boolean mEditionEnabled;
    private final Site mSite;


    public ReviewPaymentOnView(Context context, PaymentMethod paymentMethod, CardInfo cardInfo, PayerCost payerCost,
                               Site site, OnReviewChange callback, Boolean editionEnabled) {

        this.mContext = context;
        this.mCallback = callback;
        this.mPaymentMethod = paymentMethod;
        this.mCardInfo = cardInfo;
        this.mPayerCost = payerCost;
        this.mSite = site;
        this.mCurrency = site.getCurrencyId();
        this.mEditionEnabled = editionEnabled == null ? true : editionEnabled;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_adapter_review_payment, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mPaymentImage = (ImageView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentImage);
        mPaymentText = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentText);
        mPaymentDescription = (MPTextView) mView.findViewById(R.id.mpsdkAdapterReviewPaymentDescription);
        mChangePaymentButton = (FrameLayout) mView.findViewById(R.id.mpsdkAdapterReviewPaymentChangeButton);
        mPayerCostContainer = (FrameLayout) mView.findViewById(R.id.mpsdkAdapterReviewPayerCostContainer);
        mIconTimeImageView = (ImageView) mView.findViewById(R.id.mpsdkIconTime);
        mChangePaymentTextView = (MPTextView) mView.findViewById(R.id.mpsdkReviewChangePaymentText);
        mIconTimeImageView.setVisibility(View.GONE);
        mCFTTextView = (MPTextView) mView.findViewById(R.id.mpsdkCFT);
        mChangePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeSelected();
            }
        });
        if (mEditionEnabled) {
            mChangePaymentButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void draw() {
        setIcon();

        if (mPayerCost.getInstallments() != 1) {
            mPaymentText.setVisibility(View.GONE);

            mPayerCostViewController = new PayerCostColumn(mContext, mSite);
            mPayerCostViewController.inflateInParent(mPayerCostContainer, true);
            mPayerCostViewController.initializeControls();
            mPayerCostViewController.drawPayerCost(mPayerCost);

            showFinance();
            showSiteRelatedInformation();

        } else {
            mPayerCostContainer.setVisibility(View.GONE);
            mCFTTextView.setVisibility(View.GONE);

            Spanned amountText = CurrenciesUtil.getFormattedAmount(mPayerCost.getTotalAmount(), mCurrency);
            mPaymentText.setText(amountText);
        }

        String description = mContext.getString(R.string.mpsdk_review_description_card, mPaymentMethod.getName(),
                mCardInfo.getLastFourDigits());

        mPaymentDescription.setText(description);
    }

    private void showSiteRelatedInformation() {
        if (InstallmentsUtil.shouldWarnAboutBankInterests(mSite)) {
            warnAboutBankInterests();
        }
    }

    private void warnAboutBankInterests() {
        mNoInstallmentsRateTextView = (MPTextView) mView.findViewById(R.id.mpsdkNoInstallmentsRateTextView);
        mNoInstallmentsRateTextView.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView.setText(R.string.mpsdk_interest_label);
    }

    private void setIcon() {
        mPaymentImage.setImageResource(R.drawable.mpsdk_review_payment_on);
    }

    private void showFinance() {
        if (mPayerCost.hasCFT()) {
            mCFTTextView.setVisibility(View.VISIBLE);
            mCFTTextView.setText(CFT + mPayerCost.getCFTPercent());
        }
    }

    @Override
    public String getKey() {
        return ReviewKeys.PAYMENT_METHODS;
    }
}
